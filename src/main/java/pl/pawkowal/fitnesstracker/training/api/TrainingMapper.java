package pl.pawkowal.fitnesstracker.training.api;

import org.springframework.stereotype.Component;
import pl.pawkowal.fitnesstracker.training.domain.Training;

@Component
public class TrainingMapper {

    public TrainingDto toDto(Training t) {
        return new TrainingDto(
                t.getId(),
                t.getUser() != null ? t.getUser().getId() : null,
                t.getStartTime(),
                t.getEndTime(),
                t.getActivityType(),
                t.getDistance(),
                t.getAverageSpeed()
        );
    }
}
