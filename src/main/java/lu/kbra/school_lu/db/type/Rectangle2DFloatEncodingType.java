package lu.kbra.school_lu.db.type;

import java.awt.geom.Rectangle2D;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.EncodingType.FixedEncodingType;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Rectangle2DFloatEncodingType implements FixedEncodingType<Rectangle2D.Float> {

	@Override
	public Rectangle2D.Float getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return this.decode(rs.getString(columnIndex));
	}

	@Override
	public Rectangle2D.Float getObject(final ResultSet rs, final String columnName) throws SQLException {
		return this.decode(rs.getString(columnName));
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Rectangle2D.Float value) throws SQLException {
		stmt.setObject(index, this.encode(value), Types.OTHER);
	}

	@Override
	public int getSQLType() {
		return Types.OTHER;
	}

	@Override
	public String getTypeName() {
		return "BOX";
	}

	public static String encode(final Rectangle2D.Float rectangle) {
		final float x1 = rectangle.x;
		final float y1 = rectangle.y;
		final float x2 = rectangle.x + rectangle.width;
		final float y2 = rectangle.y + rectangle.height;

		return "((%s,%s),(%s,%s))".formatted(x1, y1, x2, y2);
	}

	public static Rectangle2D.Float decode(final String value) {
		final String normalized = value.replace("(", "").replace(")", "");

		final String[] parts = normalized.split(",");

		if (parts.length != 4) {
			throw new IllegalArgumentException("Invalid PostgreSQL BOX value: " + value);
		}

		final float x1 = Float.parseFloat(parts[0].trim());
		final float y1 = Float.parseFloat(parts[1].trim());
		final float x2 = Float.parseFloat(parts[2].trim());
		final float y2 = Float.parseFloat(parts[3].trim());

		final float x = Math.min(x1, x2);
		final float y = Math.min(y1, y2);
		final float width = Math.abs(x2 - x1);
		final float height = Math.abs(y2 - y1);

		return new Rectangle2D.Float(x, y, width, height);
	}

}
