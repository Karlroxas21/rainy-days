package com.rainydaysengine.rainydays.application.service.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String emailAddress;

    private String username;

    @NotBlank(message = "First Name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must contain only letters and spaces")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Middle name must contain only letters and spaces")
    private String middleName;

    @NotBlank(message = "Last Name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must contain only letters and spaces")
    private String lastName;

    private String suffix;

    private String profileUrl;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.")
    private String password;
}
