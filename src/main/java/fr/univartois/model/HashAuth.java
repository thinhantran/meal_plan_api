package fr.univartois.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashAuth {

  private User user;

  private String hash;

  private String salt;
}
