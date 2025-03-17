package fr.univartois.model;

public enum IngredientUnit {
    KG(1000, "G"),
    G(1, "G"),
    L(1000, "ML"),
    ML(1, "ML");

    private final double conversionFactor;
    private final String baseUnit;

    IngredientUnit(double conversionFactor, String baseUnit) {
        this.conversionFactor = conversionFactor;
        this.baseUnit = baseUnit;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    public String getBaseUnit() {
        return baseUnit;
    }
}
