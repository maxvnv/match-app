package com.tennismatch.matchapp.repository;

import com.tennismatch.matchapp.model.NtrpLevel;
import com.tennismatch.matchapp.model.Role;
import com.tennismatch.matchapp.model.Sex;
import com.tennismatch.matchapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByEmail_thenReturnUser() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .ntrpLevel(NtrpLevel.INTERMEDIATE_3_0)
                .homeTown("Testville")
                .age(25)
                .sex(Sex.MALE)
                .roles(Set.of(Role.ROLE_USER))
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByEmail(user.getEmail());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void whenFindByEmail_withNonExistingEmail_thenReturnEmpty() {
        // when
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isNotPresent();
    }

    @Test
    public void whenExistsByEmail_withExistingEmail_thenReturnTrue() {
        // given
        User user = User.builder()
                .email("exists@example.com")
                .password("password123")
                .firstName("Exists")
                .lastName("User")
                .ntrpLevel(NtrpLevel.ADVANCED_4_0)
                .homeTown("Existown")
                .age(30)
                .sex(Sex.MALE)
                .roles(Set.of(Role.ROLE_USER))
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // when
        Boolean exists = userRepository.existsByEmail(user.getEmail());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    public void whenExistsByEmail_withNonExistingEmail_thenReturnFalse() {
        // when
        Boolean exists = userRepository.existsByEmail("nonexistent.exists@example.com");

        // then
        assertThat(exists).isFalse();
    }
} 