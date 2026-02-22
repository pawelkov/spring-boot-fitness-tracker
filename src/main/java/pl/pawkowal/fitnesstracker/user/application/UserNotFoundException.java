package pl.pawkowal.fitnesstracker.user.application;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with ID=%d was not found".formatted(id));
    }
}
