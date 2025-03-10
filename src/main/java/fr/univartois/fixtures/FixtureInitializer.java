package fr.univartois.fixtures;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FixtureInitializer {

    @ConfigProperty(name = "quarkus.fixtures.real.enabled", defaultValue = "false")
    boolean realFixturesEnabled;

    @ConfigProperty(name = "quarkus.fixtures.fake.enabled", defaultValue = "false")
    boolean fakeFixturesEnabled;

    @Inject
    UserFixture userFixture;

    @Inject
    PasswordAuthFixture passwordAuthFixture;

    public void generateFixtures(@Observes StartupEvent ev) {
        if (realFixturesEnabled) {
            userFixture.generateRealData();
            passwordAuthFixture.generateRealData();
        } else if (fakeFixturesEnabled) {
            userFixture.generateFakeData();
            passwordAuthFixture.generateFakeData();
        }
    }
}
