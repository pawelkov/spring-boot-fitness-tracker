package pl.pawkowal.fitnesstracker.training.api;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pl.pawkowal.fitnesstracker.training.application.TrainingService;
import pl.pawkowal.fitnesstracker.training.domain.ActivityType;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingMapper mapper;

    public TrainingController(TrainingService trainingService, TrainingMapper mapper) {
        this.trainingService = trainingService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<TrainingDto> getAll() {
        return trainingService.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public TrainingDto getById(@PathVariable Long id) {
        return mapper.toDto(trainingService.getById(id));
    }

    @PostMapping
    public TrainingDto create(@Valid @RequestBody TrainingDto dto) {
        return mapper.toDto(trainingService.create(dto));
    }

    @PutMapping("/{id}")
    public TrainingDto update(@PathVariable Long id, @Valid @RequestBody TrainingDto dto) {
        return mapper.toDto(trainingService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        trainingService.delete(id);
    }

    @GetMapping("/user/{userId}")
    public List<TrainingDto> byUser(@PathVariable Long userId) {
        return trainingService.byUserId(userId).stream().map(mapper::toDto).toList();
    }

    @GetMapping("/after/{after}")
    public List<TrainingDto> endAfter(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after
    ) {
        return trainingService.endAfter(after).stream().map(mapper::toDto).toList();
    }

    @GetMapping("/activity/{activityType}")
    public List<TrainingDto> byActivityType(@PathVariable ActivityType activityType) {
        return trainingService.byActivityType(activityType).stream().map(mapper::toDto).toList();
    }

    @GetMapping("/user/{userId}/from/{from}/to/{to}")
    public List<TrainingDto> byUserIdAndStartTimeBetween(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return trainingService.byUserIdAndStartTimeBetween(userId, from, to).stream().map(mapper::toDto).toList();
    }
}
