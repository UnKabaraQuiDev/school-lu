package lu.kbra.school_lu.db.type;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.type.factory.DatabaseEncodingTypeFactory;
import lu.kbra.pclib.db.utils.registry.EncodingTypeFactory;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Component
public class SlEncodingTypeFactory implements DatabaseEncodingTypeFactory {

	@Override
	public void registerEncodingTypes(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(Rectangle2DFloatEncodingType.class,
				Rectangle2D.Float.class,
				(clazz, map) -> clazz == Rectangle2D.Float.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new Rectangle2DFloatEncodingType(),
				typeMap);
	}

	@Override
	public boolean matches(String protocol) {
		return protocol.equals(PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME);
	}

}
