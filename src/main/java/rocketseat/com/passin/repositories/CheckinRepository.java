package rocketseat.com.passin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rocketseat.com.passin.domain.checkin.Ckeckin;

import java.util.*;

public interface CheckinRepository extends JpaRepository<Ckeckin, Integer>{
    Optional<Ckeckin> findByAttendeeId(String attendeeId);
}
