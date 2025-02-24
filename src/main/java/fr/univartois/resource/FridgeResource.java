package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientFridgeQuantity;
import fr.univartois.model.IngredientFridgeQuantityInput;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/fridge/{familyId}")
public class FridgeResource {

  @GET
  @Path("/ingredients")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Ingredient> getIngredientsFromFamilyFridge(@PathParam("familyId") int familyId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @POST
  @Path("/ingredients/{ingredientId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public IngredientFridgeQuantity addIngredientToFamilyFridge(@PathParam("familyId") int familyId,
      @PathParam("ingredientId") int ingredientId, IngredientFridgeQuantityInput ingredientFridgeQuantityInput) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @PUT
  @Path("/ingredients/{ingredientFridgeQuantityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public IngredientFridgeQuantity editIngredientFromFamilyFridge(@PathParam("familyId") int familyId,
      @PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId,
      IngredientFridgeQuantityInput ingredientFridgeQuantityInput) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @DELETE
  @Path("/ingredients/{ingredientFridgeQuantityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public void removeIngredientFromFamilyFridge(@PathParam("familyId") int familyId,
      @PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @GET
  @Path("/utensils/{utensilId}")
  @Produces(MediaType.APPLICATION_JSON)
  public boolean doesFamilyHaveUtensil(@PathParam("familyId") int familyId, @PathParam("utensilId") int utensilId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @POST
  @Path("/utensils/{utensilId}")
  @Produces(MediaType.APPLICATION_JSON)
  public void addUtensilToFamily(@PathParam("familyId") int familyId, @PathParam("utensilId") int utensilId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @DELETE
  @Path("/utensils/{utensilId}")
  @Produces(MediaType.APPLICATION_JSON)
  public void removeUtensilToFamily(@PathParam("familyId") int familyId, @PathParam("utensilId") int utensilId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
