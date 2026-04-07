package com.peoplevoice.backend.repository;

import com.peoplevoice.backend.model.Complaint;
import com.peoplevoice.backend.model.ComplaintPriority;
import com.peoplevoice.backend.model.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query("""
            select c from Complaint c
            where (:status is null or c.status = :status)
              and (:category is null or lower(c.category) = lower(:category))
            order by
              case c.priority
                when com.peoplevoice.backend.model.ComplaintPriority.CRITICAL then 0
                when com.peoplevoice.backend.model.ComplaintPriority.HIGH then 1
                when com.peoplevoice.backend.model.ComplaintPriority.MEDIUM then 2
                else 3
              end,
              c.createdAt desc
            """)
    List<Complaint> search(ComplaintStatus status, String category);

    @Query("""
            select c from Complaint c
            where c.citizen.id = :citizenId
              and (:status is null or c.status = :status)
              and (:category is null or lower(c.category) = lower(:category))
            order by c.createdAt desc
            """)
    List<Complaint> searchForCitizen(Long citizenId, ComplaintStatus status, String category);

    Optional<Complaint> findByIdAndCitizenId(Long id, Long citizenId);

    long countByStatus(ComplaintStatus status);

    long countByPriority(ComplaintPriority priority);

    long countByAssignedOfficerIdAndStatusIn(Long assignedOfficerId, List<ComplaintStatus> statuses);
}
