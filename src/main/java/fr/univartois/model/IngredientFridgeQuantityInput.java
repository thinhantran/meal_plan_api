package fr.univartois.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientFridgeQuantityInput {

  private LocalDate date;

  private double quantity;
}
