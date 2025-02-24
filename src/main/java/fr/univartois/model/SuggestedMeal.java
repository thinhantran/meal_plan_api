package fr.univartois.model;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuggestedMeal extends AbstractMeal {

  private Set<User> votes;
}
