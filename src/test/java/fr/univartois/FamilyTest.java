package fr.univartois;

import fr.univartois.model.Family;
import fr.univartois.model.User;
import fr.univartois.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class FamilyTest {

    @Inject
    UserRepository userRepository;

    static Family family;

    @Test
    @Order(1)
    void testCreateFamily() {
        User user = userRepository.findAll().firstResult();

        family = given().when()
                .contentType("application/json")
                .body(user)
                .post("/families")
                .then()
                .statusCode(201)
                .extract().as(Family.class);

        Assertions.assertNotNull(family);
    }

    @Test
    @Order(2)
    void testGetUserFamily() {
        List<User> members = given().when()
                .get("/families/{identity}/members", family.getId())
                .then()
                .statusCode(200)
                .extract().body().as(List.class);

        Assertions.assertEquals(1, members.size());
    }

}
