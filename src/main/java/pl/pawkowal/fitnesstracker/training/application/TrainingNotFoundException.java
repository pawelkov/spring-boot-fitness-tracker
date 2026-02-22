package pl.pawkowal.fitnesstracker.training.application;

public class TrainingNotFoundException extends RuntimeException {

    public TrainingNotFoundException(Long id) {
        super("Training with id=%d was not found".formatted(id));
    }
}
