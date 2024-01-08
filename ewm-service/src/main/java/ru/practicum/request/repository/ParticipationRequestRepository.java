package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.dto.ParticipationRequestState;
import ru.practicum.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    List<ParticipationRequest> findAllByRequesterId(Integer id);

    List<ParticipationRequest> findAllByEventId(Integer id);

    List<ParticipationRequest> findAllByIdIn(List<Integer> ids);

    @Query("SELECT COUNT (pr) FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = :status")
    Integer countByEventIdAndStatus(@Param("eventId") Integer eventId,
                                    @Param("status") ParticipationRequestState status);
}
