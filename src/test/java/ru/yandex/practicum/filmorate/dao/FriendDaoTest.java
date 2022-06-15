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
import java.util.List;

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
                LocalDate.of(1990, 10, 6), new HashSet<>(), new HashMap<>(),
                new HashSet<>());
    }

    @Test
    void shouldAddToFriends() {
        userDbStorage.addUser(user);
        friendDao.addToFriends(1, 2);
        assertThat(userService.getUserFriends(1)).contains(user);
        assertThat(userDbStorage.getAllUsers().get(1).getFriendStatus().get(2)).isEqualTo(false);
    }

    @Test
    void shouldAddToFriendsAcceptedFriendship() {
        userDbStorage.addUser(user);
        friendDao.addToFriends(1, 2);
        friendDao.addToFriends(2, 1);
        assertThat(userService.getUserFriends(1)).contains(userDbStorage.getAllUsers().get(2));
        assertThat(userService.getUserFriends(2)).contains(userDbStorage.getAllUsers().get(1));
        assertThat(userDbStorage.getAllUsers().get(1).getFriendStatus().get(2)).isEqualTo(true);
    }

    @Test
    void shouldRemoveFromFriends() {
        userDbStorage.addUser(user);
        friendDao.addToFriends(1, 2);
        assertThat(userService.getUserFriends(1)).contains(user);
        friendDao.removeFromFriends(1, 2);
        assertThat(userService.getUserFriends(1)).isEmpty();
    }

    @Test
    void shouldReturnUserFriends() {
        userDbStorage.addUser(user);
        friendDao.addToFriends(1, 2);
        assertThat(friendDao.getUserFriends(1)).isEqualTo(List.of(user));
    }

    @Test
    void shouldReturnMutualFriends() {
        User common = new User(3, "common@mail.ru", "common", "common",
                LocalDate.of(1980, 10, 6), new HashSet<>(), new HashMap<>(),
                new HashSet<>());
        userDbStorage.addUser(user);
        userDbStorage.addUser(common);
        friendDao.addToFriends(1, 3);
        friendDao.addToFriends(2, 3);
        assertThat(friendDao.getMutualFriends(1, 2)).isEqualTo(List.of(common));
    }
}