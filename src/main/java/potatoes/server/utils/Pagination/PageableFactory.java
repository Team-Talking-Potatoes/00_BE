package potatoes.server.utils.Pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PageableFactory {
	public Pageable create(Paginator request) {
		return PageRequest.of(calculatePageNumber(request), request.getLimit(), createSort(request));
	}

	private int calculatePageNumber(Paginator request) {
		return request.getOffset() / request.getLimit();
	}

	private Sort createSort(Paginator request) {
		return Sort.by(determineDirection(request.getSortOrder()), request.getSortBy());
	}

	private Sort.Direction determineDirection(String sortOrder) {
		return sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
	}
}
