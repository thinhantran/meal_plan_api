package fr.univartois.services;

import java.util.List;
import java.util.Optional;

import fr.univartois.model.Family;
import fr.univartois.model.Fridge;
import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientFridgeQuantity;
import fr.univartois.model.IngredientFridgeQuantityInput;
import fr.univartois.model.IngredientRemove;
import fr.univartois.model.IngredientUnit;
import fr.univartois.model.Utensil;
import fr.univartois.model.UtensilInput;
import fr.univartois.repository.FamilyRepository;
import fr.univartois.repository.FridgeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FridgeService {

    @Inject
    FridgeRepository fridgeRepository;

    @Inject
    FamilyRepository familyRepository;

    @Transactional
    public Fridge createFridge(int familyId) {
        Family family = familyRepository.findById((long) familyId);
        if (family == null) {
            throw new IllegalArgumentException("Family not found for ID: " + familyId);
        }
        Optional<Fridge> existingFridge = fridgeRepository.findFridgeByFamilyId(familyId);
        if (existingFridge.isPresent()) {
            throw new IllegalArgumentException("This family already has a fridge.");
        }

        Fridge fridge = new Fridge();
        fridge.setFamily(family);
        fridgeRepository.persist(fridge);
        return fridge;
    }


    public List<Ingredient> getIngredients(int familyId) {
        List<IngredientFridgeQuantity> ingredientFridgeQuantities = fridgeRepository.getIngredients(familyId);

        return ingredientFridgeQuantities.stream()
                .map(IngredientFridgeQuantity::getIngredient)
                .toList();
    }

    public List<IngredientFridgeQuantity> getIngredientsInFridge(int familyId) {
        return fridgeRepository.findIngredientsInFridge(familyId);
    }


    @Transactional
    public IngredientFridgeQuantity addIngredient(int familyId, IngredientFridgeQuantityInput input) {
        Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamilyId(familyId);
        if (fridgeOpt.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + familyId);
        }
        Optional<Ingredient> ingredientOpt = fridgeRepository.findIngredientByName(input.getIngredientName());
        Ingredient ingredient;
        if (ingredientOpt.isEmpty()) {
            ingredient = new Ingredient();
            ingredient.setName(input.getIngredientName());
            ingredient.setCategory(input.getIngredientCategory());
            fridgeRepository.saveIngredient(ingredient);
        } else {
            ingredient = ingredientOpt.get();
        }
        Optional<IngredientFridgeQuantity> existingQuantityOpt = fridgeRepository.findIngredientFridgeQuantityByFridgeAndIngredient(fridgeOpt.get(), ingredient);
        IngredientFridgeQuantity ingredientFridgeQuantity;

        if (existingQuantityOpt.isPresent()) {
            ingredientFridgeQuantity = existingQuantityOpt.get();
            if (!ingredientFridgeQuantity.getMeasurementUnit().equals(input.getMeasurementUnit())) {
                double convertedQuantity = convertToSmallerUnit(input.getQuantity(), input.getMeasurementUnit(), ingredientFridgeQuantity.getMeasurementUnit());
                ingredientFridgeQuantity.setQuantity(ingredientFridgeQuantity.getQuantity() + convertedQuantity);
                ingredientFridgeQuantity.setMeasurementUnit(ingredientFridgeQuantity.getMeasurementUnit());
            } else {
                ingredientFridgeQuantity.setQuantity(ingredientFridgeQuantity.getQuantity() + input.getQuantity());
            }
            if (ingredientFridgeQuantity.getDate().isAfter(input.getDate())) {
                ingredientFridgeQuantity.setDate(input.getDate());
            }
        } else {
            ingredientFridgeQuantity = new IngredientFridgeQuantity();
            ingredientFridgeQuantity.setFridge(fridgeOpt.get());
            ingredientFridgeQuantity.setIngredient(ingredient);
            ingredientFridgeQuantity.setQuantity(input.getQuantity());
            ingredientFridgeQuantity.setDate(input.getDate());
            ingredientFridgeQuantity.setMeasurementUnit(input.getMeasurementUnit());
        }

        if (ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.G
                || ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.ML) {
            double totalQuantity = Math.round(ingredientFridgeQuantity.getQuantity() * 10.0) / 10.0;
            if (totalQuantity > 1000) {
                IngredientUnit newUnit = (ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.G)
                        ? IngredientUnit.KG : IngredientUnit.L;
                double convertedQuantity = convertToSmallerUnit(totalQuantity, ingredientFridgeQuantity.getMeasurementUnit(), newUnit);
                ingredientFridgeQuantity.setQuantity(convertedQuantity);
                ingredientFridgeQuantity.setMeasurementUnit(newUnit);
            }
        }
        if (ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.KG
                || ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.L) {
            double totalQuantity = Math.round(ingredientFridgeQuantity.getQuantity() * 10.0) / 10.0;
            if (totalQuantity < 1) {
                IngredientUnit newUnit = (ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.KG)
                        ? IngredientUnit.G : IngredientUnit.ML;
                double convertedQuantity = convertToSmallerUnit(totalQuantity, ingredientFridgeQuantity.getMeasurementUnit(), newUnit);
                ingredientFridgeQuantity.setQuantity(convertedQuantity);
                ingredientFridgeQuantity.setMeasurementUnit(newUnit);
            }
        }

        fridgeRepository.saveIngredientFridgeQuantity(ingredientFridgeQuantity);
        return ingredientFridgeQuantity;
    }


    @Transactional
    public IngredientFridgeQuantity updateIngredient(int familyId, int ingredientFridgeQuantityId, IngredientFridgeQuantityInput input) {
        IngredientFridgeQuantity ingredientFridgeQuantity = fridgeRepository.findIngredientFridgeQuantityById(ingredientFridgeQuantityId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found in fridge for ID: " + ingredientFridgeQuantityId));

        if (ingredientFridgeQuantity.getFridge().getFamily().getId() != familyId) {
            throw new IllegalArgumentException("Ingredient does not belong to the specified family fridge.");
        }

        if (!ingredientFridgeQuantity.getIngredient().getName().equalsIgnoreCase(input.getIngredientName())) {
            throw new IllegalArgumentException("Ingredient name cannot be changed. Expected: "
                    + ingredientFridgeQuantity.getIngredient().getName() + ", but got: " + input.getIngredientName());
        }

        IngredientUnit currentUnit = ingredientFridgeQuantity.getMeasurementUnit();
        IngredientUnit newUnit = input.getMeasurementUnit();

        double quantity = input.getQuantity();

        if (!currentUnit.equals(newUnit)) {
            quantity = convertToSmallerUnit(quantity, newUnit, currentUnit);
            input.setMeasurementUnit(currentUnit);
        }

        ingredientFridgeQuantity.setQuantity(quantity);
        ingredientFridgeQuantity.setMeasurementUnit(input.getMeasurementUnit());
        ingredientFridgeQuantity.setDate(input.getDate());

        if (ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.G
                || ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.ML) {
            double totalQuantity = ingredientFridgeQuantity.getQuantity();
            if (totalQuantity > 1000) {
                IngredientUnit newUnitToUse = (ingredientFridgeQuantity.getMeasurementUnit() == IngredientUnit.G)
                        ? IngredientUnit.KG : IngredientUnit.L;
                double convertedQuantity = convertToSmallerUnit(totalQuantity, ingredientFridgeQuantity.getMeasurementUnit(), newUnitToUse);
                ingredientFridgeQuantity.setQuantity(convertedQuantity);
                ingredientFridgeQuantity.setMeasurementUnit(newUnitToUse);
            }
        }

        return fridgeRepository.updateIngredientFridgeQuantity(ingredientFridgeQuantity);
    }

    @Transactional
    public void removeIngredient(int familyId, int ingredientFridgeQuantityId) {
        Optional<IngredientFridgeQuantity> ingredientFridgeQuantityOpt = fridgeRepository.findIngredientInFridge(familyId, ingredientFridgeQuantityId);
        if (ingredientFridgeQuantityOpt.isEmpty()) {
            throw new IllegalArgumentException("Ingredient not found in the fridge for family ID: " + familyId);
        }
        fridgeRepository.deleteIngredientFromFridge(ingredientFridgeQuantityOpt.get());
    }

    @Transactional
    public IngredientFridgeQuantity removeIngredientQuantity(int familyId, int ingredientFridgeQuantityId, IngredientRemove request) {
        Optional<IngredientFridgeQuantity> ingredientFridgeQuantityOpt = fridgeRepository.findIngredientFridgeQuantityById(ingredientFridgeQuantityId);
        if (ingredientFridgeQuantityOpt.isEmpty()) {
            throw new IllegalArgumentException("Ingredient not found in fridge for ID: " + ingredientFridgeQuantityId);
        }
        IngredientFridgeQuantity ingredientFridgeQuantity = ingredientFridgeQuantityOpt.get();
        if (ingredientFridgeQuantity.getFridge().getFamily().getId() != familyId) {
            throw new IllegalArgumentException("Ingredient does not belong to the specified family fridge.");
        }
        if (ingredientFridgeQuantity.getMeasurementUnit() != request.getMeasurementUnit()) {
            ingredientFridgeQuantity.setQuantity(
                    convertToSmallerUnit(ingredientFridgeQuantity.getQuantity(),
                            ingredientFridgeQuantity.getMeasurementUnit(),
                            request.getMeasurementUnit())
            );
            ingredientFridgeQuantity.setMeasurementUnit(request.getMeasurementUnit()); // Cập nhật đơn vị mới
        }
        if (ingredientFridgeQuantity.getQuantity() < request.getAmountToRemove()) {
            throw new IllegalArgumentException("Not enough ingredient quantity to remove.");
        }
        double quantity = Math.round((ingredientFridgeQuantity.getQuantity() - request.getAmountToRemove()) * 10.0) / 10.0;
        ingredientFridgeQuantity.setQuantity(quantity);

        return fridgeRepository.updateIngredientFridgeQuantity(ingredientFridgeQuantity);
    }

    @Transactional
    public IngredientFridgeQuantity getIngredientFridgeQuantity(int familyId, int ingredientFridgeQuantityId) {
        Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamilyId(familyId);
        if (fridgeOpt.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + familyId);
        }
        Optional<IngredientFridgeQuantity> ingredientOpt = fridgeRepository.findIngredientFridgeQuantityById(ingredientFridgeQuantityId);
        if (ingredientOpt.isEmpty()) {
            throw new IllegalArgumentException("Ingredient not found in fridge for ID: " + ingredientFridgeQuantityId);
        }
        return ingredientOpt.get();
    }


    public List<IngredientFridgeQuantity> searchIngredientByName(int familyId, String name) {
        return fridgeRepository.findIngredientByName(familyId, name);
    }


    @Transactional
    public boolean hasUtensil(int familyId, int utensilId) {
        Optional<Fridge> optionalFridge = fridgeRepository.findFridgeByFamilyId(familyId);
        if (optionalFridge.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + familyId);
        }
        Fridge fridge = optionalFridge.get();
        return fridge.getUstensils().stream()
                .anyMatch(utensil -> utensil.getUtensilId() == utensilId);
    }

    @Transactional
    public void addUtensil(int familyId, UtensilInput utensilInput) {
        Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamilyId(familyId);
        if (fridgeOpt.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + familyId);
        }
        Fridge fridge = fridgeOpt.get();

        Optional<Utensil> utensilOpt = fridgeRepository.findUtensilByNameAndFridgeId(utensilInput.getName(), fridge.getFridgeId());
        Utensil utensil;

        if (utensilOpt.isEmpty()) {
            utensil = new Utensil();
            utensil.setName(utensilInput.getName());
            utensil.setFridge(fridge);
            fridgeRepository.saveUtensil(utensil);
        } else {
            utensil = utensilOpt.get();
            if (utensil.getFridge() == null) {
                utensil.setFridge(fridge);
                fridgeRepository.updateUtensil(utensil);
            }
        }

        boolean utensilAlreadyExists = fridge.getUstensils().stream()
                .anyMatch(existingUtensil -> existingUtensil.getName().equalsIgnoreCase(utensil.getName()));

        if (!utensilAlreadyExists) {
            fridge.getUstensils().add(utensil);
            fridgeRepository.persist(fridge);
        }
    }


    @Transactional
    public void removeUtensil(int familyId, int utensilId) {
        Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamilyId(familyId);
        if (fridgeOpt.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + familyId);
        }
        Fridge fridge = fridgeOpt.get();

        Optional<Utensil> utensilOpt = fridgeRepository.findUtensilById(utensilId);
        if (utensilOpt.isEmpty()) {
            throw new IllegalArgumentException("Utensil not found for utensil ID: " + utensilId);
        }
        Utensil utensil = utensilOpt.get();
        fridge.getUstensils().remove(utensil);
        utensil.setFridge(null);
        fridgeRepository.persist(fridge);
        fridgeRepository.flush();
    }

    public List<Utensil> getUtensils(int familyId) {
        Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamilyId(familyId);
        if (fridgeOpt.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + familyId);
        }
        Fridge fridge = fridgeOpt.get();
        return fridgeRepository.getUtensilsByFamilyId(fridge.getFridgeId());
    }

    private double convertToSmallerUnit(double quantity, IngredientUnit fromUnit, IngredientUnit toUnit) {
        if (!fromUnit.getBaseUnit().equals(toUnit.getBaseUnit())) {
            throw new IllegalArgumentException("Incompatible measurement unit conversion: " + fromUnit + " to " + toUnit);
        }
        return quantity * (fromUnit.getConversionFactor() / toUnit.getConversionFactor());
    }


}
