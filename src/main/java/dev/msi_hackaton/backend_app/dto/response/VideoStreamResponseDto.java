package dev.msi_hackaton.backend_app.dto.response;

import java.util.UUID;

public class VideoStreamResponseDto {
    private UUID id;
    private UUID requestId;
    private String streamUrl;
    private String cameraName;
    private Boolean isLive;
    private Boolean emulated;
    private String description;

    public VideoStreamResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
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