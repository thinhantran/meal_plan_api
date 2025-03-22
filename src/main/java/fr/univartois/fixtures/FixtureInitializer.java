package fr.univartois.fixtures;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class FixtureInitializer {

    @ConfigProperty(name = "quarkus.fixtures.real.enabled", defaultValue = "false")
    boolean realFixturesEnabled;

    @ConfigProperty(name = "quarkus.fixtures.fake.enabled", defaultValue = "false")
    boolean fakeFixturesEnabled;

    UserFixture userFixture;

    PasswordAuthFixture passwordAuthFixture;

    IngredientFixture ingredientFixture;

    RecipeFixture recipeFixture;

    MealFixture mealFixture;

    public FixtureInitializer(UserFixture userFixture, PasswordAuthFixture passwordAuthFixture,
        IngredientFixture ingredientFixture, RecipeFixture recipeFixture, MealFixture mealFixture) {
        this.userFixture = userFixture;
        this.passwordAuthFixture = passwordAuthFixture;
        this.ingredientFixture = ingredientFixture;
        this.recipeFixture = recipeFixture;
        this.mealFixture = mealFixture;
    }

    public void generateFixtures(@Observes StartupEvent ev) {
        if (realFixturesEnabled) {
            userFixture.generateRealData();
            passwordAuthFixture.generateRealData();
            ingredientFixture.generateRealData();
            recipeFixture.generateRealData();
            mealFixture.generateRealData();
        } else if (fakeFixturesEnabled) {
            userFixture.generateDataFromOutsideSource();
            passwordAuthFixture.generateDataFromOutsideSource();
            ingredientFixture.generateDataFromOutsideSource();
            recipeFixture.generateDataFromOutsideSource();
            mealFixture.generateDataFromOutsideSource();
        }
    }
}
