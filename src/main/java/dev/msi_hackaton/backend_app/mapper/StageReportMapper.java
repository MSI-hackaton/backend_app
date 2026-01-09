package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.StageReport;
import dev.msi_hackaton.backend_app.dto.request.StageReportCreateDto;
import dev.msi_hackaton.backend_app.dto.response.ReportPhotoResponseDto;
import dev.msi_hackaton.backend_app.dto.response.StageReportResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        uses = ReportPhotoMapper.class)
public interface StageReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "stage", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reportPhotos", ignore = true)
    StageReport toEntity(StageReportCreateDto dto);

    @Mapping(target = "stageId", source = "stage.id")
    @Mapping(target = "stageName", source = "stage.name")
    @Mapping(target = "photos", source = "reportPhotos", qualifiedByName = "mapReportPhotos")
    StageReportResponseDto toDto(StageReport entity);

    @Named("mapReportPhotos")
    default List<ReportPhotoResponseDto> mapReportPhotos(List<dev.msi_hackaton.backend_app.dao.entities.ReportPhoto> reportPhotos) {
        if (reportPhotos == null || reportPhotos.isEmpty()) {
            return List.of();
        }
        ReportPhotoMapper photoMapper = new ReportPhotoMapperImpl();
        return reportPhotos.stream()
                .map(photoMapper::toDto)
                .toList();
    }
}