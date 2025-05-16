package com.tennismatch.matchapp.dto;

import com.tennismatch.matchapp.model.NtrpLevel;
import com.tennismatch.matchapp.model.Sex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Password is mandatory")
    // Add password policy validation annotations later if needed (e.g., @Pattern)
    // For now, basic @Size. Actual policy is in REQ.md (1.1.3) and will be enforced in service.
    @Size(min = 8, max = 120, message = "Password must be between 8 and 120 characters")
    private String password;

    @NotBlank(message = "First name is mandatory")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50)
    private String lastName;

    @NotNull(message = "NTRP level is mandatory")
    private NtrpLevel ntrpLevel;

    @NotBlank(message = "Home town is mandatory")
    @Size(max = 100)
    private String homeTown;

    // Optional fields - no @NotBlank, @NotNull. Validation for range if needed (e.g. @Min, @Max for age)
    private Integer age;

    private Sex sex;
} 