package fr.univartois.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class IngredientFridgeQuantity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int ingredientFridgeQuantityId;

  private LocalDate date;

  @ManyToOne
  @JoinColumn(name = "ingredient_id", nullable = false)
  private Ingredient ingredient;

  @ManyToOne
  @JoinColumn(name = "fridge_id", nullable = false)
  @JsonBackReference
  private Fridge fridge;

  @Column(nullable = false)
  private double quantity;

  private IngredientUnit measurementUnit;
}
