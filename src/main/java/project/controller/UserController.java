package project.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import project.dto.UserDto;
import project.model.Token;
import project.model.User;
import project.repository.EmailSender;
import project.repository.UserRepository;
import project.service.TokenService;
import project.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    private TokenService tokenService;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    EmailSender emailSender;

    @GetMapping("/user/v1")
    public List<User> showUser() {
        return userRepository.findAll();
    }

    @PostMapping("/user")
    public ResponseEntity<User> saveUser(@RequestBody @Valid UserDto userDto){
        var user = new User();
        BeanUtils.copyProperties(userDto, user);
        String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return new ResponseEntity<User>(userRepository.save(user), HttpStatus.CREATED);
    }

    @PostMapping("/user/signup")
    public ResponseEntity<User> signUpUser(@RequestBody @Valid UserDto userDTO) {
        var user = new User();
        BeanUtils.copyProperties(userDTO, user);
        String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        var newUser = new User();
        newUser = userService.signUpUser(user);
        //Create the token
        Token token = new Token(
                UUID.randomUUID().toString(),
                newUser,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1)
        );
        tokenService.save(token);

        //sending email
        String link = "http://localhost:9000/api/v1/user/confirm?token=" + token.getToken();
        emailSender.send(
                newUser.getEmail(),
                userService.buildEmail(newUser.getName(), link));

        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }

    @GetMapping(path = "/user/confirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
        if (!tokenService.existToken(token)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Token tokenModel = tokenService.getConfirmedByToken(token);
        User userModel = userService.enableUserByTokenConfirmed(tokenModel.getUser().getUserId());
        return new ResponseEntity<String>("Confirmed", HttpStatus.OK);
    }

    @GetMapping("/users")
    public String getAll(){
        return "Ok";
    }
}
