package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.User;
import dev.msi_hackaton.backend_app.dto.UserDto;
import dev.msi_hackaton.backend_app.dto.request.UserCreateDto;
import dev.msi_hackaton.backend_app.dto.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserDto dto);

    UserDto toDto(User entity);

    UserResponseDto toResponseDto(User user);

    User prepareEntityToCreate(UserCreateDto userCreateDto);
}
