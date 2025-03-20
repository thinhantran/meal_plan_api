package fr.univartois.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SuggestedMeal {

  @Id
  private Long id;

  private LocalDate date;

  private boolean isLunchOrDinnerOtherwise;

  @ManyToOne
  private Recipe associatedRecipe;

  @ManyToOne
  private Family associatedFamily;

  @Transient
  private List<User> expectedPeople;

  @Transient
  private Map<User, Integer> expectedPeopleForLeftovers;

  @ManyToMany
  private Set<User> votes;
}
