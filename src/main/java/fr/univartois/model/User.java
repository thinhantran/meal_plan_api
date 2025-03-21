package fr.univartois.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_table")
public class User {

  @OneToMany(mappedBy = "user", orphanRemoval = true)
  List<DietaryRestriction> dietaryRestrictions;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer userId;

  @Column(unique = true, nullable = false)
  private String username;

  @Transient
  private MemberRole memberRole;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof User user)) return false;
    return Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(username);
  }

  public void addRestriction(DietaryRestriction dietaryRestriction) {
    dietaryRestrictions.add(dietaryRestriction);
  }
}
