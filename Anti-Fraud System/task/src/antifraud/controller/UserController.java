package antifraud.controller;

import antifraud.domain.User;
import antifraud.form.AccessForm;
import antifraud.form.RoleForm;
import antifraud.form.UserForm;
import antifraud.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserForm> register(@RequestBody @Valid User user) {
        return userService.register(user);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @GetMapping("/list")
    public List<UserForm> listOfUsers() {
        return userService.getUsers();
    }

    @PutMapping("/role")
    public ResponseEntity<UserForm> changeRole(@RequestBody RoleForm roleForm) {
        return userService.changeRole(roleForm);
    }

    @PutMapping("/access")
    public ResponseEntity<Map<String, String>> changeLocking(@RequestBody AccessForm accessForm) {
        return userService.changeLocking(accessForm);
    }
}
