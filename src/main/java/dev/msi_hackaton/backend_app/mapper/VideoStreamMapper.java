package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.VideoStream;
import dev.msi_hackaton.backend_app.dto.request.VideoStreamCreateDto;
import dev.msi_hackaton.backend_app.dto.response.VideoStreamResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface VideoStreamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "construction", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    VideoStream toEntity(VideoStreamCreateDto dto);

    @Mapping(target = "constructionId", source = "construction.id")
    VideoStreamResponseDto toDto(VideoStream entity);

    @AfterMapping
    default void setDefaults(@MappingTarget VideoStream stream) {
        if (stream.getIsActive() == null) {
            stream.setIsActive(true);
        }
    }
}