package potatoes.server.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTravelRequest {
	@NotBlank(message = "여행 이름를 입력해주세요.")
	private String travelName;

	@NotNull(message = "예상 여행 경비를 입력해주세요.")
	@Positive
	private Integer expectedTripCost;

	@NotNull(message = "최소 인원 입력해주세요.")
	@Positive
	private Integer minTravelMateCount;

	@NotNull(message = "최대 인원 입력해주세요.")
	@Positive(message = "올바른 값을 입력해주세요.")
	private Integer maxTravelMateCount;

	@NotBlank(message = "여행 소개를 입력해주세요.")
	private String travelDescription;

	@NotNull(message = "여행 사진을 추가해주세요.")
	private MultipartFile travelImage;

	@NotBlank(message = "해시태그를 추가해주세요. (최대 5개)")
	private String hashTags;

	@NotNull(message = "국내여행/해외여행 여부를 체크해주세요.")
	private Boolean isDomestic;

	@NotBlank(message = "여행 진행 장소를 입력해주세요.")
	private String travelLocation;

	private String departureLocation;

	@NotNull(message = "여행 시작 시간 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime startAt;

	@NotNull(message = "여행 종료 시간 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime endAt;

	@NotNull(message = "마감 종료 시간 정해주세요.")
	@Future(message = "선택할 수 없는 날짜입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime registrationEnd;

	@Valid
	private List<DetailTravelRequest> detailTravel;

	@Getter
	@Setter
	public static class DetailTravelRequest {
		@NotNull(message = "상세일정 날을 입력해주세요.")
		@Positive(message = "올바른 값을 입력해주세요.")
		private Integer tripDay;

		@NotNull(message = "몇번째 일정인지 입력해주세요.")
		@Positive(message = "올바른 값을 입력해주세요.")
		private Integer tripOrderNumber;

		@NotBlank(message = "상세 일정 목적지 입력해주세요.")
		private String destination;

		@NotBlank(message = "상세 일정 설명을 입력해주세요.")
		private String description;

		@NotNull(message = "상세 일정 사진을 입력해주세요.")
		private MultipartFile destinationImage;
	}
}