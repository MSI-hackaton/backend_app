package dev.msi_hackaton.backend_app.service;

import dev.msi_hackaton.backend_app.dao.entities.ConstructionRequest;
import dev.msi_hackaton.backend_app.dao.entities.VideoStream;
import dev.msi_hackaton.backend_app.dao.repository.ConstructionRequestRepository;
import dev.msi_hackaton.backend_app.dao.repository.VideoStreamRepository;
import dev.msi_hackaton.backend_app.dto.request.VideoStreamCreateDto;
import dev.msi_hackaton.backend_app.dto.response.VideoSnapshotResponseDto;
import dev.msi_hackaton.backend_app.dto.response.VideoStreamResponseDto;
import dev.msi_hackaton.backend_app.exception.EntityNotFoundException;
import dev.msi_hackaton.backend_app.mapper.VideoStreamMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VideoStreamService {
    private final VideoStreamRepository videoStreamRepository;
    private final ConstructionRequestRepository requestRepository;
    private final VideoStreamMapper videoStreamMapper;

    @Value("${video.stream.emulation.enabled:true}")
    private boolean emulationEnabled;

    @Value("${video.stream.emulation.url:https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4}")
    private String emulationUrl;

    public VideoStreamService(VideoStreamRepository videoStreamRepository,
                              ConstructionRequestRepository requestRepository,
                              VideoStreamMapper videoStreamMapper) {
        this.videoStreamRepository = videoStreamRepository;
        this.requestRepository = requestRepository;
        this.videoStreamMapper = videoStreamMapper;
    }

    @Transactional(readOnly = true)
    public List<VideoStreamResponseDto> getStreams(UUID requestId) {
        List<VideoStream> streams = videoStreamRepository.findByRequestId(requestId);

        if (streams.isEmpty() && emulationEnabled) {
            return List.of(createEmulatedStream(requestId));
        }

        return streams.stream()
                .map(videoStreamMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public VideoStreamResponseDto getMainStream(UUID requestId) {
        List<VideoStream> streams = videoStreamRepository.findByRequestId(requestId);

        if (!streams.isEmpty()) {
            return videoStreamMapper.toDto(streams.get(0));
        }

        if (emulationEnabled) {
            return createEmulatedStream(requestId);
        }

        throw new EntityNotFoundException("No video streams found for request: " + requestId);
    }

    @Transactional
    public VideoStreamResponseDto addStream(UUID requestId, VideoStreamCreateDto streamDto) {
        ConstructionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found: " + requestId));

        VideoStream stream = videoStreamMapper.toEntity(streamDto);
        stream.setRequest(request);

        VideoStream savedStream = videoStreamRepository.save(stream);
        return videoStreamMapper.toDto(savedStream);
    }

    @Transactional(readOnly = true)
    public VideoSnapshotResponseDto getSnapshot(UUID requestId) {
        VideoSnapshotResponseDto snapshot = new VideoSnapshotResponseDto();
        snapshot.setRequestId(requestId);
        snapshot.setImageUrl(emulationUrl + "/snapshot.jpg?t=" + Instant.now().getEpochSecond());
        snapshot.setTimestamp(Instant.now());
        snapshot.setEmulated(true);
        snapshot.setDescription("Эмуляция скриншота с камеры на стройке");

        return snapshot;
    }

    private VideoStreamResponseDto createEmulatedStream(UUID requestId) {
        VideoStreamResponseDto emulated = new VideoStreamResponseDto();
        emulated.setId(UUID.randomUUID());
        emulated.setRequestId(requestId);
        emulated.setStreamUrl(emulationUrl);
        emulated.setCameraName("Основная камера (эмуляция)");
        emulated.setIsLive(true);
        emulated.setEmulated(true);
        emulated.setDescription("Эмуляция видеопотока с камеры на стройке");

        return emulated;
    }
}