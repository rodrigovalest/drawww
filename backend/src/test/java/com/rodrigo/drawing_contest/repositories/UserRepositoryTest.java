package com.rodrigo.drawing_contest.repositories;

import com.rodrigo.drawing_contest.models.entities.User;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

@RequiredArgsConstructor
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsername_WithValidUsername_ReturnsOptionalOfUser() {
        // Arrange
        String username = "cleiton";
        User user = new User(null, username, "encryptedpassword", null, null);
        this.testEntityManager.persist(user);

        // Act
        Optional<User> optionalSut = this.userRepository.findByUsername(username);

        // Assert
        Assertions.assertThat(optionalSut).isPresent();
        User sut = optionalSut.get();
        Assertions.assertThat(sut).isNotNull();
        Assertions.assertThat(sut.getUsername()).isEqualTo(username);
    }

    @Test
    public void findByUsername_WithInexistentUsername_ReturnsOptionOfUserWithoutAnyValue() {
        // Arrange
        String username = "cleiton";

        // Act
        Optional<User> sut = this.userRepository.findByUsername(username);

        // Assert
        Assertions.assertThat(sut).isNotPresent();
    }

    @Test
    public void existsByUsername_WithValidUsername_ReturnsTrue() {
        // Arrange
        String username = "cleiton";
        User user = new User(null, username, "encryptedpassword", null, null);
        this.testEntityManager.persist(user);

        // Act
        boolean sut = this.userRepository.existsByUsername(username);

        // Assert
        Assertions.assertThat(sut).isTrue();
    }

    @Test
    public void existsByUsername_WithValidUsername_ReturnsFalse() {
        // Arrange
        String username = "cleiton";

        // Act
        boolean sut = this.userRepository.existsByUsername(username);

        // Assert
        Assertions.assertThat(sut).isFalse();
    }
}
