package fr.univartois.fixtures;

import java.util.Arrays;

import fr.univartois.model.User;
import fr.univartois.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserFixture extends Fixture {

    UserRepository userRepository;

    public UserFixture(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    public void generateDataFromOutsideSource() {
        // UNUSED
    }
}
