package pl.pawkowal.fitnesstracker.training.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import pl.pawkowal.fitnesstracker.training.domain.ActivityType;

import java.time.LocalDateTime;

public record TrainingDto(
        Long id,
        @NotNull Long userId,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime,
        @NotNull ActivityType activityType,
        double distance,
        double averageSpeed
) {}
