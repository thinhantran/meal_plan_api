package fr.univartois.resource;

import java.util.List;

import fr.univartois.model.Ingredient;
import fr.univartois.services.IngredientService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ingredients")
public class IngredientResource {

  IngredientService service;

  public IngredientResource(IngredientService service) {
    this.service = service;
  }

  @GET
  public List<Ingredient> getIngredients() {
    return service.getAllIngredients();
  }
}
