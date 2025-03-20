package fr.univartois.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientFridgeQuantityInput {

  private String ingredientName;

  private LocalDate date;

  private Double quantity;

  private IngredientUnit measurementUnit;
}
