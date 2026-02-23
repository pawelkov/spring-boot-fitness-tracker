package pl.pawkowal.fitnesstracker.training.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.pawkowal.fitnesstracker.training.domain.ActivityType;
import pl.pawkowal.fitnesstracker.training.domain.Training;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindTrainingsByUserIdAndStartTimeBetween() {
        User user = userRepository.save(new User("Jan", "Kowalski", "jan.kowalski@test.local", LocalDate.of(1995, 1, 10)));

        Training t1 = new Training(
                user,
                LocalDateTime.of(2026, 2, 10, 8, 0),
                LocalDateTime.of(2026, 2, 10, 9, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );

        Training t2 = new Training(
                user,
                LocalDateTime.of(2026, 2, 15, 8, 0),
                LocalDateTime.of(2026, 2, 15, 9, 0),
                ActivityType.RUNNING,
                6.0,
                12.0
        );

        Training t3_outside = new Training(
                user,
                LocalDateTime.of(2026, 3, 1, 8, 0),
                LocalDateTime.of(2026, 3, 1, 9, 0),
                ActivityType.RUNNING,
                7.0,
                14.0
        );

        trainingRepository.saveAll(List.of(t1, t2, t3_outside));

        LocalDateTime from = LocalDateTime.of(2026, 2, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 2, 28, 23, 59);

        List<Training> result = trainingRepository.findByUserIdAndStartTimeBetween(user.getId(), from, to);

        assertThat(result)
                .hasSize(2)
                .extracting(Training::getStartTime)
                .containsExactlyInAnyOrder(t1.getStartTime(), t2.getStartTime());
    }

    @Test
    void shouldFindTrainingsByActivityType() {
        // given
        User user = userRepository.save(new User("Anna", "Nowak", "anna.nowak@test.local", LocalDate.of(1998, 5, 5)));

        Training run = new Training(
                user,
                LocalDateTime.of(2026, 2, 10, 8, 0),
                LocalDateTime.of(2026, 2, 10, 9, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );

        Training cycling = new Training(
                user,
                LocalDateTime.of(2026, 2, 11, 8, 0),
                LocalDateTime.of(2026, 2, 11, 9, 0),
                ActivityType.CYCLING,
                20.0,
                20.0
        );

        trainingRepository.saveAll(List.of(run, cycling));

        // when
        List<Training> result = trainingRepository.findByActivityType(ActivityType.CYCLING);

        // then
        assertThat(result)
                .hasSize(1);

        assertThat(result.get(0).getActivityType()).isEqualTo(ActivityType.CYCLING);
        assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());
    }
}
