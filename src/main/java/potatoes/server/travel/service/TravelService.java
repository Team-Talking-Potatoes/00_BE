package potatoes.server.travel.service;

import static potatoes.server.utils.error.ErrorCode.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import potatoes.server.travel.bookmark.entity.Bookmark;
import potatoes.server.travel.bookmark.repository.BookmarkRepository;
import potatoes.server.travel.dto.CreateTravelRequest;
import potatoes.server.travel.dto.DetailTravelRequest;
import potatoes.server.travel.dto.ParticipantResponse;
import potatoes.server.travel.dto.SimpleTravelResponse;
import potatoes.server.travel.dto.TravelDetailResponse;
import potatoes.server.travel.dto.TravelPlanResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.factory.TravelFactory;
import potatoes.server.travel.model.TravelModel;
import potatoes.server.travel.model.TravelPlanModel;
import potatoes.server.travel.repository.TravelPlanRepository;
import potatoes.server.travel.repository.TravelRepository;
import potatoes.server.travel.repository.TravelUserRepository;
import potatoes.server.user.entity.User;
import potatoes.server.user.repository.UserRepository;
import potatoes.server.utils.constant.ParticipantRole;
import potatoes.server.utils.error.exception.WeGoException;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TravelService {

	private final UserRepository userRepository;
	private final TravelRepository travelRepository;
	private final TravelPlanRepository travelPlanRepository;
	private final TravelUserRepository travelUserRepository;
	private final BookmarkRepository bookmarkRepository;
	private final TravelFactory travelFactory;

	@Transactional
	public void createTravel(Long userId, CreateTravelRequest request) {
		User user = findUser(userId);

		TravelModel travelModel = CreateTravelRequest.toModel(request);
		Travel travel = travelFactory.createTravel(travelModel, request.travelImage());

		List<TravelPlanModel> travelPlan = DetailTravelRequest.toModels(request.detailTravel(), travel);
		travelFactory.createTravelPlans(travelPlan);
		travelFactory.createChat(travel, user);
		travelFactory.createOrganizer(travel, user);
	}

	public TravelDetailResponse getDetails(Long travelId, Optional<Long> userId) {
		Travel travel = findTravel(travelId);

		Boolean participationFlag = userId
			.map(uid -> travelUserRepository.existsByTravelIdAndUserId(travelId, uid))
			.orElse(null);

		Boolean bookmarkFlag = userId
			.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travelId))
			.orElse(null);

		List<TravelPlanResponse> travelPlanResponses = travelPlanRepository.findAllByTravel(travel).stream()
			.map(TravelPlanResponse::from)
			.toList();
		List<ParticipantResponse> participantResponses = travelUserRepository.findAllByTravel(travel).stream()
			.map(ParticipantResponse::from)
			.toList();
		return TravelDetailResponse.from(travel, travelPlanResponses, participantResponses, participationFlag,
			bookmarkFlag);
	}

	public List<SimpleTravelResponse> getPopularTravels(Optional<Long> userId) {
		return travelRepository.findTop8ByOrderByIdDesc()
			.stream()
			.map(travel -> {
				int currentTravelMate = (int)travelUserRepository.countByTravel(travel);

				Boolean isBookmark = userId
					.map(uid -> bookmarkRepository.existsByUserIdAndTravelId(uid, travel.getId()))
					.orElse(null);

				return SimpleTravelResponse.from(travel, currentTravelMate, isBookmark);
			})
			.collect(Collectors.toList());
	}

	@Transactional
	public void participateInTravel(Long travelId, Long userId) {
		User user = findUser(userId);
		Travel travel = findTravel(travelId);

		boolean existsCheckFlag = travelUserRepository.existsByTravelIdAndUserId(travelId, userId);

		if (existsCheckFlag) {
			throw new WeGoException(ALREADY_PARTICIPATED_TRAVEL);
		}

		TravelUser travelUser = TravelUser.builder()
			.role(ParticipantRole.ATTENDEE)
			.user(user)
			.travel(travel)
			.build();

		travelUserRepository.save(travelUser);
	}

	@Transactional
	public void deleteTravelByOrganizer(Long travelId, Long userId) {
		TravelUser travelUser = findTravelUser(travelId, userId);

		if (travelUser.getRole() != ParticipantRole.ORGANIZER) {
			throw new WeGoException(INSUFFICIENT_TRAVEL_PERMISSION);
		}

		travelUserRepository.delete(travelUser);
		travelRepository.delete(travelUser.getTravel());
	}

	@Transactional
	public void deleteTravelByAttendee(Long travelId, Long userId) {
		TravelUser travelUser = findTravelUser(travelId, userId);

		if (travelUser.getRole() != ParticipantRole.ATTENDEE) {
			throw new WeGoException(NOT_PARTICIPATED_TRAVEL);
		}

		travelUserRepository.delete(travelUser);
	}

	@Transactional
	public void addBookmark(Long userId, Long travelId) {
		User user = findUser(userId);
		Travel travel = findTravel(travelId);

		if (bookmarkRepository.existsByUserAndTravel(user, travel)) {
			return;
		}

		Bookmark bookmark = Bookmark.builder()
			.user(user)
			.travel(travel)
			.build();
		bookmarkRepository.save(bookmark);
	}

	@Transactional
	public void deleteBookmark(Long userId, Long travelId) {
		User user = findUser(userId);
		Travel travel = findTravel(travelId);

		Bookmark bookmark = bookmarkRepository.findByUserAndTravel(user, travel)
			.orElseThrow(() -> new WeGoException(BOOKMARK_NOT_FOUND));
		bookmarkRepository.delete(bookmark);
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new WeGoException(USER_NOT_FOUND));
	}

	private Travel findTravel(Long travelId) {
		return travelRepository.findById(travelId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
	}

	private TravelUser findTravelUser(Long travelId, Long userId) {
		return travelUserRepository.findByTravelIdAndUserId(travelId, userId)
			.orElseThrow(() -> new WeGoException(TRAVEL_NOT_FOUND));
	}
}
