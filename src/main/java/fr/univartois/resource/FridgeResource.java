package fr.univartois.resource;

import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

import fr.univartois.model.Fridge;
import fr.univartois.model.Ingredient;
import fr.univartois.model.IngredientFridgeQuantity;
import fr.univartois.model.IngredientFridgeQuantityInput;
import fr.univartois.model.IngredientRemove;
import fr.univartois.model.Utensil;
import fr.univartois.model.UtensilInput;
import fr.univartois.services.FridgeService;
import jakarta.annotation.security.RolesAllowed;
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


@Path("/fridge")
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

  FridgeService fridgeService;

  JsonWebToken jwt;

  public FridgeResource(FridgeService fridgeService, JsonWebToken jwt) {
    this.fridgeService = fridgeService;
    this.jwt = jwt;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createFridgeForFamily() {
    Fridge fridge = fridgeService.createFridge(jwt);
    return Response.status(Response.Status.CREATED).entity(fridge).build();
  }

  @GET
  @Path("/ingredients/all")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Ingredient> getIngredients() {
    return fridgeService.getIngredients(jwt);
  }

  @GET
  @Path("/ingredients")
  @Produces(MediaType.APPLICATION_JSON)
  public List<IngredientFridgeQuantity> getIngredientsFromFamilyFridge() {
    return fridgeService.getIngredientsInFridge(jwt);
  }


  @POST
  @Path("/ingredients")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response addIngredientToFamilyFridge(IngredientFridgeQuantityInput ingredientFridgeQuantityInput) {
    IngredientFridgeQuantity addedIngredient = fridgeService.addIngredient(jwt, ingredientFridgeQuantityInput);
    return Response.status(Response.Status.CREATED).entity(addedIngredient).build();
  }

  @POST
  @Path("/ingredients/{ingredientFridgeQuantityId}/remove")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeIngredientQuantity(@PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId,
                                           IngredientRemove request) {
    try {
      IngredientFridgeQuantity updatedIngredient = fridgeService.removeIngredientQuantity(jwt, ingredientFridgeQuantityId, request);
      return Response.ok(updatedIngredient).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("/ingredients/{ingredientFridgeQuantityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response editIngredientFromFamilyFridge(@PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId,
      IngredientFridgeQuantityInput ingredientFridgeQuantityInput) {
    IngredientFridgeQuantity updatedIngredient = fridgeService.updateIngredient(jwt, ingredientFridgeQuantityId, ingredientFridgeQuantityInput);
    return Response.ok(updatedIngredient).build();
  }

  @DELETE
  @Path("/ingredients/{ingredientFridgeQuantityId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeIngredientFromFamilyFridge(@PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId) {
    fridgeService.removeIngredient(jwt, ingredientFridgeQuantityId);
    return Response.noContent().build();
  }

  @GET
  @Path("/ingredients/search/{ingredientFridgeQuantityId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getIngredientFromFamilyFridge(@PathParam("ingredientFridgeQuantityId") int ingredientFridgeQuantityId) {
    IngredientFridgeQuantity ingredient = fridgeService.getIngredientFridgeQuantity(jwt, ingredientFridgeQuantityId);
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
  public Response searchIngredientByName(@PathParam("name") String name) {
    List<IngredientFridgeQuantity> ingredients = fridgeService.searchIngredientByName(jwt, name);
    if (ingredients.isEmpty()) {
      return Response.status(Response.Status.NOT_FOUND).entity("Ingredient not found").build();
    }
    return Response.ok(ingredients).build();
  }

  @GET
  @Path("/utensils")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Utensil> getUtensilsFromFamilyFridge() {
    return fridgeService.getUtensils(jwt);
  }


  @GET
  @Path("/utensils/{utensilId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response doesFamilyHaveUtensil(@PathParam("utensilId") int utensilId) {
    boolean exists = fridgeService.hasUtensil(jwt, utensilId);
    return Response.ok(exists).build();
  }

  @POST
  @Path("/utensils")
  @Produces(MediaType.APPLICATION_JSON)
  public Response addUtensilToFamily(UtensilInput utensil) {
    fridgeService.addUtensil(jwt, utensil);
    return Response.status(Response.Status.CREATED).build();
  }

  @DELETE
  @Path("/utensils/{utensilId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response removeUtensilToFamily(@PathParam("utensilId") int utensilId) {
    fridgeService.removeUtensil(jwt,utensilId);
    return Response.noContent().build();
  }
}
