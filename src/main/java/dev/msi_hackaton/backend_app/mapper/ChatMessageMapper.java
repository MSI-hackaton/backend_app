package dev.msi_hackaton.backend_app.mapper;

import dev.msi_hackaton.backend_app.dao.entities.ChatMessage;
import dev.msi_hackaton.backend_app.dto.response.ChatMessageResponseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ChatMessageMapper {

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", source = "sender.fullName")
    ChatMessageResponseDto toDto(ChatMessage message);
}