package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionStage;
import dev.msi_hackaton.backend_app.dao.entities.VideoStream;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionStageRepository;
import dev.msi_hackaton.backend_app.dao.repository.VideoStreamRepository;
import dev.msi_hackaton.backend_app.dto.request.VideoStreamCreateDto;
import dev.msi_hackaton.backend_app.dto.response.VideoStreamResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.VideoStreamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoStreamService {

    private final VideoStreamRepository videoStreamRepository;
    private final ConstructionStageRepository constructionStageRepository;
    private final VideoStreamMapper videoStreamMapper;

    @Transactional
    public VideoStreamResponseDto createVideoStream(UUID constructionId, VideoStreamCreateDto createDto) {
        ConstructionStage construction = constructionStageRepository.findById(constructionId)
                .orElseThrow(() -> new EntityNotFoundException("Construction request not found: " + constructionId));

        VideoStream stream = videoStreamMapper.toEntity(createDto);
        stream.setConstruction(construction);

        VideoStream savedStream = videoStreamRepository.save(stream);
        return videoStreamMapper.toDto(savedStream);
    }

    @Transactional(readOnly = true)
    public List<VideoStreamResponseDto> getStreamsByConstruction(UUID constructionId) {
        return videoStreamRepository.findByConstructionId(constructionId).stream()
                .map(videoStreamMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public VideoStreamResponseDto getStreamById(UUID streamId) {
        VideoStream stream = videoStreamRepository.findById(streamId)
                .orElseThrow(() -> new EntityNotFoundException("Video stream not found: " + streamId));
        return videoStreamMapper.toDto(stream);
    }

    @Transactional
    public VideoStreamResponseDto updateStream(UUID streamId, VideoStreamCreateDto updateDto) {
        VideoStream stream = videoStreamRepository.findById(streamId)
                .orElseThrow(() -> new EntityNotFoundException("Video stream not found: " + streamId));

        stream.setStreamUrl(updateDto.getStreamUrl());
        stream.setCameraName(updateDto.getCameraName());
        stream.setCameraLocation(updateDto.getCameraLocation());
        stream.setThumbnailUrl(updateDto.getThumbnailUrl());

        VideoStream updated = videoStreamRepository.save(stream);
        return videoStreamMapper.toDto(updated);
    }

    @Transactional
    public void toggleStreamStatus(UUID streamId) {
        VideoStream stream = videoStreamRepository.findById(streamId)
                .orElseThrow(() -> new EntityNotFoundException("Video stream not found: " + streamId));

        stream.setIsActive(!stream.getIsActive());
        videoStreamRepository.save(stream);
    }

    @Transactional
    public void deleteStream(UUID streamId) {
        if (!videoStreamRepository.existsById(streamId)) {
            throw new EntityNotFoundException("Video stream not found: " + streamId);
        }
        videoStreamRepository.deleteById(streamId);
    }
}