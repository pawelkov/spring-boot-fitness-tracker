package pl.pawkowal.fitnesstracker.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pawkowal.fitnesstracker.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByBirthdateBefore(LocalDate cutoffDate);
}
