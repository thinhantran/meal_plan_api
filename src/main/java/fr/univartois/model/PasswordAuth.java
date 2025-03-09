package fr.univartois.model;

import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PasswordAuth {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(unique = true, nullable = false)
  private User user;

  private String password;

  private byte[] salt;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PasswordAuth that)) return false;
    return Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(user);
  }
}
