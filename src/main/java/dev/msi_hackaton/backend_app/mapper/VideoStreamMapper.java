package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.VideoStream;
import dev.msi_hackaton.backend_app.dto.request.VideoStreamCreateDto;
import dev.msi_hackaton.backend_app.dto.response.VideoStreamResponseDto;
import org.springframework.stereotype.Component;

@Component
public class VideoStreamMapper {

    public VideoStream toEntity(VideoStreamCreateDto dto) {
        VideoStream entity = new VideoStream();
        entity.setStreamUrl(dto.getStreamUrl());
        entity.setCameraName(dto.getCameraName());
        return entity;
    }

    public VideoStreamResponseDto toDto(VideoStream entity) {
        VideoStreamResponseDto dto = new VideoStreamResponseDto();
        dto.setId(entity.getId());
        dto.setRequestId(entity.getRequest().getId());
        dto.setStreamUrl(entity.getStreamUrl());
        dto.setCameraName(entity.getCameraName());
        dto.setIsLive(true); // Предполагаем, что поток всегда живой
        dto.setEmulated(false);
        dto.setDescription("Видеопоток с камеры: " + entity.getCameraName());
        return dto;
    }
}