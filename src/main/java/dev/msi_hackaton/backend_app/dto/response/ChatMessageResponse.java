package dev.msi_hackaton.backend_app.dto.response;

import java.time.LocalDateTime;

public class ChatMessageResponse {
    private Long id;
    private Long orderId;
    private Long senderId;
    private String senderName;
    private String message;
    private String attachmentUrl;
    private Boolean isRead;
    private LocalDateTime sentAt;

    public ChatMessageResponse() {}

    public ChatMessageResponse(Long id, Long orderId, Long senderId, String senderName,
                               String message, String attachmentUrl, Boolean isRead,
                               LocalDateTime sentAt) {
        this.id = id;
        this.orderId = orderId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.attachmentUrl = attachmentUrl;
        this.isRead = isRead;
        this.sentAt = sentAt;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}