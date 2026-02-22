package pl.pawkowal.fitnesstracker.user.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pawkowal.fitnesstracker.user.domain.User;
import pl.pawkowal.fitnesstracker.user.infrastructure.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("User already has ID - create is not allowed.");
        }
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> { throw new EmailAlreadyUsedException(user.getEmail()); });

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User update(Long id, User updated) {
        User existing = getById(id);

        if (updated.getEmail() != null && !updated.getEmail().equals(existing.getEmail())) {
            userRepository.findByEmail(updated.getEmail())
                    .ifPresent(u -> { throw new EmailAlreadyUsedException(updated.getEmail()); });
        }

        existing.update(
                updated.getFirstName(),
                updated.getLastName(),
                updated.getEmail(),
                updated.getBirthdate()
        );

        return userRepository.save(existing);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<User> olderThan(int age) {
        LocalDate cutoff = LocalDate.now().minusYears(age);
        return userRepository.findByBirthdateBefore(cutoff);
    }
}
