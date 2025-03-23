package fr.univartois.dtos;

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

  private Double quantity;

  private String measurementUnit;
}
