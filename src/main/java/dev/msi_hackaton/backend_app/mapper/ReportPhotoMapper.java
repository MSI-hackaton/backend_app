package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.ReportPhoto;
import dev.msi_hackaton.backend_app.dto.response.ReportPhotoResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ReportPhotoMapper {

    @Mapping(target = "reportId", source = "report.id")
    ReportPhotoResponseDto toDto(ReportPhoto entity);
}