package com.peoplevoice.backend.repository;

import com.peoplevoice.backend.model.ComplaintTimelineEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintTimelineEntryRepository extends JpaRepository<ComplaintTimelineEntry, Long> {
    List<ComplaintTimelineEntry> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);
}
