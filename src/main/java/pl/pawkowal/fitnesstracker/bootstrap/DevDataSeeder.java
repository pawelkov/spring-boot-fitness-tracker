package pl.pawkowal.fitnesstracker.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.pawkowal.fitnesstracker.training.domain.Training;
import pl.pawkowal.fitnesstracker.training.infrastructure.TrainingRepository;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    public DevDataSeeder(UserRepository userRepository, TrainingRepository trainingRepository) {
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User u1 = userRepository.save(new User("Emma", "Johnson", "emma.johnson@dev.local", LocalDate.of(1996, 3, 12)));
        User u2 = userRepository.save(new User("Ethan", "Taylor", "ethan.taylor@dev.local", LocalDate.of(1980, 11, 2)));
        User u3 = userRepository.save(new User("Olivia", "Davis", "olivia.davis@dev.local", LocalDate.of(1975, 6, 25)));

        List<Training> trainings = List.of(
                new Training(u1, LocalDateTime.of(2026, 2, 10, 8, 0),  LocalDateTime.of(2026, 2, 10, 9, 0),  "RUN",     10.5, 10.5),
                new Training(u1, LocalDateTime.of(2026, 2, 12, 18, 0), LocalDateTime.of(2026, 2, 12, 19, 10), "CYCLING", 25.0, 21.4),
                new Training(u2, LocalDateTime.of(2026, 2, 15, 7, 30), LocalDateTime.of(2026, 2, 15, 8, 10), "WALK",     4.2,  6.3),
                new Training(u3, LocalDateTime.of(2026, 2, 20, 16, 0), LocalDateTime.of(2026, 2, 20, 17, 0), "RUN",      8.0,  8.0)
        );

        trainingRepository.saveAll(trainings);
    }
}
