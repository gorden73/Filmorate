package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController controller;
    private User user;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;
    private User user7;
    private User user8;

    @BeforeEach
    public void start() {
        UserStorage userStorage = new InMemoryUserStorage();
        FriendDao friendDao = new FriendDao(new JdbcTemplate());
        controller = new UserController(new UserService(userStorage, friendDao));
        createUsersForTests();
    }

    private void createUsersForTests() {
        user = new User(1, "salvador@mail.ru", "chico", "Poco",
                LocalDate.of(1990, 10, 6));
        user1 = new User(1, "", "chico1", "Poco1",
                LocalDate.of(1991, 10, 6));
        user2 = new User(1, "salvador.ru", "chico2", "Poco2",
                LocalDate.of(1992, 10, 6));
        user3 = new User(1, "salvador@t.ru", "", "Poco3",
                LocalDate.of(1993, 10, 6));
        user4 = new User(1, "salvador@t.ru", "chico and son", "Poco4",
                LocalDate.of(1994, 10, 6));
        user5 = new User(1, "salvador@t.ru5", "chico5", "",
                LocalDate.of(1995, 10, 6));
        user6 = new User(1, "salvador@t.ru6", "chico6", "Poco6",
                LocalDate.now().minusDays(1));
        user7 = new User(1, "salvador@t.ru7", "chico7", "Poco7",
                LocalDate.now());
        user8 = new User(1, "salvador@t.ru8", "chico8", "Poco8",
                LocalDate.now().plusDays(1));
    }

    @Test
    void shouldReturnAllUsers() {
        assertEquals(0, controller.allUsers().size(), "Хранилище должно быть пустым.");
        controller.add(user);
        Collection<User> users = controller.allUsers();
        assertEquals(1, users.size(), "Хранилище не должно быть пустым.");
        assertTrue(users.contains(user), "Пользователь не добавлен.");
    }

    @Test
    void shouldAddUserWhenDataIsValid() {
        controller.add(user);
        assertTrue(controller.allUsers().contains(user), "Пользователь не добавлен в хранилище.");
    }

    @Test
    void shouldNotAddUserWhenEmailIsEmpty() {
        assertThrows(ValidationException.class, () -> controller.add(user1), "Email не пустой.");
        assertFalse(controller.allUsers().contains(user1), "Фильм добавлен в хранилище.");
    }

    @Test
    void shouldNotAddUserWhenEmailIsNotContainsSymbol() {
        assertThrows(ValidationException.class, () -> controller.add(user2), "Email содержит символ @.");
        assertFalse(controller.allUsers().contains(user2), "Пользователь добавлен в хранилище.");
    }

    @Test
    void shouldNotAddUserWhenLoginIsEmpty() {
        assertThrows(ValidationException.class, () -> controller.add(user3), "Логин не пустой.");
        assertFalse(controller.allUsers().contains(user3), "Пользователь добавлен в хранилище.");
    }

    @Test
    void shouldNotAddUserWhenLoginContainsSpaces() {
        assertThrows(ValidationException.class, () -> controller.add(user4), "Логин не содержит пробелы.");
        assertFalse(controller.allUsers().contains(user4), "Пользователь добавлен в хранилище.");
    }

    @Test
    void shouldAddUserWhenNameIsEmpty() {
        controller.add(user5);
        assertTrue(controller.allUsers().contains(user5), "Пользователь не добавлен в хранилище.");
        assertEquals(user5.getLogin(), user5.getName(), "Логин и имя различны.");
    }

    @Test
    void shouldAddUserWhenBirthdayIsBeforeNow() {
        controller.add(user6);
        assertTrue(controller.allUsers().contains(user6), "Пользователь не добавлен в хранилище.");
    }

    @Test
    void shouldAddUserWhenBirthdayIsEqualsNow() {
        controller.add(user7);
        assertTrue(controller.allUsers().contains(user7), "Пользователь не добавлен в хранилище.");
    }

    @Test
    void shouldNotAddUserWhenBirthdayIsAfterNow() {
        assertThrows(ValidationException.class, () -> controller.add(user8), "Дата рождения не в будущем.");
        assertFalse(controller.allUsers().contains(user8), "Пользователь добавлен в хранилище.");
    }

    @Test
    void shouldUpdateUserWhenDataIsValid() {
        controller.add(user);
        assertEquals(1, controller.allUsers().size(), "Хранилище не должно быть пустым.");
        assertTrue(controller.allUsers().contains(user), "Фильм не добавлен в хранилище.");
        controller.update(user6);
        assertEquals(1, controller.allUsers().size(), "Хранилище не должно быть пустым.");
        assertEquals(user.getEmail(), user6.getEmail(), "Адреса email не совпадают.");
        assertEquals(user.getLogin(), user6.getLogin(), "Логины не совпадают.");
        assertEquals(user.getName(), user6.getName(), "Имена не совпадают.");
        assertEquals(user.getBirthday(), user6.getBirthday(), "Даты рождения не совпадают.");
    }

    @Test
    void shouldNotUpdateUserWhenEmailIsEmpty() {
        controller.add(user);
        assertThrows(ValidationException.class, () -> controller.update(user1), "Email не пустой.");
        assertNotEquals(user.getEmail(), user1.getEmail(), "Адреса email совпадают.");
        assertNotEquals(user.getLogin(), user1.getLogin(), "Логины совпадают.");
        assertNotEquals(user.getName(), user1.getName(), "Имена совпадают.");
        assertNotEquals(user.getBirthday(), user1.getBirthday(), "Даты рождения совпадают.");
    }

    @Test
    void shouldNotUpdateUserWhenEmailIsNotContainsSymbol() {
        controller.add(user);
        assertThrows(ValidationException.class, () -> controller.update(user2), "Email содержит символ @.");
        assertNotEquals(user.getEmail(), user2.getEmail(), "Адреса email совпадают.");
        assertNotEquals(user.getLogin(), user2.getLogin(), "Логины совпадают.");
        assertNotEquals(user.getName(), user2.getName(), "Имена совпадают.");
        assertNotEquals(user.getBirthday(), user2.getBirthday(), "Даты рождения совпадают.");
    }

    @Test
    void shouldNotUpdateUserWhenLoginIsEmpty() {
        controller.add(user);
        assertThrows(ValidationException.class, () -> controller.update(user3), "Логин не пустой.");
        assertNotEquals(user.getEmail(), user3.getEmail(), "Адреса email совпадают.");
        assertNotEquals(user.getLogin(), user3.getLogin(), "Логины совпадают.");
        assertNotEquals(user.getName(), user3.getName(), "Имена совпадают.");
        assertNotEquals(user.getBirthday(), user3.getBirthday(), "Даты рождения совпадают.");
    }

    @Test
    void shouldNotUpdateUserWhenLoginContainsSpace() {
        controller.add(user);
        assertThrows(ValidationException.class, () -> controller.update(user4), "Логин не содержит пробелы.");
        assertNotEquals(user.getEmail(), user4.getEmail(), "Адреса email совпадают.");
        assertNotEquals(user.getLogin(), user4.getLogin(), "Логины совпадают.");
        assertNotEquals(user.getName(), user4.getName(), "Имена совпадают.");
        assertNotEquals(user.getBirthday(), user4.getBirthday(), "Даты рождения совпадают.");
    }

    @Test
    void shouldUpdateUserWhenNameIsEmpty() {
        controller.add(user);
        assertTrue(controller.allUsers().contains(user), "Пользователь не добавлен в хранилище.");
        controller.update(user5);
        assertEquals(user.getLogin(), user.getName(), "Логин и имя различны.");
        assertEquals(user.getEmail(), user5.getEmail(), "Адреса email не совпадают.");
        assertEquals(user.getLogin(), user5.getLogin(), "Логины не совпадают.");
        assertEquals(user.getName(), user5.getName(), "Имена не совпадают.");
        assertEquals(user.getBirthday(), user5.getBirthday(), "Даты рождения не совпадают.");
    }

    @Test
    void shouldUpdateUserWhenBirthdayIsBeforeNow() {
        controller.add(user);
        assertTrue(controller.allUsers().contains(user), "Пользователь не добавлен в хранилище.");
        controller.update(user6);
        assertEquals(user.getEmail(), user6.getEmail(), "Адреса email не совпадают.");
        assertEquals(user.getLogin(), user6.getLogin(), "Логины не совпадают.");
        assertEquals(user.getName(), user6.getName(), "Имена не совпадают.");
        assertEquals(user.getBirthday(), user6.getBirthday(), "Даты рождения не совпадают.");
    }

    @Test
    void shouldUpdateUserWhenBirthdayIsEqualsNow() {
        controller.add(user);
        assertTrue(controller.allUsers().contains(user), "Пользователь не добавлен в хранилище.");
        controller.update(user7);
        assertEquals(user.getEmail(), user7.getEmail(), "Адреса email не совпадают.");
        assertEquals(user.getLogin(), user7.getLogin(), "Логины не совпадают.");
        assertEquals(user.getName(), user7.getName(), "Имена не совпадают.");
        assertEquals(user.getBirthday(), user7.getBirthday(), "Даты рождения не совпадают.");
    }

    @Test
    void shouldNotUpdateUserWhenBirthdayIsAfterNow() {
        controller.add(user);
        assertThrows(ValidationException.class, () -> controller.update(user8), "Дата рождения не в будущем.");
        assertNotEquals(user.getEmail(), user8.getEmail(), "Адреса email совпадают.");
        assertNotEquals(user.getLogin(), user8.getLogin(), "Логины совпадают.");
        assertNotEquals(user.getName(), user8.getName(), "Имена совпадают.");
        assertNotEquals(user.getBirthday(), user8.getBirthday(), "Даты рождения совпадают.");
    }
}