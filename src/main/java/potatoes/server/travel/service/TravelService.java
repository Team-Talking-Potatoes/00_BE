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
import potatoes.server.travel.bookmark.factory.BookmarkFactory;
import potatoes.server.travel.dto.CreateTravelRequest;
import potatoes.server.travel.dto.DetailTravelRequest;
import potatoes.server.travel.dto.ParticipantResponse;
import potatoes.server.travel.dto.SimpleTravelResponse;
import potatoes.server.travel.dto.TravelDetailResponse;
import potatoes.server.travel.dto.TravelPlanResponse;
import potatoes.server.travel.entity.Travel;
import potatoes.server.travel.entity.TravelUser;
import potatoes.server.travel.factory.TravelFactory;
import potatoes.server.travel.factory.TravelUserFactory;
import potatoes.server.travel.model.TravelModel;
import potatoes.server.travel.model.TravelPlanModel;
import potatoes.server.user.entity.User;
import potatoes.server.user.factory.UserFactory;
import potatoes.server.utils.constant.ParticipantRole;
import potatoes.server.utils.error.exception.WeGoException;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TravelService {

	private final TravelFactory travelFactory;
	private final TravelUserFactory travelUserFactory;
	private final UserFactory userFactory;
	private final BookmarkFactory bookmarkFactory;

	@Transactional
	public void createTravel(Long userId, CreateTravelRequest request) {
		User user = userFactory.findUser(userId);

		TravelModel travelModel = CreateTravelRequest.toModel(request);
		Travel travel = travelFactory.createTravel(travelModel, request.travelImage());

		List<TravelPlanModel> travelPlan = DetailTravelRequest.toModels(request.detailTravel(), travel);
		travelFactory.createTravelPlans(travelPlan);
		travelFactory.createChat(travel, user);
		travelUserFactory.createOrganizer(travel, user);
	}

	public TravelDetailResponse getDetails(Long travelId, Optional<Long> userId) {
		Travel travel = travelFactory.findTravel(travelId);

		Boolean participationFlag = travelUserFactory.isUserParticipating(travelId, userId);
		Boolean bookmarkFlag = bookmarkFactory.isUserParticipating(userId, travelId);

		List<TravelPlanResponse> travelPlanResponses = travelFactory.findAllTravelPlans(travel);
		List<ParticipantResponse> participantResponses = travelUserFactory.findAllParticipants(travel);

		return TravelDetailResponse.from(travel, travelPlanResponses, participantResponses, participationFlag,
			bookmarkFlag);
	}

	public List<SimpleTravelResponse> getPopularTravels(Optional<Long> userId) {

		return travelFactory.findTop8ByOrderByIdDesc()
			.stream()
			.map(travel -> {
				int currentTravelMate = (int)travelUserFactory.countByTravel(travel);
				Boolean isBookmark = bookmarkFactory.isUserParticipating(userId, travel.getId());

				return SimpleTravelResponse.from(travel, currentTravelMate, isBookmark);
			})
			.collect(Collectors.toList());
	}

	@Transactional
	public void participateInTravel(Long travelId, Long userId) {
		Boolean existsCheckFlag = travelUserFactory.isUserParticipating(travelId, userId);

		if (existsCheckFlag) {
			throw new WeGoException(ALREADY_PARTICIPATED_TRAVEL);
		}

		User user = userFactory.findUser(userId);
		Travel travel = travelFactory.findTravel(travelId);

		travelUserFactory.createAttendee(travel, user);
	}

	@Transactional
	public void deleteTravelByOrganizer(Long travelId, Long userId) {
		TravelUser travelUser = travelUserFactory.findTravelUser(travelId, userId);

		if (travelUser.getRole() != ParticipantRole.ORGANIZER) {
			throw new WeGoException(INSUFFICIENT_TRAVEL_PERMISSION);
		}

		travelUserFactory.deleteTravelUser(travelUser);
		travelFactory.deleteTravel(travelUser.getTravel());
	}

	@Transactional
	public void deleteTravelByAttendee(Long travelId, Long userId) {
		TravelUser travelUser = travelUserFactory.findTravelUser(travelId, userId);

		if (travelUser.getRole() != ParticipantRole.ATTENDEE) {
			throw new WeGoException(NOT_PARTICIPATED_TRAVEL);
		}

		travelUserFactory.deleteTravelUser(travelUser);
	}

	@Transactional
	public void addBookmark(Long userId, Long travelId) {
		User user = userFactory.findUser(userId);
		Travel travel = travelFactory.findTravel(travelId);

		Boolean existsCheckFlag = bookmarkFactory.isUserParticipating(user.getId(), travel.getId());

		if (existsCheckFlag) {
			throw new WeGoException(BOOKMARK_ALREADY_EXIST);
		}

		bookmarkFactory.createBookmark(user, travel);
	}

	@Transactional
	public void deleteBookmark(Long userId, Long travelId) {
		User user = userFactory.findUser(userId);
		Travel travel = travelFactory.findTravel(travelId);

		Boolean existsCheckFlag = bookmarkFactory.isUserParticipating(user.getId(), travel.getId());

		if (!existsCheckFlag) {
			throw new WeGoException(BOOKMARK_NOT_FOUND);
		}

		Bookmark bookmark = bookmarkFactory.getBookmark(userId, travelId);
		bookmarkFactory.deleteBookmark(bookmark);
	}
}
