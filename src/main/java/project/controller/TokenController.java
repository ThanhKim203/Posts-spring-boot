package project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.model.Token;
import project.service.TokenService;

@RestController
@RequestMapping(path = "api/v1")
public class TokenController {
    TokenService tokenService;

    @PostMapping("/register")
    public Token register(@RequestBody Token token){
        return tokenService.save(token);
    }
}
