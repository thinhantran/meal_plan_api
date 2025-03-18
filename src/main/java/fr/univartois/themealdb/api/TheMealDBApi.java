package fr.univartois.themealdb.api;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import fr.univartois.themealdb.dto.CategoriesDTO;
import fr.univartois.themealdb.dto.IngredientsDTO;
import fr.univartois.themealdb.dto.RecipesDTO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@RegisterRestClient(configKey = "themealdb-api")
public interface TheMealDBApi {

  @GET
  @Path("/api/json/v1/1/list.php")
  IngredientsDTO getAllIngredients(@QueryParam("i") String i);

  @GET
  @Path("/api/json/v1/1/list.php")
  CategoriesDTO getAllCategories(@QueryParam("c") String c);

  @GET
  @Path("/api/json/v1/1/filter.php")
  RecipesDTO getAllRecipesByCategory(@QueryParam("c") String c);

  @GET
  @Path("/api/json/v1/1/lookup.php")
  RecipesDTO getRecipe(@QueryParam("i") long id);

  @GET
  @Path("/api/json/v1/1/search.php")
  RecipesDTO getRecipesByFirstLetter(@QueryParam("f") String f);
}
