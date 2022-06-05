package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private User user;

    @BeforeEach
    public void createUserForTests() {
        user = new User("salvador@mail.ru", "chico", "Poco",
                LocalDate.of(1990, 10, 6));
    }

    @Test
    void shouldReturnUserWhenDbHasUser() {
        Map<Integer, User> users = userDbStorage.allUsers();
        assertThat(users).hasSize(1);
        User testUser = users.get(1);
        assertThat(testUser.getEmail()).isEqualTo("mail");
        assertThat(testUser.getLogin()).isEqualTo("login");
        assertThat(testUser.getName()).isEqualTo("name");
        assertThat(testUser.getBirthday()).isEqualTo("1990-03-08");
    }

    @Test
    void shouldAddUser() {
        userDbStorage.add(user);
        Map<Integer, User> users = userDbStorage.allUsers();
        User testUser = users.get(2);
        assertThat(testUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(testUser.getLogin()).isEqualTo(user.getLogin());
        assertThat(testUser.getName()).isEqualTo(user.getName());
        assertThat(testUser.getBirthday()).isEqualTo(user.getBirthday());
    }

    @Test
    void shouldUpdateUser() {
        User updateUser = new User(1, "updateEmail", "updateLogin", "updateName",
                LocalDate.of(2000, 01, 01));
        userDbStorage.update(updateUser);
        User returnedUser = userDbStorage.allUsers().get(1);
        assertThat(returnedUser).isEqualTo(updateUser);
    }

    @Test
    void shouldRemoveUser() {
        assertThat(userDbStorage.allUsers().get(1).getId()).isEqualTo(1);
        assertThat(userDbStorage.allUsers()).hasSize(1);
        userDbStorage.remove(1);
        assertThat(userDbStorage.allUsers()).isEmpty();
    }
}