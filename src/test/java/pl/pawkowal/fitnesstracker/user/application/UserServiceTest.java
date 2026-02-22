package pl.pawkowal.fitnesstracker.user.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldThrowEmailAlreadyUsed_whenCreatingUserAndEmailExists() {
        // given
        User user = new User("Jan", "Kowalski", "taken@test.local", LocalDate.of(1990, 1, 1));
        when(userRepository.findByEmail("taken@test.local"))
                .thenReturn(Optional.of(mock(User.class)));

        // when + then
        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(EmailAlreadyUsedException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldSaveUser_whenCreatingUserAndEmailIsFree() {
        // given
        User user = new User("Jan", "Kowalski", "free@test.local", LocalDate.of(1990, 1, 1));
        when(userRepository.findByEmail("free@test.local")).thenReturn(Optional.empty());

        User saved = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        // when
        User result = userService.create(user);

        // then
        assertThat(result).isSameAs(saved);
        verify(userRepository).save(user);
    }
}
