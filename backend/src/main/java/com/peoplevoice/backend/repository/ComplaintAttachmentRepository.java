package com.peoplevoice.backend.repository;

import com.peoplevoice.backend.model.ComplaintAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintAttachmentRepository extends JpaRepository<ComplaintAttachment, Long> {
    List<ComplaintAttachment> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);
}
