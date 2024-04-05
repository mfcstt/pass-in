package rocketseat.com.passin.services;

import java.util.*;
import java.time.*;


import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import rocketseat.com.passin.domain.attendee.Attendee;
import rocketseat.com.passin.domain.attendee.exceptions.AttendeeAlreadyExistException;
import rocketseat.com.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import rocketseat.com.passin.domain.checkin.Ckeckin;
import rocketseat.com.passin.dto.attendee.AttendeeBadgeResponseDTO;
import rocketseat.com.passin.dto.attendee.AttendeeDetailDTO;
import rocketseat.com.passin.dto.attendee.AttendeesListResponseDTO;
import rocketseat.com.passin.dto.attendee.AttendeeBadgeDTO;
import rocketseat.com.passin.repositories.AttendeeRepository;
import rocketseat.com.passin.repositories.CheckinRepository;

@Service
@RequiredArgsConstructor
public class AttendeeService {

    private final AttendeeRepository attendeeRepository;
    private final CheckinRepository checkInRepository;

    public List <Attendee> getAllAttendeesFromEvent(String eventId){
         return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeesListResponseDTO getEventsAttendee(String eventId){
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetailDTO> attendeeDetailsList = attendeeList.stream().map(attendee -> {
            Optional <Ckeckin> ckeckIn = this.checkInRepository.findByAttendeeId(attendee.getId());
            LocalDateTime checkedInAt = ckeckIn.isPresent() ? ckeckIn.get().getCreatedAt() : null;
            return new AttendeeDetailDTO(
                attendee.getId(), 
                attendee.getName(), 
                attendee.getEmail(), 
                attendee.getCreatedAt(), checkedInAt);
        }).toList();

        return new AttendeesListResponseDTO(attendeeDetailsList);
    }

    public void verifyAttendeeSubscription(String email, String eventId){
        Optional <Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);
        if(isAttendeeRegistered.isPresent()) throw new AttendeeAlreadyExistException("Attendee is already registered");
    }

    public Attendee registerAttendee(Attendee newAttendee){
        this.attendeeRepository.save(newAttendee);
        return newAttendee;
}

    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder){
        Attendee attendee = this.attendeeRepository.findById(attendeeId)
        .orElseThrow(() -> new AttendeeNotFoundException("Attendee not found with ID: " + attendeeId));

        var uri = uriComponentsBuilder.path("/attendees/{attendeeId}/ckeck-in")
        .buildAndExpand(attendeeId).toUri().toString();


        AttendeeBadgeDTO badgeDTO = new AttendeeBadgeDTO(
            attendee.getName(), 
            attendee.getEmail(), 
            uri, 
            attendee.getEvent().getId());
        return new AttendeeBadgeResponseDTO(badgeDTO);
    }
}
