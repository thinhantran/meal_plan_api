package fr.univartois.repository;

import java.util.List;
import java.util.Optional;

import fr.univartois.model.Family;
import fr.univartois.model.Fridge;
import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientFridgeQuantity;
import fr.univartois.model.Utensil;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class FridgeRepository implements PanacheRepository<Fridge> {

  public static final String FAMILY_ID = "familyId";
  @PersistenceContext
  EntityManager entityManager;

  public List<IngredientFridgeQuantity> getIngredients(int familyId) {
    return entityManager.createQuery(
            "SELECT i FROM IngredientFridgeQuantity i WHERE i.fridge.family.id = :familyId",
            IngredientFridgeQuantity.class)
        .setParameter(FAMILY_ID, familyId)
        .getResultList();
  }

  public List<IngredientFridgeQuantity> findIngredientsInFridge(int familyId) {
    return entityManager.createQuery(
            "SELECT i FROM IngredientFridgeQuantity i WHERE i.fridge.family.id = :familyId",
            IngredientFridgeQuantity.class)
        .setParameter(FAMILY_ID, familyId)
        .getResultList();
  }


  public Optional<Fridge> findFridgeByFamily(long familyId) {
    return Optional.ofNullable(find("family.id", familyId).firstResult());
  }

  public Fridge findFridgeByFamily(Family family) {
    return find("family", family).firstResult();
  }

  public Optional<Ingredient> findIngredientByName(String name) {
    return entityManager.createQuery(
            "SELECT i FROM Ingredient i WHERE i.name = :name", Ingredient.class)
        .setParameter("name", name)
        .getResultStream()
        .findFirst();
  }

  public void saveIngredient(Ingredient ingredient) {
    entityManager.persist(ingredient);
  }

  public void saveUtensil(Utensil utensil) {
    entityManager.persist(utensil);
  }

  public void saveIngredientFridgeQuantity(IngredientFridgeQuantity ingredientFridgeQuantity) {
    entityManager.persist(ingredientFridgeQuantity);
  }

  public Optional<IngredientFridgeQuantity> findIngredientFridgeQuantityById(int ingredientFridgeQuantityId) {
    return Optional.ofNullable(entityManager.find(IngredientFridgeQuantity.class, ingredientFridgeQuantityId));
  }

  public IngredientFridgeQuantity updateIngredientFridgeQuantity(IngredientFridgeQuantity ingredientFridgeQuantity) {
    return entityManager.merge(ingredientFridgeQuantity);
  }

  public Optional<IngredientFridgeQuantity> findIngredientInFridge(long familyId, int ingredientFridgeQuantityId) {
    return entityManager.createQuery(
            "SELECT i FROM IngredientFridgeQuantity i WHERE i.fridge.family.id = :familyId AND i" +
                ".ingredientFridgeQuantityId = :ingredientFridgeQuantityId",
            IngredientFridgeQuantity.class)
        .setParameter(FAMILY_ID, familyId)
        .setParameter("ingredientFridgeQuantityId", ingredientFridgeQuantityId)
        .getResultStream()
        .findFirst();
  }

  public void deleteIngredientFromFridge(IngredientFridgeQuantity ingredientFridgeQuantity) {
    entityManager.remove(entityManager.contains(ingredientFridgeQuantity)
        ? ingredientFridgeQuantity
        : entityManager.merge(ingredientFridgeQuantity));
  }

  public List<IngredientFridgeQuantity> findIngredientByName(long familyId, String name) {
    return entityManager.createQuery(
            "SELECT i FROM IngredientFridgeQuantity i WHERE i.fridge.family.id = :familyId AND LOWER(i.ingredient" +
                ".name) LIKE LOWER(:name)",
            IngredientFridgeQuantity.class)
        .setParameter(FAMILY_ID, familyId)
        .setParameter("name", "%" + name + "%")
        .getResultList();
  }

  public Optional<Utensil> findUtensilById(int utensilId) {
    return Optional.ofNullable(entityManager.find(Utensil.class, utensilId));
  }


  public void updateUtensil(Utensil utensil) {
    entityManager.merge(utensil);
  }

  public List<Utensil> getUtensilsByFamilyId(int fridgeId) {
    return entityManager.createQuery(
            "SELECT u FROM Utensil u WHERE u.fridge.fridgeId = :fridgeId", Utensil.class)
        .setParameter("fridgeId", fridgeId)
        .getResultList();
  }

  public Optional<IngredientFridgeQuantity> findIngredientFridgeQuantityByFridgeAndIngredient(Fridge fridge,
      Ingredient ingredient) {
    return entityManager.createQuery(
            "SELECT i FROM IngredientFridgeQuantity i WHERE i.fridge = :fridge AND i.ingredient = :ingredient",
            IngredientFridgeQuantity.class)
        .setParameter("fridge", fridge)
        .setParameter("ingredient", ingredient)
        .getResultStream()
        .findFirst();
  }
}
