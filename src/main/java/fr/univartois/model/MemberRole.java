package fr.univartois.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MemberRole {

  @Id
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "userId")
  private User user;

  @ManyToOne
  private Family family;

  private Role category;

  public enum Role {
    MEMBER, PROPOSER, MANAGER, ADMIN;
  }
}
