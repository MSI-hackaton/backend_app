package dev.msi_hackaton.backend_app.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "video_streams")
public class VideoStream extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id", nullable = false)
    private ConstructionStage construction;

    @Column(name = "stream_url", nullable = false)
    private String streamUrl;  // URL потокового видео

    @Column(name = "camera_name", nullable = false)
    private String cameraName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "camera_location")
    private String cameraLocation;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
}