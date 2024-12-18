package potatoes.server.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import potatoes.server.utils.pagination.PagePolymorphic;

public record TravelPageResponse<T extends PagePolymorphic>(
	List<T> content,
	long total,
	int currentPage,
	boolean hasNext
) implements PagePolymorphic {
	public static <T extends PagePolymorphic> TravelPageResponse<T> from(Page<T> page) {
		return new TravelPageResponse<>(
			page.getContent(),
			page.getTotalElements(),
			page.getNumber(),
			page.hasNext()
		);
	}
}
