package fr.univartois.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {

  @Id
  @Column(name = "ingredient_id")
  private Long ingredientId;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(fetch = FetchType.EAGER)
  @JsonIgnore
  private List<IngredientRecipeQuantity> recipes;
}
