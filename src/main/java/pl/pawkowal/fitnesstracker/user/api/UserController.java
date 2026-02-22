package pl.pawkowal.fitnesstracker.user.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.pawkowal.fitnesstracker.user.application.UserService;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return mapper.toDto(userService.getById(id));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto dto) {
        return mapper.toDto(userService.create(mapper.toEntity(dto)));
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserDto dto) {
        return mapper.toDto(userService.update(id, mapper.toEntity(dto)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/older-than/{age}")
    public List<UserDto> olderThan(@PathVariable int age) {
        return userService.olderThan(age).stream().map(mapper::toDto).toList();
    }
}
