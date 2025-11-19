package com.rainydaysengine.rainydays.infra.postgres;

import com.rainydaysengine.rainydays.infra.postgres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

//    @Test
//    public void UserRepository_Save_ReturnSavedUser() {
//        String randomUuid = UUID.randomUUID().toString();
//
//        // Arrange
//        UsersEntity user = UsersEntity.builder()
//                .iamId(randomUuid)
//                .emailAddress("test@test.com")
//                .username("test-user")
//                .firstName("Test")
//                .middleName("Middle")
//                .lastName("LastName")
//                .build();
//
//        // Act
//        UsersEntity savedUsersEntity = userRepository.save(user);
//
//        // Assert
//        Assertions.assertThat(savedUsersEntity).isNotNull();
//        Assertions.assertThat(savedUsersEntity.getId());
//    }

//    @Test
//    public void PokemonRepository_GetAll_ReturnMoreThanOneUser() {
//        // Arrange
//        UsersEntity user = UsersEntity.builder()
//                .iamId(UUID.randomUUID().toString())
//                .emailAddress("test@test.com")
//                .username("test-user")
//                .firstName("Test")
//                .middleName("Middle")
//                .lastName("LastName")
//                .build();
//
//        UsersEntity user2 = UsersEntity.builder()
//                .iamId(UUID.randomUUID().toString())
//                .emailAddress("yen@yen.com")
//                .username("yen-cath")
//                .firstName("nrtn")
//                .middleName("cath")
//                .lastName("Roxas")
//                .build();
//        // Act
//        userRepository.save(user);
//        userRepository.save(user2);
//
//        List<UsersEntity> usersEntityList = userRepository.findAll();
//
//        // Assert
//        Assertions.assertThat(usersEntityList).isNotNull();
//        Assertions.assertThat(usersEntityList.size()).isEqualTo(2);
//    }
//
//    @Test
//    public void PokemonRepository_FindById_ReturnsUser() {
//        // Arrange
//        UsersEntity user = UsersEntity.builder()
//                .iamId(UUID.randomUUID().toString())
//                .emailAddress("test@test.com")
//                .username("test-user")
//                .firstName("Test")
//                .middleName("Middle")
//                .lastName("LastName")
//                .build();
//        // Act
//        userRepository.save(user);
//
//        UsersEntity usersEntityList = userRepository.findById(user.getId()).get();
//
//        // Assert
//        Assertions.assertThat(usersEntityList).isNotNull();
//    }
//
//    @Test
//    public void UserRepository_FindByEmailAddress_ReturnsUserNotNull() {
//        // Arrange
//        UsersEntity user = UsersEntity.builder()
//                .iamId(UUID.randomUUID().toString())
//                .emailAddress("test@test.com")
//                .username("test-user")
//                .firstName("Test")
//                .middleName("Middle")
//                .lastName("LastName")
//                .build();
//
//        // Act
//        userRepository.save(user);
//        Optional<String> usersList = userRepository.findByEmailAddress("test@test.com");
//
//        // Assert
//        Assertions.assertThat(usersList).isNotNull();
//    }
//
//    @Test
//    public void UserRepository_UpdateUser_ReturnsUserNotNull() {
//        UsersEntity user = UsersEntity.builder()
//                .iamId(UUID.randomUUID().toString())
//                .emailAddress("test@test.com")
//                .username("test-user")
//                .firstName("Test")
//                .middleName("Middle")
//                .lastName("LastName")
//                .build();
//
//        userRepository.save(user);
//        UsersEntity usersEntitySave = userRepository.findById(user.getId()).get();
//        usersEntitySave.setLastName("Roxas");
//        usersEntitySave.setFirstName("Catherine");
//
//        UsersEntity updatedUser = userRepository.save(usersEntitySave);
//
//        Assertions.assertThat(updatedUser.getFirstName()).isNotNull();
//        Assertions.assertThat(updatedUser.getLastName()).isNotNull();
//    }
//
//    @Test
//    public void PokemonRepository_DeleteById_ReturnsUserIsEmpty() {
//        // Arrange
//        UsersEntity user = UsersEntity.builder()
//                .iamId(UUID.randomUUID().toString())
//                .emailAddress("test@test.com")
//                .username("test-user")
//                .firstName("Test")
//                .middleName("Middle")
//                .lastName("LastName")
//                .build();
//        // Act
//        userRepository.save(user);
//
//        userRepository.deleteById(user.getId());
//        Optional<UsersEntity> userReturn = userRepository.findById(user.getId());
//
//        // Assert
//        Assertions.assertThat(userReturn).isEmpty();
//    }
}
