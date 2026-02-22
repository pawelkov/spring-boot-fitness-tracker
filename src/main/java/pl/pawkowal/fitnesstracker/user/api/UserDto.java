package pl.pawkowal.fitnesstracker.user.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserDto(
        Long id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthdate
) {}
