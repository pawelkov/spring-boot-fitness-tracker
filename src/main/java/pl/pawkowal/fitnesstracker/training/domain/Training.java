package pl.pawkowal.fitnesstracker.training.domain;

import jakarta.persistence.*;
import pl.pawkowal.fitnesstracker.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    private double distance;
    private double averageSpeed;

    protected Training() {}

    public Training(User user,
                    LocalDateTime startTime,
                    LocalDateTime endTime,
                    ActivityType activityType,
                    double distance,
                    double averageSpeed) {
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activityType = activityType;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public double getDistance() {
        return distance;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void reschedule(LocalDateTime newStart, LocalDateTime newEnd) {
        validateTime(newStart, newEnd);
        this.startTime = newStart;
        this.endTime = newEnd;
    }

    public void changeActivityType(ActivityType newType) {
        this.activityType = newType;
    }

    public void updateMetrics(double distance, double averageSpeed) {
        if (distance < 0 || averageSpeed < 0) {
            throw new IllegalArgumentException("Distance and speed must be >= 0");
        }
        this.distance = distance;
        this.averageSpeed = averageSpeed;
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end required");
        if (!end.isAfter(start)) throw new IllegalArgumentException("End must be after start");
    }
}
