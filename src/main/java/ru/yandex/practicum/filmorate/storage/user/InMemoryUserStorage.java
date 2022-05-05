package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    private boolean checkValidData(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Введен пустой email или отсутствует символ @.", UserController.class);
            throw new ValidationException("Email не может быть пустым и должен содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Введен пустой логин или логин содержит пробелы.", UserController.class);
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Введена дата рождения из будущего.", UserController.class);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return true;
    }

    private boolean checkUpdateValidData(User user) {
        if (checkValidData(user)) {
            if (!users.containsKey(user.getId())) {
                log.error("Введен неверный id.", UserController.class);
                throw new ValidationException("Пользователя с id" + user.getId() + " нет.");
            }
        }
        return true;
    }

    public Collection<User> allUsers() {
        return users.values();
    }

    public void add(User user) {
        if (checkValidData(user)) {
            user.setId(id);
            users.put(id, user);
            id++;
            log.debug("Добавлен пользователь " + user);
        }
    }

    public void update(User user) {
        if (checkUpdateValidData(user)) {
            User updateUser = users.get(user.getId());
            updateUser.setEmail(user.getEmail());
            updateUser.setLogin(user.getLogin());
            updateUser.setName(user.getName());
            updateUser.setBirthday(user.getBirthday());
            users.put(user.getId(), updateUser);
            log.debug("Данные пользователя " + updateUser + " обновлены.");
        }
    }

    public void remove(Integer id) {
        users.remove(id);
    }
}
