package fr.univartois.services;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.univartois.dtos.CustomJwtAccess;
import fr.univartois.dtos.CustomJwtPair;
import fr.univartois.model.PasswordAuth;
import fr.univartois.model.TokenAuth;
import fr.univartois.model.User;
import fr.univartois.repository.PasswordAuthRepository;
import fr.univartois.repository.TokenAuthRepository;
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

  public AuthService(PasswordAuthRepository passwordAuthRepository, TokenAuthRepository tokenAuthRepository) {
    this.passwordAuthRepository = passwordAuthRepository;
    this.tokenAuthRepository = tokenAuthRepository;
  }

  @Transactional
  public void createUserWithPassword(String username, String password) {
    User user = new User();
    user.setUsername(username);
    PasswordAuth passwordAuth = new PasswordAuth();
    byte[] salt = generateSalt();
    String newPassword = encryptPassword(password, salt);
    passwordAuth.setPassword(newPassword);
    passwordAuth.setSalt(salt);
    passwordAuth.setUser(user);
    passwordAuthRepository.persist(passwordAuth);
  }

  public PasswordAuth findUser(String username) {
    return passwordAuthRepository.find("user.username", username).firstResult();
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
        .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
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
    if (!Objects.equals(Set.of(REFRESH_GROUP), jwt.getGroups())) {
      return null;
    }
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
    if (!Objects.equals(Set.of(REFRESH_GROUP), jwt.getGroups())) {
      return;
    }
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
