package potatoes.server.utils;

import java.security.SecureRandom;

public class GenerateRandomNumber {
	private static final SecureRandom secureRandom = new SecureRandom();
	private static final int LENGTH = 6;
	private static final int BOUND = 10;

	public static String generateNumber() {
		StringBuilder builder = new StringBuilder(LENGTH);

		builder.append(secureRandom.nextInt(9) + 1);

		for (int i = 1; i < LENGTH; i++) {
			builder.append(secureRandom.nextInt(BOUND));
		}

		return builder.toString();
	}
}
