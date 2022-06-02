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


    public Map<Integer, User> allUsers() {
        return users;
    }

    public User add(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        log.debug("Добавлен пользователь {}.", user);
        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ElementNotFoundException("пользователь " + user.getId());
        }
        User updateUser = users.get(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        updateUser.setName(user.getName());
        updateUser.setBirthday(user.getBirthday());
        users.put(user.getId(), updateUser);
        log.debug("Обновлены данные пользователя {}.", updateUser.getId());
        return updateUser;
    }

    public Integer remove(Integer id) {
        users.remove(id);
        log.debug("Удален пользователь {}", id);
        return id;
    }
}
