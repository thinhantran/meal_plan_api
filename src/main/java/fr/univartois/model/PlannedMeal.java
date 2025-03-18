package fr.univartois.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint (columnNames = {"date", "isLunchOrDinnerOtherwise"})
})
public class PlannedMeal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate date;

  private boolean isLunchOrDinnerOtherwise;

  @ManyToOne
  private Recipe associatedRecipe;

  @ManyToOne
  @Transient
  private Family associatedFamily;

  @Transient
  private List<User> expectedPeople;

  @Transient
  private Map<User, Integer> expectedPeopleForLeftovers;
}
