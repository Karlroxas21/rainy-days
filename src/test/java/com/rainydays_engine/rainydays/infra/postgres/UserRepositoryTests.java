package com.rainydays_engine.rainydays.infra.postgres;

import com.rainydays_engine.rainydays.infra.postgres.entity.Users;
import com.rainydays_engine.rainydays.infra.postgres.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        String randomUuid = UUID.randomUUID().toString();

        // Arrange
        Users user = Users.builder()
                .iamId(randomUuid)
                .emailAddress("test@test.com")
                .username("test-user")
                .firstName("Test")
                .middleName("Middle")
                .lastName("LastName")
                .build();

        // Act
        Users savedUsers = userRepository.save(user);

        // Assert
        Assertions.assertThat(savedUsers).isNotNull();
        Assertions.assertThat(savedUsers.getId());
    }

    @Test
    public void PokemonRepository_GetAll_ReturnMoreThanOneUser(){
        // Arrange
        Users user = Users.builder()
                .iamId(UUID.randomUUID().toString())
                .emailAddress("test@test.com")
                .username("test-user")
                .firstName("Test")
                .middleName("Middle")
                .lastName("LastName")
                .build();

        Users user2 = Users.builder()
                .iamId( UUID.randomUUID().toString())
                .emailAddress("yen@yen.com")
                .username("yen-cath")
                .firstName("nrtn")
                .middleName("cath")
                .lastName("Roxas")
                .build();
        // Act
        userRepository.save(user);
        userRepository.save(user2);

        List<Users> usersList = userRepository.findAll();

        // Assert
        Assertions.assertThat(usersList).isNotNull();
        Assertions.assertThat(usersList.size()).isEqualTo(2);
    }

    @Test
    public void PokemonRepository_FindById_ReturnsUser(){
        // Arrange
        Users user = Users.builder()
                .iamId(UUID.randomUUID().toString())
                .emailAddress("test@test.com")
                .username("test-user")
                .firstName("Test")
                .middleName("Middle")
                .lastName("LastName")
                .build();
        // Act
        userRepository.save(user);

        Users usersList = userRepository.findById(user.getId()).get();

        // Assert
        Assertions.assertThat(usersList).isNotNull();
    }

    @Test
    public void UserRepository_FindByEmailAddress_ReturnsUserNotNull() {
        // Arrange
        Users user = Users.builder()
                .iamId(UUID.randomUUID().toString())
                .emailAddress("test@test.com")
                .username("test-user")
                .firstName("Test")
                .middleName("Middle")
                .lastName("LastName")
                .build();

        // Act
        userRepository.save(user);
        Optional<String> usersList = userRepository.findByEmailAddress("test@test.com");

        // Assert
        Assertions.assertThat(usersList).isNotNull();
    }

    @Test
    public void UserRepository_UpdateUser_ReturnsUserNotNull() {
        Users user = Users.builder()
                .iamId(UUID.randomUUID().toString())
                .emailAddress("test@test.com")
                .username("test-user")
                .firstName("Test")
                .middleName("Middle")
                .lastName("LastName")
                .build();

        userRepository.save(user);
        Users usersSave = userRepository.findById(user.getId()).get();
        usersSave.setLastName("Roxas");
        usersSave.setFirstName("Catherine");

        Users updatedUser = userRepository.save(usersSave);

        Assertions.assertThat(updatedUser.getFirstName()).isNotNull();
        Assertions.assertThat(updatedUser.getLastName()).isNotNull();
    }

    @Test
    public void PokemonRepository_DeleteById_ReturnsUserIsEmpty(){
        // Arrange
        Users user = Users.builder()
                .iamId(UUID.randomUUID().toString())
                .emailAddress("test@test.com")
                .username("test-user")
                .firstName("Test")
                .middleName("Middle")
                .lastName("LastName")
                .build();
        // Act
        userRepository.save(user);

        userRepository.deleteById(user.getId());
        Optional<Users> userReturn = userRepository.findById(user.getId());

        // Assert
        Assertions.assertThat(userReturn).isEmpty();
    }
}
