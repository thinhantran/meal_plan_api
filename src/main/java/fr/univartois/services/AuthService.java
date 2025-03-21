package fr.univartois.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.univartois.dtos.CustomJwtAccess;
import fr.univartois.dtos.CustomJwtPair;
import fr.univartois.model.PasswordAuth;
import fr.univartois.model.TokenAuth;
import fr.univartois.model.User;
import fr.univartois.repository.PasswordAuthRepository;
import fr.univartois.repository.TokenAuthRepository;
import fr.univartois.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {

  public static final String REFRESH_GROUP = "refresh";

  SecureRandom random = new SecureRandom();

  PasswordAuthRepository passwordAuthRepository;

  TokenAuthRepository tokenAuthRepository;

  UserRepository userRepository;

  public AuthService(PasswordAuthRepository passwordAuthRepository, TokenAuthRepository tokenAuthRepository, UserRepository userRepository) {
    this.passwordAuthRepository = passwordAuthRepository;
    this.tokenAuthRepository = tokenAuthRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public User createUserWithPassword(String username, String password) {
    User user = new User();
    user.setUsername(username);
    PasswordAuth passwordAuth = new PasswordAuth();
    byte[] salt = generateSalt();
    String newPassword = encryptPassword(password, salt);
    passwordAuth.setPassword(newPassword);
    passwordAuth.setSalt(salt);
    passwordAuth.setUser(user);
    passwordAuthRepository.persist(passwordAuth);
    return user;
  }

  @Transactional
  public void updateUserWithNewPassword(PasswordAuth passwordAuth, String newPassword) {
    byte[] salt = generateSalt();
    String newPasswordEncrypted = encryptPassword(newPassword, salt);
    passwordAuth.setSalt(salt);
    passwordAuth.setPassword(newPasswordEncrypted);
    passwordAuthRepository.getEntityManager().merge(passwordAuth);
  }

  public PasswordAuth findPasswordAuth(String username) {
    return passwordAuthRepository.find("user.username", username).firstResult();
  }

  public User findUser(String username) {
    return userRepository.find("username", username).firstResult();
  }

  public boolean comparePassword(PasswordAuth passwordAuth, String password) {
    String storedPassword = passwordAuth.getPassword();
    String encryptedInput = encryptPassword(password, passwordAuth.getSalt());
    return Objects.equals(storedPassword, encryptedInput);
  }

  @Transactional
  public CustomJwtAccess getAccessToken(User user) {
    return new CustomJwtAccess(Jwt.issuer("http://localhost:8080")
        .subject(user.getUsername())
        .groups("access")
        .expiresAt(Instant.now().plus(1, ChronoUnit.MINUTES))
        .sign());
  }

  @Transactional
  public CustomJwtPair getAccessAndRefreshToken(User user) {
    String accessToken = getAccessToken(user).accessToken();
    String refreshToken = Jwt.issuer("http://localhost:8080")
        .subject(user.getUsername())
        .groups(REFRESH_GROUP)
        .expiresAt(Instant.now().plus(28, ChronoUnit.DAYS))
        .sign();
    TokenAuth tokenAuth = new TokenAuth();
    tokenAuth.setToken(refreshToken);
    tokenAuth.setUser(user);
    tokenAuthRepository.persist(tokenAuth);
    return new CustomJwtPair(accessToken, refreshToken);
  }

  public User hasAssociatedUser(JsonWebToken jwt) {
    String username = jwt.getSubject();
    String rawToken = jwt.getRawToken();
    TokenAuth tokenAuth = tokenAuthRepository.find("token", rawToken).firstResult();
    if (tokenAuth != null && Objects.equals(username, tokenAuth.getUser().getUsername())) {
      return tokenAuth.getUser();
    }
    return null;
  }

  @Transactional
  public void deleteRefreshToken(JsonWebToken jwt) {
    tokenAuthRepository.delete("token", jwt.getRawToken());
  }

  public byte[] generateSalt() {
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return salt;
  }

  public String encryptPassword(String password, byte[] salt) {
    return BcryptUtil.bcryptHash(password, 6, salt);
  }
}
