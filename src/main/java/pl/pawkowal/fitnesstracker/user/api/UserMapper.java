package pl.pawkowal.fitnesstracker.user.api;

import org.springframework.stereotype.Component;
import pl.pawkowal.fitnesstracker.user.domain.User;

@Component
class UserMapper {

    UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthdate()
        );
    }

    User toEntity(UserDto dto) {
        return new User(dto.firstName(), dto.lastName(), dto.email(), dto.birthdate());
    }
}
