package fr.univartois.resource;

import java.util.ArrayList;
import java.util.List;

import fr.univartois.model.Ingredient;
import fr.univartois.services.IngredientService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ingredients")
public class IngredientResource {

  @Inject
  IngredientService service;

  @GET
  public List<Ingredient> getIngredients() {
    return service.getAllIngredients();
  }
}
