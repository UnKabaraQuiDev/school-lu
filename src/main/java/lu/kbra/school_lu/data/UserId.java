package lu.kbra.school_lu.data;

import java.util.Objects;

public record UserId(Long id) {

	public UserId {
		Objects.requireNonNull(id, "id");
	}

}