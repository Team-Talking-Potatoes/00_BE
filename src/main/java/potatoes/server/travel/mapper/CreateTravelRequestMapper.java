package potatoes.server.travel.mapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import potatoes.server.travel.dto.CreateTravelRequest;
import potatoes.server.travel.entity.Travel;

@Mapper(componentModel = "spring")
public interface CreateTravelRequestMapper {

	@Mapping(target = "name", source = "request.travelName")
	@Mapping(target = "description", source = "request.travelDescription")
	@Mapping(target = "image", source = "fileUrl")
	@Mapping(target = "startAt", source = "request.startAt")
	@Mapping(target = "endAt", source = "request.endAt")
	@Mapping(target = "registrationEnd", source = "request.registrationEnd")
	@Mapping(target = "tripDuration", expression = "java(calculateTripDuration(request.startAt(), request.endAt()))")
	Travel toEntity(CreateTravelRequest request, String fileUrl);

	default int calculateTripDuration(LocalDateTime startAt, LocalDateTime endAt) {
		return (int)(Duration.between(startAt, endAt).toDays() + 1);
	}

	default Instant map(LocalDateTime dateTime) {
		return dateTime.toInstant(ZoneOffset.UTC);
	}
}
