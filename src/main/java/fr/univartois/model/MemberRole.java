package fr.univartois.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MemberRole {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @ManyToOne
  @JoinColumn(name = "family_id", nullable = false)
  private Family family;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role category;

  public enum Role {
    MEMBER, PROPOSER, MANAGER, ADMIN
  }
}
