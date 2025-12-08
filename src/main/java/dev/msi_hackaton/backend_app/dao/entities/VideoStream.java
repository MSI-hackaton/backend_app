package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "video_streams")
public class VideoStream extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ConstructionRequest request;

    @Column(name = "stream_url", nullable = false)
    private String streamUrl;

    @Column(name = "camera_name", nullable = false)
    private String cameraName;

    public VideoStream() {
    }

    public ConstructionRequest getRequest() {
        return request;
    }

    public void setRequest(ConstructionRequest request) {
        this.request = request;
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