package pl.pawkowal.fitnesstracker.training.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import pl.pawkowal.fitnesstracker.common.api.RestExceptionHandler;
import pl.pawkowal.fitnesstracker.training.application.TrainingService;
import pl.pawkowal.fitnesstracker.training.domain.ActivityType;
import pl.pawkowal.fitnesstracker.training.domain.Training;
import pl.pawkowal.fitnesstracker.user.application.UserNotFoundException;
import pl.pawkowal.fitnesstracker.user.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(TrainingController.class)
@Import({TrainingMapper.class, RestExceptionHandler.class})
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn200_whenCreatingTraining() throws Exception {
        // given: request DTO (what client sends)
        TrainingDto request = new TrainingDto(
                null,
                1L,
                LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 11, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );

        // and: service returns a saved Training (with id + user id)
        User user = new User("Jan", "Kowalski", "jan@test.local", LocalDate.of(1995, 1, 10));
        ReflectionTestUtils.setField(user, "id", 1L);

        Training saved = new Training(
                user,
                request.startTime(),
                request.endTime(),
                request.activityType(),
                request.distance(),
                request.averageSpeed()
        );
        ReflectionTestUtils.setField(saved, "id", 99L);

        when(trainingService.create(any())).thenReturn(saved);

        // when + then
        mockMvc.perform(post("/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.startTime").value("2026-02-10T10:00:00"))
                .andExpect(jsonPath("$.endTime").value("2026-02-10T11:00:00"))
                .andExpect(jsonPath("$.activityType").value("RUNNING"))
                .andExpect(jsonPath("$.distance").value(5.0))
                .andExpect(jsonPath("$.averageSpeed").value(10.0));
    }

    @Test
    void shouldReturn404_whenUserNotFoundOnCreate() throws Exception {
        // given
        TrainingDto request = new TrainingDto(
                null,
                999L,
                LocalDateTime.of(2026, 2, 10, 10, 0),
                LocalDateTime.of(2026, 2, 10, 11, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );

        when(trainingService.create(any())).thenThrow(new UserNotFoundException(999L));

        // when + then
        mockMvc.perform(post("/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn200_andList_whenFilteringByUserAndStartTimeBetween() throws Exception {
        // given
        User user = new User("Anna", "Nowak", "anna@test.local", LocalDate.of(1998, 5, 5));
        ReflectionTestUtils.setField(user, "id", 1L);

        Training t = new Training(
                user,
                LocalDateTime.of(2026, 2, 12, 10, 0),
                LocalDateTime.of(2026, 2, 12, 11, 0),
                ActivityType.RUNNING,
                5.0,
                10.0
        );
        ReflectionTestUtils.setField(t, "id", 7L);

        when(trainingService.byUserIdAndStartTimeBetween(any(), any(), any()))
                .thenReturn(List.of(t));

        // when + then
        mockMvc.perform(get("/v1/trainings/user/1/from/2026-02-01T00:00:00/to/2026-02-28T23:59:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].activityType").value("RUNNING"));
    }
}
