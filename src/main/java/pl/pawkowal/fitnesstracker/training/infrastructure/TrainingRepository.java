package pl.pawkowal.fitnesstracker.training.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pawkowal.fitnesstracker.training.domain.Training;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByUserId(Long userId);

    List<Training> findByEndTimeAfter(LocalDateTime after);

    List<Training> findByActivityType(String activityType);
}
