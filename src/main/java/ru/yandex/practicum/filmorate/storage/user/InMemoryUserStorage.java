package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    private boolean checkValidData(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Введен пустой email или отсутствует символ @.", InMemoryUserStorage.class);
            throw new ValidationException("Email не может быть пустым и должен содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Введен пустой логин или логин содержит пробелы.", InMemoryUserStorage.class);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Введена дата рождения из будущего.", InMemoryUserStorage.class);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

    private boolean checkUpdateValidData(User user) {
        if (checkValidData(user)) {
            if (!users.containsKey(user.getId())) {
                log.error("Введен неверный id.", InMemoryUserStorage.class);
                throw new ValidationException("Пользователя с id" + user.getId() + " нет.");
            }
        }
        return true;
    }

    public Map<Integer, User> allUsers() {
        return users;
    }

    public User add(User user) {
        if (checkValidData(user)) {
            user.setId(id);
            users.put(id, user);
            id++;
            log.debug("Добавлен пользователь {}.", user);
        }
        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ElementNotFoundException("пользователь " + user.getId());
        }
        User updateUser = users.get(user.getId());
        if (checkUpdateValidData(user)) {
            updateUser.setEmail(user.getEmail());
            updateUser.setLogin(user.getLogin());
            updateUser.setName(user.getName());
            updateUser.setBirthday(user.getBirthday());
            users.put(user.getId(), updateUser);
            log.debug("Обновлены данные пользователя {}.", updateUser.getId());
        }
        return updateUser;
    }

    public Integer remove(Integer id) {
        users.remove(id);
        log.debug("Удален пользователь {}", id);
        return id;
    }
}
