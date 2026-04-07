package com.peoplevoice.backend.service;

import com.peoplevoice.backend.dto.TimelineEntryResponse;
import com.peoplevoice.backend.model.Complaint;
import com.peoplevoice.backend.model.ComplaintTimelineEntry;
import com.peoplevoice.backend.model.Role;
import com.peoplevoice.backend.repository.ComplaintTimelineEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final ComplaintTimelineEntryRepository timelineRepository;

    public void record(Complaint complaint, String actorName, Role actorRole, String fromStatus, String toStatus, String message) {
        ComplaintTimelineEntry entry = new ComplaintTimelineEntry();
        entry.setComplaint(complaint);
        entry.setActorName(actorName);
        entry.setActorRole(actorRole.name());
        entry.setFromStatus(fromStatus);
        entry.setToStatus(toStatus);
        entry.setMessage(message);
        timelineRepository.save(entry);
    }

    public List<TimelineEntryResponse> getTimeline(Long complaintId) {
        return timelineRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId).stream()
                .map(entry -> new TimelineEntryResponse(
                        entry.getId(),
                        entry.getActorName(),
                        entry.getActorRole(),
                        entry.getFromStatus(),
                        entry.getToStatus(),
                        entry.getMessage(),
                        entry.getCreatedAt()
                ))
                .toList();
    }
}
