package potatoes.server.utils.pagination;

public interface Paginator {
	int getOffset();

	int getLimit();

	String getSortBy();

	String getSortOrder();
}
