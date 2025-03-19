package fr.univartois.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IngredientRecipeQuantity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ingredientRecipeQuantityId;

  @ManyToOne
  @JsonManagedReference("ingredient")
  @JoinColumn(name = "ingredient_id")
  private Ingredient ingredient;

  @ManyToOne
  @JsonBackReference("recipe")
  @JoinColumn(name = "recipe_id")
  private Recipe recipe;

  private String quantity;
}
