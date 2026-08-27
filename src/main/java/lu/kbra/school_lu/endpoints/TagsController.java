package lu.kbra.school_lu.endpoints;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.school_lu.data.UserId;
import lu.kbra.school_lu.data.UserPermissionType;
import lu.kbra.school_lu.db.data.TagData;
import lu.kbra.school_lu.db.table.TagTable;
import lu.kbra.school_lu.service.UserPermissionService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagsController {

	private final TagTable tagTable;
	private final UserPermissionService userPermissionService;

	public record TagRequest(@NotBlank @Size(max = 100) String name, int color) {
	}

	@GetMapping("/list")
	public List<TagData> list() {
		return this.tagTable.loadAll();
	}

	@GetMapping("/{id}")
	public TagData get(@PathVariable final Long id) {
		return this.tagTable.load(new TagData(id));
	}

	@PostMapping("/new")
	public TagData create(@AuthenticationPrincipal final UserId userId, @Valid @RequestBody final TagRequest request) {
		this.userPermissionService.requireAllPermissions(userId, UserPermissionType.MANAGE_TAG);

		final TagData tag = new TagData(request.name());
		tag.setColor(request.color());

		return this.tagTable.insertAndReload(tag);
	}

	@PatchMapping("/{id}")
	public TagData update(
			@AuthenticationPrincipal final UserId userId,
			@PathVariable final Long id,
			@Valid @RequestBody final TagRequest request) {
		this.userPermissionService.requireAnyPermission(userId, UserPermissionType.EDIT_TAG, UserPermissionType.MANAGE_TAG);

		final TagData tag = new TagData(id);
		tag.setName(request.name());
		tag.setColor(request.color());

		return this.tagTable.updateAndReload(tag);
	}

	@DeleteMapping("/{id}")
	public void delete(@AuthenticationPrincipal final UserId userId, @PathVariable final Long id) {
		this.userPermissionService.requireAllPermissions(userId, UserPermissionType.MANAGE_TAG);

		this.tagTable.delete(new TagData(id));
	}

}
