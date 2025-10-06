package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testFindUserById() {
        // Given - создаем пользователя
        User newUser = User.builder()
                .email("test@mail.ru")
                .login("testLogin")
                .name("Test Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userStorage.create(newUser);

        // When - ищем пользователя по ID
        Optional<User> userOptional = userStorage.findById(createdUser.getId());

        // Then - проверяем, что пользователь найден и данные корректны
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                                .hasFieldOrPropertyWithValue("email", "test@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "testLogin")
                                .hasFieldOrPropertyWithValue("name", "Test Name")
                );
    }

    @Test
    void testCreateUser() {
        // Given
        User newUser = User.builder()
                .email("create@mail.ru")
                .login("createLogin")
                .name("Create Name")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        // When
        User createdUser = userStorage.create(newUser);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("create@mail.ru");
        assertThat(createdUser.getLogin()).isEqualTo("createLogin");
    }

    @Test
    void testUpdateUser() {
        // Given - создаем пользователя
        User user = User.builder()
                .email("update@mail.ru")
                .login("updateLogin")
                .name("Update Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userStorage.create(user);

        // When - обновляем пользователя
        User updatedUser = User.builder()
                .id(createdUser.getId())
                .email("updated@mail.ru")
                .login("updatedLogin")
                .name("Updated Name")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        userStorage.update(updatedUser);

        // Then - проверяем обновление
        Optional<User> foundUser = userStorage.findById(createdUser.getId());
        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("email", "updated@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "updatedLogin")
                                .hasFieldOrPropertyWithValue("name", "Updated Name")
                );
    }

    @Test
    void testFindAllUsers() {
        // Given - создаем несколько пользователей
        User user1 = User.builder()
                .email("user1@mail.ru")
                .login("user1Login")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@mail.ru")
                .login("user2Login")
                .name("User Two")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        userStorage.create(user1);
        userStorage.create(user2);

        // When - получаем всех пользователей
        List<User> users = userStorage.findAll();

        // Then - проверяем, что оба пользователя в списке
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getLogin)
                .contains("user1Login", "user2Login");
    }

    @Test
    void testAddAndGetFriends() {
        // Given - создаем двух пользователей
        User user1 = User.builder()
                .email("user1@mail.ru")
                .login("user1Login")
                .name("User One")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@mail.ru")
                .login("user2Login")
                .name("User Two")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);

        // When - добавляем в друзья и получаем список друзей
        userStorage.addFriend(createdUser1.getId(), createdUser2.getId());
        List<User> friends = userStorage.getFriends(createdUser1.getId());

        // Then - проверяем, что друг добавлен
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getLogin()).isEqualTo("user2Login");
    }
}