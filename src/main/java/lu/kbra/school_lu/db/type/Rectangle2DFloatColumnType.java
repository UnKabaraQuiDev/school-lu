package lu.kbra.school_lu.db.type;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Type;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;

import lombok.Getter;

@Getter
public class Rectangle2DFloatColumnType implements ColumnType<Rectangle2D.Float, Rectangle2D.Float> {

	private final EncodingType<Rectangle2D.Float> encodingType;

	public Rectangle2DFloatColumnType(SQLEncodingTypeProvider etp) {
		encodingType = etp.getTypeFor(Rectangle2D.Float.class);
	}

	@Override
	public Rectangle2D.Float decode(final Rectangle2D.Float value, final Type type) {
		return value;
	}

	@Override
	public Rectangle2D.Float encode(final Rectangle2D.Float value) {
		return value;
	}

}
