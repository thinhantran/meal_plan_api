package fr.univartois.fixtures;

import fr.univartois.model.PasswordAuth;
import fr.univartois.repository.PasswordAuthRepository;
import fr.univartois.repository.UserRepository;
import fr.univartois.services.AuthService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PasswordAuthFixture extends Fixture {

    private static final String ADMIN_PASSWORD = "admin";

    AuthService authService;

    UserRepository userRepository;

    PasswordAuthRepository passwordAuthRepository;

    public PasswordAuthFixture(AuthService authService, UserRepository userRepository,
        PasswordAuthRepository passwordAuthRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordAuthRepository = passwordAuthRepository;
    }

    @Override
    @Transactional
    public void generateRealData() {
        String[] passwords = new String[]{ADMIN_PASSWORD, ADMIN_PASSWORD, ADMIN_PASSWORD};
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
    public void generateDataFromOutsideSource() {
        // UNUSED
    }
}
