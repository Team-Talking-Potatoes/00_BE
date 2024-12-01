package potatoes.server.utils.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

@Component
public class DateTimeUtils {

	public static Instant getStartOfDay(String dateStr) {
		if (dateStr == null) {
			return null;
		}
		LocalDate date = LocalDate.parse(dateStr);
		return date.atStartOfDay(ZoneOffset.UTC).toInstant();
	}

	public static Instant getEndOfDay(String dateStr) {
		if (dateStr == null) {
			return null;
		}
		LocalDate date = LocalDate.parse(dateStr);
		return date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
	}
}
