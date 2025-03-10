package fr.univartois.fixtures;

import fr.univartois.model.PasswordAuth;
import fr.univartois.repository.PasswordAuthRepository;
import fr.univartois.repository.UserRepository;
import fr.univartois.services.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Arrays;

@ApplicationScoped
public class PasswordAuthFixture extends Fixture {

    @Inject
    AuthService authService;

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordAuthRepository passwordAuthRepository;

    @Override
    @Transactional
    public void generateRealData() {
        String[] passwords = new String[]{"admin", "admin", "admin"};
        for (int i = 1; i <= passwords.length; i++) {
            String password = passwords[i - 1];
            byte[] salt = authService.generateSalt();
            String encryptedPassword = authService.encryptPassword(password, salt);
            PasswordAuth passwordAuth = new PasswordAuth();
            passwordAuth.setPassword(encryptedPassword);
            passwordAuth.setSalt(salt);
            passwordAuth.setUser(userRepository.findById((long) i));
            passwordAuthRepository.persist(passwordAuth);
        }
    }

    @Override
    @Transactional
    public void generateSingleFakeData() {

    }
}
