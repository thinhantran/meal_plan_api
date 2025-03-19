package fr.univartois.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

  @OneToMany(fetch = FetchType.EAGER)
  @JsonBackReference("ingredient")
  private List<IngredientRecipeQuantity> recipes;
}
