package fr.univartois.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import fr.univartois.model.*;
import fr.univartois.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class FridgeService {

    @Inject
    FridgeRepository fridgeRepository;

    @Inject
    FamilyRepository familyRepository;

    @Inject
    IngredientRepository ingredientRepository;

    @Inject
    AuthService authService;

    @Inject
    IngredientFridgeQuantityRepository ingredientFridgeQuantityRepository;

    @Inject
    UtensilRepository utensilRepository;

    @Transactional
    public Fridge createFridge(JsonWebToken jwt) {
        User user = authService.findUser(jwt.getSubject());
        if (user == null) {
            throw new IllegalArgumentException("Invalid user");
        }
        Family family = familyRepository.findByUser(user);
        if (family == null) {
            throw new IllegalArgumentException("Family not found for " + user.getUsername());
        }
        if (family.getFridge() != null) {
            throw new IllegalArgumentException("This family already has a fridge.");
        }
        Fridge fridge = new Fridge();
        fridge.setFamily(family);
        family.setFridge(fridge);
        fridgeRepository.persist(fridge);
        return fridge;
    }


    public Set<Ingredient> getIngredients(JsonWebToken jsonWebToken) {
        return getIngredientsInFridge(jsonWebToken).stream().map(IngredientFridgeQuantity::getIngredient).collect(Collectors.toSet());
    }

    public List<IngredientFridgeQuantity> getIngredientsInFridge(JsonWebToken jsonWebToken) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        return ingredientFridgeQuantityRepository.findByFamily(family);
    }

    /*
    @Transactional
    public IngredientFridgeQuantity addIngredient(JsonWebToken jsonWebToken, IngredientFridgeQuantityInput input) {
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
    }*/

    @Transactional
    public IngredientFridgeQuantity addIngredient(JsonWebToken jsonWebToken, IngredientFridgeQuantityInput input) {
        IngredientFridgeQuantity ingredientFridgeQuantity = new IngredientFridgeQuantity();
        Ingredient ingredient = ingredientRepository.findByName(input.getIngredientName());
        User user = authService.findUser(jsonWebToken.getSubject());
        Fridge fridge = user.getMemberRole().getFamily().getFridge();
        ingredientFridgeQuantity.setQuantity(input.getQuantity());
        ingredientFridgeQuantity.setIngredient(ingredient);
        ingredientFridgeQuantity.setFridge(fridge);
        ingredientFridgeQuantityRepository.persist(ingredientFridgeQuantity);
        return ingredientFridgeQuantity;
    }

    @Transactional
    public IngredientFridgeQuantity updateIngredient(JsonWebToken jsonWebToken, int ingredientFridgeQuantityId, IngredientFridgeQuantityInput input) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        IngredientFridgeQuantity ingredientFridgeQuantity = fridgeRepository.findIngredientFridgeQuantityById(ingredientFridgeQuantityId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found in fridge for ID: " + ingredientFridgeQuantityId));

        if (ingredientFridgeQuantity.getFridge().getFamily().getId() != family.getId()) {
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
    public void removeIngredient(JsonWebToken jsonWebToken, int ingredientFridgeQuantityId) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        Optional<IngredientFridgeQuantity> ingredientFridgeQuantityOpt = fridgeRepository.findIngredientInFridge(family.getId(), ingredientFridgeQuantityId);
        if (ingredientFridgeQuantityOpt.isEmpty()) {
            throw new IllegalArgumentException("Ingredient not found in the fridge for family ID: " + family.getId());
        }
        fridgeRepository.deleteIngredientFromFridge(ingredientFridgeQuantityOpt.get());
    }

    @Transactional
    public IngredientFridgeQuantity removeIngredientQuantity(JsonWebToken jsonWebToken, int ingredientFridgeQuantityId, IngredientRemove request) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        Optional<IngredientFridgeQuantity> ingredientFridgeQuantityOpt = fridgeRepository.findIngredientFridgeQuantityById(ingredientFridgeQuantityId);
        if (ingredientFridgeQuantityOpt.isEmpty()) {
            throw new IllegalArgumentException("Ingredient not found in fridge for ID: " + ingredientFridgeQuantityId);
        }
        IngredientFridgeQuantity ingredientFridgeQuantity = ingredientFridgeQuantityOpt.get();
        if (ingredientFridgeQuantity.getFridge().getFamily().getId() != family.getId()) {
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
    public IngredientFridgeQuantity getIngredientFridgeQuantity(JsonWebToken jsonWebToken, int ingredientFridgeQuantityId) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        Fridge fridgeOpt = fridgeRepository.findFridgeByFamily(family);
        if (fridgeOpt == null) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + family.getId());
        }
        Optional<IngredientFridgeQuantity> ingredientOpt = fridgeRepository.findIngredientFridgeQuantityById(ingredientFridgeQuantityId);
        if (ingredientOpt.isEmpty()) {
            throw new IllegalArgumentException("Ingredient not found in fridge for ID: " + ingredientFridgeQuantityId);
        }
        return ingredientOpt.get();
    }


    public List<IngredientFridgeQuantity> searchIngredientByName(JsonWebToken jsonWebToken, String name) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        return fridgeRepository.findIngredientByName(family.getId(), name);
    }


    @Transactional
    public boolean hasUtensil(JsonWebToken jsonWebToken, int utensilId) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        Fridge optionalFridge = fridgeRepository.findFridgeByFamily(family);
        if (optionalFridge == null) {
            throw new IllegalArgumentException("Fridge not found for family ID: ");
        }
        return optionalFridge.getUstensils().stream()
                .anyMatch(utensil -> utensil.getUtensilId() == utensilId);
    }

    @Transactional
    public void addUtensil(JsonWebToken jsonWebToken, UtensilInput utensilInput) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        Fridge fridgeOpt = fridgeRepository.findFridgeByFamily(family);
        if (fridgeOpt == null) {
            throw new IllegalArgumentException("Fridge not found for family ID: ");
        }

        Utensil utensilOpt = utensilRepository.findByNameAndFridge(utensilInput.getName(), fridgeOpt);
        Utensil utensil;

        if (utensilOpt == null) {
            utensilOpt = new Utensil();
            utensilOpt.setName(utensilInput.getName());
            utensilOpt.setFridge(fridgeOpt);
            fridgeRepository.saveUtensil(utensilOpt);
        } else {
            if (utensilOpt.getFridge() == null) {
                utensilOpt.setFridge(fridgeOpt);
                fridgeRepository.updateUtensil(utensilOpt);
            }
        }

        Utensil finalUtensilOpt = utensilOpt;
        boolean utensilAlreadyExists = fridgeOpt.getUstensils().stream()
                .anyMatch(existingUtensil -> existingUtensil.getName().equalsIgnoreCase(finalUtensilOpt.getName()));

        if (!utensilAlreadyExists) {
            fridgeOpt.getUstensils().add(utensilOpt);
            fridgeRepository.persist(fridgeOpt);
        }
    }


    @Transactional
    public void removeUtensil(JsonWebToken jsonWebToken, int utensilId) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamily(family.getId());
        if (fridgeOpt.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: " + family.getId());
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

    public List<Utensil> getUtensils(JsonWebToken jsonWebToken) {
        Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
        Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamily(family.getId());
        if (fridgeOpt.isEmpty()) {
            throw new IllegalArgumentException("Fridge not found for family ID: ");
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
