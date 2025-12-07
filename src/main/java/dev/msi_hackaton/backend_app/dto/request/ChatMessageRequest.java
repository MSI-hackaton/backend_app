package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatMessageRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Message is required")
    private String message;

    private String attachmentUrl;

    public ChatMessageRequest() {}

    public ChatMessageRequest(Long orderId, String message, String attachmentUrl) {
        this.orderId = orderId;
        this.message = message;
        this.attachmentUrl = attachmentUrl;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
}