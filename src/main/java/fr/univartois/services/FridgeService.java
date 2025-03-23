package fr.univartois.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.jwt.JsonWebToken;

import fr.univartois.dtos.IngredientFridgeQuantityInput;
import fr.univartois.dtos.Message;
import fr.univartois.dtos.UtensilInput;
import fr.univartois.model.Family;
import fr.univartois.model.Fridge;
import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientFridgeQuantity;
import fr.univartois.model.IngredientRemove;
import fr.univartois.model.Recipe;
import fr.univartois.model.User;
import fr.univartois.model.Utensil;
import fr.univartois.repository.FamilyRepository;
import fr.univartois.repository.FridgeRepository;
import fr.univartois.repository.IngredientFridgeQuantityRepository;
import fr.univartois.repository.IngredientRepository;
import fr.univartois.repository.UtensilRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class FridgeService {

  public static final String INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID = "Ingredient not found in fridge for ID: ";
  FridgeRepository fridgeRepository;

  FamilyRepository familyRepository;

  IngredientRepository ingredientRepository;

  AuthService authService;

  IngredientFridgeQuantityRepository ingredientFridgeQuantityRepository;

  UtensilRepository utensilRepository;

  RecipeService recipeService;

  public FridgeService(FridgeRepository fridgeRepository, FamilyRepository familyRepository,
      IngredientRepository ingredientRepository, AuthService authService,
      IngredientFridgeQuantityRepository ingredientFridgeQuantityRepository, UtensilRepository utensilRepository, RecipeService recipeService) {
    this.fridgeRepository = fridgeRepository;
    this.familyRepository = familyRepository;
    this.ingredientRepository = ingredientRepository;
    this.authService = authService;
    this.ingredientFridgeQuantityRepository = ingredientFridgeQuantityRepository;
    this.utensilRepository = utensilRepository;
    this.recipeService = recipeService;
  }

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
    for (Ingredient ingredient : ingredientRepository.listAll()) {
      IngredientFridgeQuantity ingredientFridgeQuantity = new IngredientFridgeQuantity();
      ingredientFridgeQuantity.setIngredient(ingredient);
      ingredientFridgeQuantity.setFridge(fridge);
      ingredientFridgeQuantity.setQuantity(0);
      ingredientFridgeQuantityRepository.persist(ingredientFridgeQuantity);
    }
    return fridge;
  }


  public Set<Ingredient> getIngredients(JsonWebToken jsonWebToken) {
    return getIngredientsInFridge(jsonWebToken).stream().map(IngredientFridgeQuantity::getIngredient).collect(
        Collectors.toSet());
  }

  public List<IngredientFridgeQuantity> getIngredientsInFridge(JsonWebToken jsonWebToken) {
    Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
    return ingredientFridgeQuantityRepository.findByFamily(family);
  }

  @Transactional
  public Response addIngredient(JsonWebToken jsonWebToken, IngredientFridgeQuantityInput input) {
    User user = authService.findUser(jsonWebToken.getSubject());
    Family family = familyRepository.findByUser(user);
    List<IngredientFridgeQuantity> existingList = ingredientFridgeQuantityRepository.findByFamily(family);
    Ingredient ingredient = ingredientRepository.findByName(input.getIngredientName());

    if (existingList.stream().map(IngredientFridgeQuantity::getIngredient).anyMatch(ingredient::equals)) {
      IngredientFridgeQuantity existingQuantity = existingList.stream().filter(i -> ingredient.equals(i.getIngredient()))
          .toList().getFirst();
      if (existingQuantity.getQuantity() != 0) {
        return Response.status(Response.Status.CONFLICT).entity(new Message("Ingredient is already present")).build();
      } else {
        ingredientFridgeQuantityRepository.delete(existingQuantity);
      }
    }
    IngredientFridgeQuantity ingredientFridgeQuantity = new IngredientFridgeQuantity();
    Fridge fridge = family.getFridge();
    ingredientFridgeQuantity.setQuantity(input.getQuantity());
    ingredientFridgeQuantity.setIngredient(ingredient);
    ingredientFridgeQuantity.setFridge(fridge);
    ingredientFridgeQuantityRepository.persist(ingredientFridgeQuantity);
    return Response.status(Response.Status.CREATED).entity(ingredientFridgeQuantity).build();
  }

  @Transactional
  public IngredientFridgeQuantity updateIngredient(JsonWebToken jsonWebToken, int ingredientFridgeQuantityId,
      IngredientFridgeQuantityInput input) {
    Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
    IngredientFridgeQuantity ingredientFridgeQuantity = fridgeRepository.findIngredientFridgeQuantityById(
            ingredientFridgeQuantityId)
        .orElseThrow(
            () -> new IllegalArgumentException(INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID + ingredientFridgeQuantityId));

    if (ingredientFridgeQuantity.getFridge().getFamily().getId() != family.getId()) {
      throw new IllegalArgumentException("Ingredient does not belong to the specified family fridge.");
    }

    if (!ingredientFridgeQuantity.getIngredient().getName().equalsIgnoreCase(input.getIngredientName())) {
      throw new IllegalArgumentException("Ingredient name cannot be changed. Expected: "
          + ingredientFridgeQuantity.getIngredient().getName() + ", but got: " + input.getIngredientName());
    }

    if (input.getMeasurementUnit() != null) {
      ingredientFridgeQuantity.setMeasurementUnit(ingredientFridgeQuantity.getMeasurementUnit());
    }

    if (input.getQuantity() != null) {
      ingredientFridgeQuantity.setQuantity(input.getQuantity());
    }

    return fridgeRepository.updateIngredientFridgeQuantity(ingredientFridgeQuantity);
  }

  @Transactional
  public void removeIngredient(JsonWebToken jsonWebToken, int ingredientFridgeQuantityId) {
    Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
    Optional<IngredientFridgeQuantity> ingredientFridgeQuantityOpt = fridgeRepository.findIngredientInFridge(
        family.getId(), ingredientFridgeQuantityId);
    if (ingredientFridgeQuantityOpt.isEmpty()) {
      throw new IllegalArgumentException("Ingredient not found in the fridge for family ID: " + family.getId());
    }
    fridgeRepository.deleteIngredientFromFridge(ingredientFridgeQuantityOpt.get());
  }

  @Transactional
  public IngredientFridgeQuantity removeIngredientQuantity(JsonWebToken jsonWebToken, int ingredientFridgeQuantityId,
      IngredientRemove request) {
    Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
    Optional<IngredientFridgeQuantity> ingredientFridgeQuantityOpt = fridgeRepository.findIngredientFridgeQuantityById(
        ingredientFridgeQuantityId);
    if (ingredientFridgeQuantityOpt.isEmpty()) {
      throw new IllegalArgumentException(INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID + ingredientFridgeQuantityId);
    }
    IngredientFridgeQuantity ingredientFridgeQuantity = ingredientFridgeQuantityOpt.get();
    if (ingredientFridgeQuantity.getFridge().getFamily().getId() != family.getId()) {
      throw new IllegalArgumentException("Ingredient does not belong to the specified family fridge.");
    }
    if (request.getMeasurementUnit() != null) {
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
  public IngredientFridgeQuantity getIngredientFridgeQuantity(JsonWebToken jsonWebToken,
      int ingredientFridgeQuantityId) {
    Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
    Fridge fridgeOpt = fridgeRepository.findFridgeByFamily(family);
    if (fridgeOpt == null) {
      throw new IllegalArgumentException(INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID + family.getId());
    }
    Optional<IngredientFridgeQuantity> ingredientOpt = fridgeRepository.findIngredientFridgeQuantityById(
        ingredientFridgeQuantityId);
    if (ingredientOpt.isEmpty()) {
      throw new IllegalArgumentException(INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID + ingredientFridgeQuantityId);
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
      throw new IllegalArgumentException(INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID + family.getId());
    }
    return optionalFridge.getUstensils().stream()
        .anyMatch(utensil -> utensil.getUtensilId() == utensilId);
  }

  @Transactional
  public void addUtensil(JsonWebToken jsonWebToken, UtensilInput utensilInput) {
    Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
    Fridge fridgeOpt = fridgeRepository.findFridgeByFamily(family);
    if (fridgeOpt == null) {
      throw new IllegalArgumentException(INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID + family.getId());
    }

    Utensil utensilOpt = utensilRepository.findByNameAndFridge(utensilInput.getName(), fridgeOpt);

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
    Fridge fridge = checkForEmptyFridge(jsonWebToken);

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

  private Fridge checkForEmptyFridge(JsonWebToken jsonWebToken) {
    Family family = authService.findUser(jsonWebToken.getSubject()).getMemberRole().getFamily();
    Optional<Fridge> fridgeOpt = fridgeRepository.findFridgeByFamily(family.getId());
    if (fridgeOpt.isEmpty()) {
      throw new IllegalArgumentException(INGREDIENT_NOT_FOUND_IN_FRIDGE_FOR_ID + family.getId());
    }
    return fridgeOpt.get();
  }

  public List<Utensil> getUtensils(JsonWebToken jsonWebToken) {
    Fridge fridge = checkForEmptyFridge(jsonWebToken);
    return fridgeRepository.getUtensilsByFamilyId(fridge.getFridgeId());
  }

  public Response getIngredientsByRecipeId(JsonWebToken jsonWebToken, long recipeId) {
    User user = authService.findUser(jsonWebToken.getSubject());
    if (user == null || user.getMemberRole() == null || user.getMemberRole().getFamily() == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    Family family = user.getMemberRole().getFamily();
    Recipe recipe = recipeService.getRecipeById(recipeId);
    Set<Long> recipeIds = recipe.getIngredients().stream().map(i -> i.getIngredient().getIngredientId())
        .collect(Collectors.toSet());
    List<IngredientFridgeQuantity> ingredientFridgeQuantities = ingredientFridgeQuantityRepository.findByFamily(family)
        .stream().filter(iFQ -> recipeIds.contains(iFQ.getIngredient().getIngredientId())).toList();
    return Response.ok().entity(ingredientFridgeQuantities).build();
  }
}
