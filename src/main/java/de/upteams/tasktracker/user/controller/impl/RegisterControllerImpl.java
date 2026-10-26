package de.upteams.tasktracker.user.controller.impl;

import de.upteams.tasktracker.user.controller.interfaces.RegisterControllerApi;
import de.upteams.tasktracker.user.dto.request.UserCreateDto;
import de.upteams.tasktracker.user.dto.response.UserCreateResponseDto;
import de.upteams.tasktracker.user.dto.response.UserResponseDto;
import de.upteams.tasktracker.user.service.impl.UserRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class RegisterControllerImpl implements RegisterControllerApi {

    @Value("${base-url}")
    private String baseUrl;

    private final UserRegisterService service;

    @Override
    public UserResponseDto confirmRegistration(String code) {
        return service.confirmRegistration(code);
    }

    @Override
    public UserCreateResponseDto register(UserCreateDto registerUser) {
        return service.register(registerUser);
    }


    @GetMapping("/confirm-redirect/{code}")
    public ResponseEntity<Void> confirmEmailRedirect(@PathVariable String code) {
        service.confirmRegistration(code);
        URI redirectUri = URI.create(baseUrl +"/#/login?confirmed=true");
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();


    }
}
