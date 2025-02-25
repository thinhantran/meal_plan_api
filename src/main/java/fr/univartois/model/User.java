package fr.univartois.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int userId;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
  private List<DietaryRestriction> dietaryRestrictions;

  private String username;

  @OneToOne
  private MemberRole memberRole;
}
