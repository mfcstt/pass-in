package rocketseat.com.passin.services;

import java.util.*;
import java.time.*;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import rocketseat.com.passin.domain.attendee.Attendee;
import rocketseat.com.passin.domain.checkin.Ckeckin;
import rocketseat.com.passin.dto.attendee.AttendeeDetailDTO;
import rocketseat.com.passin.dto.attendee.AttendeesListResponseDTO;
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
}
