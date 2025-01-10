package potatoes.server.utils;

public record CommonResponse<T>(String status, T data) {
	private static final String SUCCESS_STATUS = "success";

	public static <T> CommonResponse<T> from(T data) {
		return new CommonResponse<T>(SUCCESS_STATUS, data);
	}

	public static CommonResponse<?> create() {
		return new CommonResponse<>(SUCCESS_STATUS, null);
	}
}
