package fr.univartois.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordAuth {

  private User user;

  private String password;
}
