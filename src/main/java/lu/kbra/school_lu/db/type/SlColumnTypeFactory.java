package lu.kbra.school_lu.db.type;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.type.factory.DatabaseColumnTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

@Component
public class SlColumnTypeFactory implements DatabaseColumnTypeFactory {

	@Override
	public void registerColumnTypes(List<ColumnTypeFactory<?>> typeMap) {
		ColumnTypeRegistry.registerType(Rectangle2DFloatColumnType.class,
				(clazz, hints, etp) -> clazz == Rectangle2D.Float.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, hints, etp) -> new Rectangle2DFloatColumnType(etp),
				typeMap);

		ColumnTypeRegistry.registerType(Rectangle2DFloatArrayColumnType.class,
				(clazz, map, etp) -> clazz.isArray() && PCUtils.getComponentType(clazz) == Rectangle2D.Float.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new Rectangle2DFloatArrayColumnType(PCUtils.getArrayDimension(type.get().getType())),
				typeMap);
	}

	@Override
	public boolean matches(String protocol) {
		return protocol.equals(PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
