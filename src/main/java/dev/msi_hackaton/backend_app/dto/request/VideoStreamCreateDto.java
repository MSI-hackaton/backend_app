package dev.msi_hackaton.backend_app.dto.request;

public class VideoStreamCreateDto {
    private String streamUrl;
    private String cameraName;

    public VideoStreamCreateDto() {
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
}