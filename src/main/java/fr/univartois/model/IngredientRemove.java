package fr.univartois.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IngredientRemove {

    private double amountToRemove;
    private String measurementUnit;
}
