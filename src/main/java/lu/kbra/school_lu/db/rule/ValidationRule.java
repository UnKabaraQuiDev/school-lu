package lu.kbra.school_lu.db.rule;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.PropertyConstraintMappingContext;
import org.hibernate.validator.cfg.context.TypeConstraintMappingContext;
import org.hibernate.validator.cfg.defs.NotNullDef;
import org.hibernate.validator.cfg.defs.SizeDef;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.FieldStorageBinding;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.InsertRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.PrepareRule;
import lu.kbra.pclib.db.utils.impl.SQLQueryableRule.UpdateRule;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidationRule implements PrepareRule, InsertRule, UpdateRule {

	public static final String SKIP_VALIDATION = "SKIP_VALIDATION";

	private final Map<TableStructure, Validator> validatorCache = new HashMap<>();

	@Override
	public void executePrepare(final RuleHookType hookType, final SQLQueryable<?> queryable, final Connection c, final Object data) {
		if (queryable.getStructure().getBooleanHint(SKIP_VALIDATION)) {
			return;
		}

		final Validator validator = validatorCache.computeIfAbsent((TableStructure) queryable.getStructure(), this::createValidator);

		if (data instanceof Collection<?> col) {
			for (final Object entry : col) {
				validate(validator, entry);
			}
		} else {
			validate(validator, data);
		}
	}

	private void validate(final Validator validator, final Object data) {
		final Set<ConstraintViolation<Object>> violations = validator.validate(data);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

	@Override
	public boolean shouldRun(final RuleHookType hookType, final SQLQueryable<?> queryable) {
		return hookType.isPrepare() && (hookType.isInsert() || hookType.isUpdate());
	}

	public Validator createValidator(final TableStructure structure) {

		final HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class).configure();
		final ConstraintMapping mapping = configuration.createConstraintMapping();
		final TypeConstraintMappingContext<? extends DatabaseEntry> typeMapping = mapping.type(structure.getEntryClass());

		for (final ColumnData column : structure.getColumns()) {
			if (!(column.getStorageBinding() instanceof final FieldStorageBinding binding)) {
				return null;
			}

			final String fieldName = binding.getField().getName();
			final PropertyConstraintMappingContext fieldMapping = typeMapping.field(fieldName);

			Object maxLength = column.getTypeHint(DefaultTypeHints.MAX_LENGTH);
			if (maxLength == null) {
				maxLength = column.getTypeHint(DefaultTypeHints.FIXED_LENGTH);
			}
			if (maxLength != null) {
				fieldMapping.constraint(new SizeDef().max((Integer) maxLength));
			}

			final boolean nullable = column.getBooleanHint(DefaultColumnHints.NULLABLE) || column.hasDefaultValue()
					|| column.isAutoIncrement();
			if (!nullable) {
				fieldMapping.constraint(new NotNullDef());
			}
		}

		return configuration.addMapping(mapping).buildValidatorFactory().getValidator();
	}

}
