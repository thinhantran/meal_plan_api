package fr.univartois.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IngredientCategory {
  BEEF,
  CHICKEN,
  GOAT,
  LAMB,
  MISCELLANEOUS,
  PASTA,
  PORK,
  SEAFOOD,
  VEGAN,
  VEGETARIAN;

  @JsonCreator
  public static IngredientCategory fromString(String string) {
    try {
      return IngredientCategory.valueOf(string.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @JsonValue
  public String toValue() {
    return name();
  }
}
