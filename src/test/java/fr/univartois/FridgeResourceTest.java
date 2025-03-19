package fr.univartois;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import fr.univartois.dtos.CustomJwtPair;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import fr.univartois.model.Family;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FridgeResourceTest {

    private static long familyId;
    private static long ingredientFridgeId;
    private static CustomJwtPair customJwtPair;

    static void setup() {

        String username = "python";

        customJwtPair = given().when()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("username", username)
                .formParam("password", "admin")
                .post("/auth/login/user")
                .then()
                .statusCode(200)
                .extract().as(CustomJwtPair.class);

        Family createdFamily = given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .post("/families")
                .then()
                .statusCode(201)
                .extract().body().as(Family.class);

        familyId = createdFamily.getId();

        given().when()
                .contentType("application/json")
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .post("/fridge")
                .then()
                .statusCode(201)
                .body("fridgeId", is(notNullValue()));
    }

    @Test
    @Order(8)
    void testGetIngredients() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .get("/fridge/ingredients/all")
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }

    @Test
    @Order(7)
    void testGetIngredientsFromFamilyFridge() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .get("/fridge/ingredients")
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }

    @Test
    @Order(1)
    void testAddIngredientToFamilyFridge() {
        setup();
        String requestBody = """
        {
          "ingredientName": "Tomate",
          "date": "2025-03-10",
          "quantity": 5,
          "measurementUnit": "kg",
          "ingredientCategory": "LEGUMES"
        }
        """;

        ingredientFridgeId = given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .contentType("application/json")
                .body(requestBody)
                .post("/fridge/ingredients")
                .then()
                .statusCode(201)
                .body("ingredient.name", is("Tomate"))
                .extract().path("ingredientFridgeQuantityId");
    }

    @Test
    @Order(2)
    void testEditIngredientFromFamilyFridge() {
        String requestBody = """
        {
          "ingredientName": "Tomate",
          "date": "2025-03-10",
          "quantity": 10,
          "measurementUnit": "kg",
          "ingredientCategory": "LEGUMES"
        }
        """;

        given().when()
                .contentType("application/json")
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .body(requestBody)
                .put("/fridge/ingredients/{ingredientFridgeQuantityId}", ingredientFridgeId)
                .then()
                .statusCode(200)
                .body("quantity", equalTo(10.0F));
    }

    @Test
    @Order(11)
    void testRemoveIngredientFromFamilyFridge() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .delete("/fridge/ingredients/{ingredientFridgeQuantityId}", ingredientFridgeId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    void testGetIngredientFromFamilyFridge() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .get("/fridge/ingredients/search/{ingredientFridgeQuantityId}", ingredientFridgeId)
                .then()
                .statusCode(200)
                .log().body()
                .body("ingredientFridgeQuantityId", is(ingredientFridgeId));
    }

    @Test
    @Order(3)
    void testSearchIngredientByName() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .get("/fridge/ingredients/Tomate")
                .then()
                .statusCode(200)
                .body("[0].ingredient.name", is("Tomate"));
    }

    @Test
    @Order(4)
    void testGetIngredientsGroupedByCategory() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .get("/fridge/categories")
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }

    @Test
    @Order(5)
    void testGetUtensilsFromFamilyFridge() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .get("/fridge/utensils")
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }

    @Test
    @Order(6)
    void testAddUtensilToFamily() {

        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .contentType("application/json")
                .body("{\"name\": \"Knife\"}")
                .post("/fridge/utensils")
                .then()
                .statusCode(201)
                .log().body();
    }

    @Test
    @Order(12)
    void testRemoveUtensilToFamily() {
        given().when()
                .header("Authorization", "Bearer " + customJwtPair.accessToken())
                .delete("/fridge/utensils/{utensilId}", 1)
                .then()
                .statusCode(204);
    }
}
