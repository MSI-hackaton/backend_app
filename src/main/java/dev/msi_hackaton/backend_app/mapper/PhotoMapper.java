package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.Photo;
import dev.msi_hackaton.backend_app.dto.response.PhotoResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
    PhotoResponseDto toDto(Photo photo);
}
