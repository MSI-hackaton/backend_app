package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DocumentSignRequest {
    @NotBlank(message = "SMS code is required")
    private String smsCode;

    public DocumentSignRequest() {}

    public DocumentSignRequest(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getSmsCode() { return smsCode; }
    public void setSmsCode(String smsCode) { this.smsCode = smsCode; }
}