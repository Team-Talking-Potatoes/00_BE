package potatoes.server.travel.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import potatoes.server.travel.model.TravelModel;

public record CreateTravelRequest(
	@NotBlank(message = "여행 이름를 입력해주세요.")
	String travelName,

	@NotNull(message = "예상 여행 경비를 입력해주세요.")
	@Positive
	Integer expectedTripCost,

	@NotNull(message = "최소 인원 입력해주세요.")
	@Positive
	Integer minTravelMateCount,

	@NotNull(message = "최대 인원 입력해주세요.")
	@Positive(message = "올바른 값을 입력해주세요.")
	Integer maxTravelMateCount,

	@NotBlank(message = "여행 소개를 입력해주세요.")
	String travelDescription,

	@NotNull(message = "여행 사진을 추가해주세요.")
	MultipartFile travelImage,

	@NotBlank(message = "해시태그를 추가해주세요. (최대 5개)")
	String hashTags,

	@NotNull(message = "국내여행/해외여행 여부를 체크해주세요.")
	Boolean isDomestic,

	@NotBlank(message = "여행 진행 장소를 입력해주세요.")
	String travelLocation,

	String departureLocation,

	@NotNull(message = "여행 시작 시간 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime startAt,

	@NotNull(message = "여행 종료 시간 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime endAt,

	@NotNull(message = "마감 종료 시간 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	LocalDateTime registrationEnd,

	@Valid
	List<DetailTravelRequest> detailTravel
) {
	public static TravelModel toModel(CreateTravelRequest request) {
		return new TravelModel(
			request.travelName(),
			request.travelDescription(),
			null,
			request.expectedTripCost(),
			request.minTravelMateCount(),
			request.maxTravelMateCount(),
			request.hashTags(),
			request.isDomestic(),
			request.travelLocation(),
			request.departureLocation(),
			request.startAt().toInstant(ZoneOffset.UTC),
			request.endAt().toInstant(ZoneOffset.UTC),
			request.registrationEnd().toInstant(ZoneOffset.UTC),
			calculateTripDuration(request.startAt, request.endAt)
		);
	}

	private static int calculateTripDuration(LocalDateTime startAt, LocalDateTime endAt) {
		Duration duration = Duration.between(startAt, endAt);
		return (int)duration.toDays();
	}
}

