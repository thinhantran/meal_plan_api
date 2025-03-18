package fr.univartois.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JsonBackReference
  private Ingredient ingredient;

  @ManyToOne
  @JsonBackReference
  private Recipe recipe;

  private String quantity;
}
