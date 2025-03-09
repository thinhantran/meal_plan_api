package fr.univartois;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

@QuarkusTest
class AuthTest {

    @Test
    void testSignUpSuccessful() {
        given().when()
                .formParam("username", "username")
                .formParam("password", "password")
                .post("/auth/users")
                .then()
                .statusCode(201)
                .body("message", is("Registered successfully"));
    }

    @Test
    void testSignUpFailed() {
        given().when()
                .formParam("username", "java")
                .formParam("password", "password")
                .post("/auth/users")
                .then()
                .statusCode(409);
    }

    @Test
    void testLoginSuccessful() {
        given().when()
                .formParam("username", "java")
                .formParam("password", "admin")
                .post("/auth/login/user")
                .then()
                .statusCode(200)
                .body("accessToken", is(notNullValue()), "refreshToken", is(notNullValue()));
    }

    @Test
    void testLoginFailed() {
        given().when()
                .formParam("username", "username")
                .formParam("password", "password")
                .post("/auth/login/user")
                .then()
                .statusCode(401);
    }

    @Test
    void testLoginTokenSuccessful() {
        String refreshToken = given().when()
                .formParam("username", "java")
                .formParam("password", "admin")
                .post("/auth/login/user")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getString("refreshToken");
        given().when()
                .header("Authorization", "Bearer " + refreshToken)
                .post("/auth/login/token")
                .then()
                .statusCode(200);
    }

    @Test
    void testLoginTokenFailed() {
        given().when()
                .header("Authorization", "Bearer 123456")
                .post("/auth/login/token")
                .then()
                .statusCode(401);
        given().when()
            .post("/auth/login/token")
            .then()
            .statusCode(401);

        String accessToken = given().when()
            .formParam("username", "java")
            .formParam("password", "admin")
            .post("/auth/login/user")
            .then()
            .statusCode(200)
            .extract().body().jsonPath().getString("accessToken");

        given().when()
            .header("Authorization", "Bearer " + accessToken)
            .post("/auth/login/token")
            .then()
            .statusCode(403);
    }

    @Test
    void testLogoutSuccessful() {
        Response response = given().when()
                .formParam("username", "java")
                .formParam("password", "admin")
                .post("/auth/login/user");
        String refreshToken = response.getBody().jsonPath().getString("refreshToken");
        given().when()
            .header("Authorization", "Bearer " + refreshToken)
            .post("/auth/logout")
            .then()
            .statusCode(204);
        given().when()
            .header("Authorization", "Bearer " + refreshToken)
            .post("/auth/logout")
            .then()
            .statusCode(401);
        given().when()
            .header("Authorization", "Bearer " + refreshToken)
            .post("/auth/login/token")
            .then()
            .statusCode(401);
    }
}
