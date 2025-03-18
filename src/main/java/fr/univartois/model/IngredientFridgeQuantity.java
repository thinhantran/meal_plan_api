package fr.univartois.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
