package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FriendDaoTest {
    private final FriendDao friendDao;
    private final UserDbStorage userDbStorage;
    private final UserService userService;
    private User user;

    @BeforeEach
    public void createUserForTests() {
        user = new User(2, "salvador@mail.ru", "chico", "Poco",
                LocalDate.of(1990, 10, 6), new HashSet<>(), new HashMap<>(), new HashSet<>());
    }

    @Test
    void shouldAddToFriends() {
        userDbStorage.add(user);
        friendDao.addToFriends(1, 2);
        assertThat(userService.getUserFriends(1)).contains(user);
    }

    @Test
    void shouldRemoveFromFriends() {
        userDbStorage.add(user);
        friendDao.addToFriends(1, 2);
        assertThat(userService.getUserFriends(1)).contains(user);
        friendDao.removeFromFriends(1, 2);
        assertThat(userService.getUserFriends(1)).isEmpty();
    }
}