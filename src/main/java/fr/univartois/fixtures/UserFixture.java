package fr.univartois.fixtures;

import fr.univartois.model.User;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Arrays;

@ApplicationScoped
public class UserFixture extends Fixture {

    @Inject
    UserRepository userRepository;

    @Transactional
    @Override
    public void generateRealData() {
        String[] names = new String[]{"java", "python", "c++"};
        Arrays.stream(names).forEach(name -> {
            User user = new User();
            user.setUsername(name);
            userRepository.persist(user);
        });
    }

    @Transactional
    @Override
    public void generateSingleFakeData() {

    }
}
