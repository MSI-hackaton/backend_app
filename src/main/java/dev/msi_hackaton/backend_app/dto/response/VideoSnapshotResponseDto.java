package dev.msi_hackaton.backend_app.dto.response;

import java.time.Instant;
import java.util.UUID;

public class VideoSnapshotResponseDto {
    private UUID requestId;
    private String imageUrl;
    private Instant timestamp;
    private Boolean emulated;
    private String description;

    public VideoSnapshotResponseDto() {
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getEmulated() {
        return emulated;
    }

    public void setEmulated(Boolean emulated) {
        this.emulated = emulated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}