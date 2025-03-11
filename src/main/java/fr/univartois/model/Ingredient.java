package fr.univartois.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Ingredient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ingredient_id")
  private int ingredientId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String measurementUnit;

  // private List<DietaryRestriction> associatedDietaryRestrictions;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IngredientCategory category = IngredientCategory.AUTRE;

}
