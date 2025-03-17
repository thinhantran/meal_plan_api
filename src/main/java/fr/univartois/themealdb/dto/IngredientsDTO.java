package fr.univartois.themealdb.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientsDTO {

  private List<IngredientDTO> meals;

  @Getter
  @Setter
  public static class IngredientDTO {

    private Long idIngredient;

    private String strIngredient;
  }
}
