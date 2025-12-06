package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.User;

import dev.msi_hackaton.backend_app.dto.request.UserCreateDto;
import dev.msi_hackaton.backend_app.dto.response.UserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserCreateDto userCreateDto);

    UserResponseDto toDto(User user);
}
