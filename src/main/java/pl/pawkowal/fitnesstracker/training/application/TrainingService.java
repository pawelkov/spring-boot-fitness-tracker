package pl.pawkowal.fitnesstracker.training.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pawkowal.fitnesstracker.training.api.TrainingDto;
import pl.pawkowal.fitnesstracker.training.domain.Training;
import pl.pawkowal.fitnesstracker.training.infrastructure.TrainingRepository;
import pl.pawkowal.fitnesstracker.user.application.UserNotFoundException;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;
import pl.pawkowal.fitnesstracker.training.domain.ActivityType;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public TrainingService(TrainingRepository trainingRepository,
                           UserRepository userRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Training> getAll() {
        return trainingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Training getById(Long id) {
        return trainingRepository.findById(id).orElseThrow(() -> new TrainingNotFoundException(id));
    }

    @Transactional
    public Training create(TrainingDto dto) {
        if (dto.id() != null) {
            throw new IllegalArgumentException("Training already has id - cannot create");
        }

        Long userId = dto.userId();
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Training training = new Training(
                user,
                dto.startTime(),
                dto.endTime(),
                dto.activityType(),
                dto.distance(),
                dto.averageSpeed()
        );

        return trainingRepository.save(training);
    }

    @Transactional
    public Training update(Long id, TrainingDto dto) {
        Training existing = getById(id);

        existing.reschedule(dto.startTime(), dto.endTime());
        existing.changeActivityType(dto.activityType());
        existing.updateMetrics(dto.distance(), dto.averageSpeed());

        return existing;
    }

    @Transactional
    public void delete(Long id) {
        Training existing = getById(id);
        trainingRepository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<Training> byUserId(Long userId) {
        return trainingRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Training> endAfter(LocalDateTime after) {
        return trainingRepository.findByEndTimeAfter(after);
    }

    @Transactional(readOnly = true)
    public List<Training> byActivityType(ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType);
    }

    @Transactional(readOnly = true)
    public List<Training> byUserIdAndStartTimeBetween(Long userId, LocalDateTime from, LocalDateTime to) {
        return trainingRepository.findByUserIdAndStartTimeBetween(userId, from, to);
    }
}
