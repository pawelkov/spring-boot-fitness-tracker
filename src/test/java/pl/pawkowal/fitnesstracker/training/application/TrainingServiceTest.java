package pl.pawkowal.fitnesstracker.training.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pawkowal.fitnesstracker.training.api.TrainingDto;
import pl.pawkowal.fitnesstracker.training.domain.ActivityType;
import pl.pawkowal.fitnesstracker.training.domain.Training;
import pl.pawkowal.fitnesstracker.training.infrastructure.TrainingRepository;
import pl.pawkowal.fitnesstracker.user.application.UserNotFoundException;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    void shouldThrowUserNotFound_whenCreatingTrainingAndUserDoesNotExist() {
        // given
        TrainingDto dto = new TrainingDto(
                null,
                123L,
                LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 11, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );

        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(UserNotFoundException.class);

        verify(trainingRepository, never()).save(any());
    }

    @Test
    void shouldThrowIllegalArgumentException_whenCreatingTrainingWithNonNullId() {
        // given
        TrainingDto dto = new TrainingDto(
                999L, // id non-null
                1L,
                LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 11, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );

        // when + then
        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(userRepository);
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void shouldSaveTraining_whenCreatingTrainingAndUserExists() {
        // given
        Long userId = 1L;

        TrainingDto dto = new TrainingDto(
                null,
                userId,
                LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 11, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );

        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Training saved = mock(Training.class);
        when(trainingRepository.save(any(Training.class))).thenReturn(saved);

        // when
        Training result = trainingService.create(dto);

        // then
        assertThat(result).isSameAs(saved);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).save(captor.capture());

        Training created = captor.getValue();
        assertThat(created.getUser()).isSameAs(user);
        assertThat(created.getStartTime()).isEqualTo(dto.startTime());
        assertThat(created.getEndTime()).isEqualTo(dto.endTime());
        assertThat(created.getActivityType()).isEqualTo(dto.activityType());
        assertThat(created.getDistance()).isEqualTo(dto.distance());
        assertThat(created.getAverageSpeed()).isEqualTo(dto.averageSpeed());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}
