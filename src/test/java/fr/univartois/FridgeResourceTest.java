package fr.univartois;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import fr.univartois.model.Family;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FridgeResourceTest {

    private static long FAMILY_ID;
    private static long INGREDIENT_FRIDGE_ID;

    static void setup() {

        String username = "testUser";

        Family createdFamily = given().when()
                .contentType("application/json")
                .post("/families/{username}", username)
                .then()
                .statusCode(201)
                .extract().body().as(Family.class);

        FAMILY_ID = createdFamily.getId();

        given().when()
                .contentType("application/json")
                .post("/fridge/{familyId}/create", FAMILY_ID)
                .then()
                .statusCode(201)
                .body("fridgeId", is(notNullValue()));
    }

    @Test
    @Order(8)
    void testGetIngredients() {
        given().when()
                .get("/fridge/{familyId}/ingredients/all", FAMILY_ID)
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }

    @Test
    @Order(7)
    void testGetIngredientsFromFamilyFridge() {
        given().when()
                .get("/fridge/{familyId}/ingredients", FAMILY_ID)
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

        INGREDIENT_FRIDGE_ID = given().when()
                .contentType("application/json")
                .body(requestBody)
                .post("/fridge/{familyId}/ingredients", FAMILY_ID)
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
                .body(requestBody)
                .put("/fridge/{familyId}/ingredients/{ingredientFridgeQuantityId}", FAMILY_ID, INGREDIENT_FRIDGE_ID)
                .then()
                .statusCode(200)
                .body("quantity", equalTo(10.0F));
    }

    @Test
    @Order(11)
    void testRemoveIngredientFromFamilyFridge() {
        given().when()
                .delete("/fridge/{familyId}/ingredients/{ingredientFridgeQuantityId}", FAMILY_ID,INGREDIENT_FRIDGE_ID)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    void testGetIngredientFromFamilyFridge() {
        given().when()
                .get("/fridge/{familyId}/ingredients/search/{ingredientFridgeQuantityId}", FAMILY_ID, INGREDIENT_FRIDGE_ID)
                .then()
                .statusCode(200)
                .log().body()
                .body("ingredientFridgeQuantityId", is(INGREDIENT_FRIDGE_ID));
    }

    @Test
    @Order(3)
    void testSearchIngredientByName() {
        given().when()
                .get("/fridge/{familyId}/ingredients/Tomate", FAMILY_ID)
                .then()
                .statusCode(200)
                .body("[0].ingredient.name", is("Tomate"));
    }

    @Test
    @Order(4)
    void testGetIngredientsGroupedByCategory() {
        given().when()
                .get("/fridge/{familyId}/categories", FAMILY_ID)
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }

    @Test
    @Order(5)
    void testGetUtensilsFromFamilyFridge() {
        given().when()
                .get("/fridge/{familyId}/utensils", FAMILY_ID)
                .then()
                .statusCode(200)
                .body(is(not(empty())));
    }

    @Test
    @Order(6)
    void testAddUtensilToFamily() {

        given().when()
                .contentType("application/json")
                .body("{\"name\": \"Knife\"}")
                .post("/fridge/{familyId}/utensils", FAMILY_ID)
                .then()
                .statusCode(201)
                .log().body();
    }

    @Test
    @Order(12)
    void testRemoveUtensilToFamily() {
        given().when()
                .delete("/fridge/{familyId}/utensils/{utensilId}", FAMILY_ID, 1)
                .then()
                .statusCode(204);
    }
}
