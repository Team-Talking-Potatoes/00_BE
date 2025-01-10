package potatoes.server.utils.pagination.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import potatoes.server.utils.pagination.PagePolymorphic;

public record PageResponse<T extends PagePolymorphic>(
	List<T> content,
	long total,
	int currentPage,
	boolean hasNext
) implements PagePolymorphic {
	public static <T extends PagePolymorphic> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.getTotalElements(),
			page.getNumber(),
			page.hasNext()
		);
	}
}
