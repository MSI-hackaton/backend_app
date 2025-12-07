package dev.msi_hackaton.backend_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class OrderRequest {
    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;

    private String comment;
    private String address;

    public OrderRequest() {}

    public OrderRequest(Long projectId, String fullName, String email, String phone, String comment, String address) {
        this.projectId = projectId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.comment = comment;
        this.address = address;
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}