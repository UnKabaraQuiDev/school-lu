package lu.kbra.school_lu.db.type;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.Type;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Rectangle2DFloatArrayColumnType implements ColumnType<Rectangle2D.Float[], String[]> {

	private final EncodingType<String[]> encodingType;

	public Rectangle2DFloatArrayColumnType(final int dimensionCount) {
		this.encodingType = new Rectangle2DFloatArrayEncodingType(dimensionCount);
	}

	@Override
	public Rectangle2D.Float[] decode(final String[] value, final Type type) {
		if (value == null) {
			return null;
		}

		final Rectangle2D.Float[] result = new Rectangle2D.Float[value.length];

		for (int i = 0; i < value.length; i++) {
			result[i] = Rectangle2DFloatEncodingType.decode(value[i]);
		}

		return result;
	}

	@Override
	public String[] encode(final Rectangle2D.Float[] value) {
		if (value == null) {
			return null;
		}

		final String[] result = new String[value.length];

		for (int i = 0; i < value.length; i++) {
			result[i] = Rectangle2DFloatEncodingType.encode(value[i]);
		}

		return result;
	}

}
