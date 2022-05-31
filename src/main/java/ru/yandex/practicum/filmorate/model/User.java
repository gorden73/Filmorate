package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    private Integer id;
    @NonNull
    @Email
    private String email;
    @NonNull
    private String login;
    private String name;
    @NonNull
    private LocalDate birthday;
    private Set<Integer> friends;
    private Map<Integer, Boolean> friendStatus;
    private Set<Integer> likedFilms;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
        this.friendStatus = new HashMap<>();
        this.likedFilms = new HashSet<>();
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
        this.friendStatus = new HashMap<>();
        this.likedFilms = new HashSet<>();
    }

    public User(Integer id, String email, String login, String name, LocalDate birthday, HashSet<Integer> friends,
                HashMap<Integer, Boolean> friendStatus, HashSet<Integer> likedFilms) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
        this.friendStatus = friendStatus;
        this.likedFilms = likedFilms;
    }
}
