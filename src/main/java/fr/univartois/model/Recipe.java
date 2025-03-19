package fr.univartois.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@NamedQuery(name = "Recipe.searchByName", query = "SELECT r FROM Recipe r WHERE lower(name) LIKE CONCAT('%',?1,'%') ")
public class Recipe {

  @Id
  private Long recipeId;

  @Column(unique=true)
  private String name;

  private String thumbnailUrl;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe", fetch = FetchType.EAGER)
  @JsonManagedReference("recipe")
  private List<IngredientRecipeQuantity> ingredients;

  @Enumerated(EnumType.STRING)
  private IngredientCategory category = IngredientCategory.MISCELLANEOUS;
}
