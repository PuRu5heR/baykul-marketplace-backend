package by.baykulbackend.controller.user;

import by.baykulbackend.database.dao.user.User;
import by.baykulbackend.database.model.Views;
import by.baykulbackend.services.user.RegistrationService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/registration")
@RequiredArgsConstructor
public class UserRegistrationRestController {
    private final RegistrationService registrationService;

    @PutMapping
    public ResponseEntity<?> registration(@RequestBody @JsonView(Views.UserView.Put.class) User user) {
        return registrationService.registerUser(user);
    }
}
