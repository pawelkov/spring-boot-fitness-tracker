package pl.pawkowal.fitnesstracker.training.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pawkowal.fitnesstracker.training.api.TrainingDto;
import pl.pawkowal.fitnesstracker.training.api.TrainingMapper;
import pl.pawkowal.fitnesstracker.training.domain.Training;
import pl.pawkowal.fitnesstracker.training.infrastructure.TrainingRepository;
import pl.pawkowal.fitnesstracker.user.application.UserNotFoundException;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingMapper mapper;

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
                "RUN",
                5.0,
                10.0
        );

        when(userRepository.existsById(123L)).thenReturn(false);

        // when + then
        assertThatThrownBy(() -> trainingService.create(dto))
                .isInstanceOf(UserNotFoundException.class);

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
                "RUN",
                5.0,
                10.0
        );

        when(userRepository.existsById(userId)).thenReturn(true);

        User userRef = mock(User.class);
        when(userRepository.getReferenceById(userId)).thenReturn(userRef);

        Training mapped = mock(Training.class);
        when(mapper.toEntity(dto)).thenReturn(mapped);

        Training saved = mock(Training.class);
        when(trainingRepository.save(any())).thenReturn(saved);

        // when
        Training result = trainingService.create(dto);

        // then
        assertThat(result).isSameAs(saved);

        verify(mapper).toEntity(dto);
        verify(mapped).setUser(userRef);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).save(captor.capture());
        assertThat(captor.getValue()).isSameAs(mapped);
    }
}
