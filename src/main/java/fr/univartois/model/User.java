package fr.univartois.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

  List<DietaryRestriction> dietaryRestrictions;
  private int userId;
  private String username;
  private MemberRole memberRole;
}
