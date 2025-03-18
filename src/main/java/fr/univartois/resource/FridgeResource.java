package fr.univartois.resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import fr.univartois.model.Fridge;
import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientCategory;
import fr.univartois.model.IngredientFridgeQuantity;
import fr.univartois.model.IngredientFridgeQuantityInput;
import fr.univartois.model.IngredientRemove;
import fr.univartois.model.Utensil;
import fr.univartois.model.UtensilInput;
import fr.univartois.services.FridgeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/fridge/{familyId}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecuritySchemes(value = {
        @SecurityScheme(
                bearerFormat = "JWT",
                scheme = "bearer",
                securitySchemeName = "AccessBearerAuthentication",
                apiKeyName = "Authorization",
                type = SecuritySchemeType.HTTP,
                description = "Uses the access token provided at authentication (Header \"Authentification\", Value \"Bearer xxx\")",
                in = SecuritySchemeIn.HEADER
        )
})
@RolesAllowed("access")
@SecurityRequirement(name = "AccessBearerAuthentication")
public class FridgeResource {

  @Inject
  FridgeService fridgeService;

  @Inject
  JsonWebToken jwt;

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createFridgeForFamily(@PathParam("familyId") int familyId) {
    Fridge fridge = fridgeService.createFridge(familyId);
    return Response.status(Response.Status.CREATED).entity(fridge).build();
  }

  @GET
  @Path("/ingredients/all")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Ingredient> getIngredients(@PathParam("familyId") int familyId) {
    return fridgeService.getIngredients(familyId);
  }

  @GET
  @Path("/ingredients")
  @Produces(MediaType.APPLICATION_JSON)
  public List<IngredientFridgeQuantity> getIngredientsFromFamilyFridge(@PathParam("familyId") int familyId) {
    return fridgeService.getIngredientsInFridge(familyId);
  }


  @POST
  @Path("/ingredients")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addIngredientToFamilyFridge(@PathParam("familyId") int familyId,
                                              IngredientFridgeQuantityInput ingredientFridgeQuantityInput) {
    IngredientFridgeQuantity addedIngredient = fridgeService.addIngredient(familyId, ingredientFridgeQuantityInput);
    return Response.status(Response.Status.CREATED).entity(addedIngredient).build();
  }

  @POST
  @Path("/ingredients/{ingredientFridgeQuantityId}/remove")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeIngredientQuantity(@PathParam("familyId") int familyId,
                                           @PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId,
                                           IngredientRemove request) {
    try {
      IngredientFridgeQuantity updatedIngredient = fridgeService.removeIngredientQuantity(familyId, ingredientFridgeQuantityId, request);
      return Response.ok(updatedIngredient).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/ingredients/{ingredientFridgeQuantityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response editIngredientFromFamilyFridge(@PathParam("familyId") int familyId,
      @PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId,
      IngredientFridgeQuantityInput ingredientFridgeQuantityInput) {
    IngredientFridgeQuantity updatedIngredient = fridgeService.updateIngredient(familyId, ingredientFridgeQuantityId, ingredientFridgeQuantityInput);
    return Response.ok(updatedIngredient).build();
  }

  @DELETE
  @Path("/ingredients/{ingredientFridgeQuantityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeIngredientFromFamilyFridge(@PathParam("familyId") int familyId,
      @PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId) {
    fridgeService.removeIngredient(familyId, ingredientFridgeQuantityId);
    return Response.noContent().build();
  }

  @GET
  @Path("/ingredients/search/{ingredientFridgeQuantityId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getIngredientFromFamilyFridge(@PathParam("familyId") int familyId,
                                                @PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId) {
    IngredientFridgeQuantity ingredient = fridgeService.getIngredientFridgeQuantity(familyId, ingredientFridgeQuantityId);
    System.out.println("Ingredient :" +ingredient);
    if (ingredient != null) {
      return Response.ok(ingredient).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND)
              .entity("{\"message\":\"Ingredient not found !!!!!!!\"}")
              .build();
    }
  }

  @GET
  @Path("/ingredients/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response searchIngredientByName(@PathParam("familyId") int familyId,
                                         @PathParam("name") String name) {
    System.out.println("Entering searchIngredientByName with name = " + name);
    List<IngredientFridgeQuantity> ingredients = fridgeService.searchIngredientByName(familyId, name);
    if (ingredients.isEmpty()) {
      return Response.status(Response.Status.NOT_FOUND).entity("Ingredient not found fd,gdfgt,dt").build();
    }
    return Response.ok(ingredients).build();
  }


  @GET
  @Path("/categories")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<IngredientCategory, List<Ingredient>> getIngredientsGroupedByCategory(int familyId) {
    List<Ingredient> ingredients = fridgeService.getIngredients(familyId);
    return ingredients.stream().collect(Collectors.groupingBy(Ingredient::getCategory));
  }
  @GET
  @Path("/utensils")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Utensil> getUtensilsFromFamilyFridge(@PathParam("familyId") int familyId) {
    return fridgeService.getUtensils(familyId);
  }


  @GET
  @Path("/utensils/{utensilId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response doesFamilyHaveUtensil(@PathParam("familyId") int familyId, @PathParam("utensilId") int utensilId) {
    boolean exists = fridgeService.hasUtensil(familyId, utensilId);
    return Response.ok(exists).build();
  }

  @POST
  @Path("/utensils")
  @Produces(MediaType.APPLICATION_JSON)
  public Response addUtensilToFamily(@PathParam("familyId") int familyId, UtensilInput utensil) {
    fridgeService.addUtensil(familyId, utensil);
    return Response.status(Response.Status.CREATED).build();
  }

  @DELETE
  @Path("/utensils/{utensilId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeUtensilToFamily(@PathParam("familyId") int familyId, @PathParam("utensilId") int utensilId) {
    fridgeService.removeUtensil(familyId,utensilId);
    return Response.noContent().build();
  }
}
