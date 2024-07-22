package antifraud.service;

import antifraud.domain.User;
import antifraud.form.AccessForm;
import antifraud.form.RoleForm;
import antifraud.form.UserForm;
import antifraud.repository.UserRepository;
import antifraud.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UserForm> register(User user) {
        if (userRepository.findUserByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        setRole(user);
        setPassword(user);
        userRepository.save(user);
        return new ResponseEntity<>(new UserForm(user), HttpStatus.CREATED);
    }

    private void setRole(User user) {
        if (userRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMINISTRATOR);
            user.setLocked(false);
        } else {
            user.setRole(Role.MERCHANT);
        }
    }

    private void setPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    public ResponseEntity<Map<String, String>> deleteUser(String username) {
        if (userRepository.findUserByUsernameIgnoreCase(username).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findUserByUsernameIgnoreCase(username).get();
        userRepository.delete(user);
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("status", "Deleted successfully!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public List<UserForm> getUsers() {
        return userRepository.findAll().stream().map(UserForm::new).collect(Collectors.toList());
    }

    public ResponseEntity<UserForm> changeRole(RoleForm roleForm) {
        if (userRepository.findUserByUsernameIgnoreCase(roleForm.getUsername()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findUserByUsernameIgnoreCase(roleForm.getUsername()).get();
        Role currentRole = user.getRole();
        Role newRole;
        try {
            newRole = Role.valueOf(roleForm.getRole());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!(newRole.equals(Role.MERCHANT) || newRole.equals(Role.SUPPORT))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (currentRole.equals(newRole)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        user.setRole(newRole);
        userRepository.save(user);
        return new ResponseEntity<>(new UserForm(user), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, String>> changeLocking(AccessForm accessForm) {
        if (userRepository.findUserByUsernameIgnoreCase(accessForm.getUsername()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findUserByUsernameIgnoreCase(accessForm.getUsername()).get();
        Role role = user.getRole();
        if (role.equals(Role.ADMINISTRATOR)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        changeStatus(user);
        userRepository.save(user);
        Map<String, String> map = new HashMap<>();
        map.put("status", "User " + accessForm.getUsername() + " " + (user.getLocked() ? "locked" : "unlocked") + "!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    private void changeStatus(User user) {
        user.setLocked(!user.getLocked());
    }
}
