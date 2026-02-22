package pl.pawkowal.fitnesstracker.user.application;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("Email is already used: " + email);
    }
}
