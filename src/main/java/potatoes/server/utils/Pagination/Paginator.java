package potatoes.server.utils.Pagination;

public interface Paginator {
	int getOffset();

	int getLimit();

	String getSortBy();

	String getSortOrder();
}
