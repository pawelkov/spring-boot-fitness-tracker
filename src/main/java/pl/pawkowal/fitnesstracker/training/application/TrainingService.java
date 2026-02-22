package pl.pawkowal.fitnesstracker.training.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pawkowal.fitnesstracker.training.api.TrainingDto;
import pl.pawkowal.fitnesstracker.training.api.TrainingMapper;
import pl.pawkowal.fitnesstracker.training.domain.Training;
import pl.pawkowal.fitnesstracker.training.infrastructure.TrainingRepository;
import pl.pawkowal.fitnesstracker.user.application.UserNotFoundException;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final TrainingMapper mapper;

    public TrainingService(TrainingRepository trainingRepository,
                           UserRepository userRepository,
                           TrainingMapper mapper) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
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
        if (userId == null || !userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        User userRef = userRepository.getReferenceById(userId);

        Training training = mapper.toEntity(dto);
        training.setUser(userRef);

        return trainingRepository.save(training);
    }

    @Transactional
    public Training update(Long id, TrainingDto dto) {
        Training existing = getById(id);

        Long userId = dto.userId();
        if (userId == null || !userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        existing.setUser(userRepository.getReferenceById(userId));

        existing.setStartTime(dto.startTime());
        existing.setEndTime(dto.endTime());
        existing.setActivityType(dto.activityType());
        existing.setDistance(dto.distance());
        existing.setAverageSpeed(dto.averageSpeed());

        return trainingRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!trainingRepository.existsById(id)) {
            throw new TrainingNotFoundException(id);
        }
        trainingRepository.deleteById(id);
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
    public List<Training> byActivityType(String activityType) {
        return trainingRepository.findByActivityType(activityType);
    }
}
