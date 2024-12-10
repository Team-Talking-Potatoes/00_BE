package potatoes.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import potatoes.server.service.AuthService;

@Tag(name = "User", description = "User API")
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

	private final AuthService authService;


}
