package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ChatMessageCreateDto {
    @NotBlank(message = "Message cannot be empty")
    private String message;

    private String attachmentId; // ID прикрепленного файла/фото

    public ChatMessageCreateDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }
}