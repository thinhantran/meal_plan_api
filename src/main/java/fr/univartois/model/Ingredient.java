package fr.univartois.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Ingredient {

  @Id
  @Column(name = "ingredient_id")
  private Long ingredientId;

  @Column(nullable = false, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  private IngredientCategory category = IngredientCategory.MISCELLANEOUS;

  @OneToMany(fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<IngredientRecipeQuantity> recipes;
}
