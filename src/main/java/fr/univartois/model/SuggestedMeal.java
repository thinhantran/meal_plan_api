package fr.univartois.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SuggestedMeal extends AbstractMeal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate date;

  private boolean isLunchOrDinnerOtherwise;

  private int numberOfParticipants;

  private String proposerUsername;

  @ManyToOne
  private Recipe associatedRecipe;

  @ManyToOne(fetch = FetchType.EAGER)
  private Family associatedFamily;

  @ManyToMany
  private Set<User> votes;
}
