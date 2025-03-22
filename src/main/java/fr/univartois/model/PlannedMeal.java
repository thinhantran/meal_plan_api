package fr.univartois.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = { "date", "isLunchOrDinnerOtherwise", "associatedFamilyId" })
})
@AllArgsConstructor
@NoArgsConstructor
public class PlannedMeal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate date;

  private boolean isLunchOrDinnerOtherwise;

  private int numberOfParticipants;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Recipe associatedRecipe;

  @ManyToOne
  @JoinColumn(name = "associatedFamilyId")
  private Family associatedFamily;

  public PlannedMeal(SuggestedMeal suggestedMeal) {
    this(null, suggestedMeal.getDate(), suggestedMeal.isLunchOrDinnerOtherwise(),
        suggestedMeal.getNumberOfParticipants(), suggestedMeal.getAssociatedRecipe(),
        suggestedMeal.getAssociatedFamily());
  }
}
