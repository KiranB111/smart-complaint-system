package com.peoplevoice.backend.dto;

public record AdminCleanupResponse(
        long deletedAttachments,
        long deletedTimelineEntries,
        long deletedNotifications,
        long deletedComplaints,
        long deletedNonAdminUsers
) {
}
