package fr.univartois.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRole {

  private User user;

  private Family family;

  private Role category;

  public enum Role {
    MEMBER, PROPOSER, MANAGER, ADMIN;
  }
}
