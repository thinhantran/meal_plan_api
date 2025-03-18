package fr.univartois.themealdb.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriesDTO {

  private List<CategoryDTO> meals;

  @Getter
  @Setter
  public static class CategoryDTO {
    private String strCategory;
  }
}
