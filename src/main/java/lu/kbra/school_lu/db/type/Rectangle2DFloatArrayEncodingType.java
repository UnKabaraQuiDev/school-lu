package lu.kbra.school_lu.db.type;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.postgres.encoding.array.ArrayEncodingType;

import lombok.Getter;

@Getter
public class Rectangle2DFloatArrayEncodingType implements ArrayEncodingType<String[]> {

	private final int dimensionCount;

	public Rectangle2DFloatArrayEncodingType(final int dimensionCount) {
		this.dimensionCount = dimensionCount;
	}

	@Override
	public String[] getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		final Array array = rs.getArray(columnIndex);

		if (array == null) {
			return null;
		}

		final Object[] values = (Object[]) array.getArray();
		final String[] result = new String[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = values[i] == null ? null : values[i].toString();
		}

		return result;
	}

	@Override
	public String[] getObject(final ResultSet rs, final String columnName) throws SQLException {
		final Array array = rs.getArray(columnName);

		if (array == null) {
			return null;
		}

		final Object[] values = (Object[]) array.getArray();
		final String[] result = new String[values.length];

		for (int i = 0; i < values.length; i++) {
			result[i] = values[i] == null ? null : values[i].toString();
		}

		return result;
	}

	@Override
	public int getSQLType() {
		return Types.ARRAY;
	}

	@Override
	public String getRawTypeName() {
		return "BOX";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final String[] value) throws SQLException {
		if (value == null) {
			stmt.setNull(index, Types.ARRAY);
			return;
		}

		stmt.setArray(index, stmt.getConnection().createArrayOf("BOX", (String[]) value));
	}

}
