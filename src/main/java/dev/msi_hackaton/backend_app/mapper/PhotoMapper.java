package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.Photo;
import dev.msi_hackaton.backend_app.dto.response.PhotoResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface PhotoMapper {

    @Mapping(target = "description", ignore = true)
    PhotoResponseDto toDto(Photo photo);
}