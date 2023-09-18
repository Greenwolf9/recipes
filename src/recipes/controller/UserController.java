package recipes.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import recipes.model.User;
import recipes.service.UserServiceImpl;

import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/api/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userService.existByEmail(user.getEmail())) {
            log.info("User {} already exists", user.getEmail());
            return ResponseEntity.badRequest().build();
        }
        userService.saveUser(user);
        log.info("User {} was saved successfully", user.getEmail());
        return ResponseEntity.ok().build();
    }
}
