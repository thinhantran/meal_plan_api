package fr.univartois.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenAuth {

  private User user;

  private String token;
}
