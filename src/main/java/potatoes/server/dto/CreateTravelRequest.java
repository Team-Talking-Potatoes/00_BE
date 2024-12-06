package potatoes.server.dto;

import java.time.Instant;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

	@NotNull(message = "여행 시작 시간을 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	Instant startAt,

	@NotNull(message = "여행 종료 시간을 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	Instant endAt,

	@NotNull(message = "여행 진행기간을 입력해주세요.")
	@Positive(message = "올바른 값을 입력해주세요.")
	Integer tripDuration,

	@NotEmpty(message = "여행 상세일정을 입력해주세요.")
	@Valid
	List<DetailTravelRequest> detailTravel
) {
	public record DetailTravelRequest(
		@NotNull(message = "상세일정 날을 입력해주세요.")
		@Positive(message = "올바른 값을 입력해주세요.")
		Integer tripDay,

		@NotNull(message = "몇번째 일정인지 입력해주세요.")
		@Positive(message = "올바른 값을 입력해주세요.")
		Integer tripOrderNumber,

		@NotBlank(message = "상세 일정 목적지 입력해주세요.")
		String destination,

		@NotBlank(message = "상세 일정 설명을 입력해주세요.")
		String description,

		@NotNull(message = "상세 일정 사진을 입력해주세요.")
		MultipartFile destinationImage
	) {
	}
}
