package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.model.Token;
import project.repository.TokenRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TokenService {
    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserService userService;

    public Token save(Token token) {
        token.setCreateAt(LocalDateTime.now(ZoneId.of("UTC")));
        return tokenRepository.save(token);
    }

    public boolean existToken(String token) {
        if (tokenRepository.findByToken(token) == null) {
            throw new IllegalStateException("Token does not Exist!");
        }
        return true;
    }

    public Token getConfirmedByToken(String token) {
        if (!existToken(token)){
            throw new IllegalStateException("Token does not Exist!");
        }

        Token tokenModel = tokenRepository.findByToken(token);
        LocalDateTime expiredAt = tokenModel.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now(ZoneId.of("UTC")))) {
            throw new IllegalStateException("Token expired!");
        }
        tokenModel.setConfirmedAt(LocalDateTime.now(ZoneId.of("UTC")));
        tokenRepository.save(tokenModel);
        return tokenModel;
    }
}
