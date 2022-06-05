package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Film {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private Integer duration;
    private Set<Integer> likes;
    private Mpa mpa;
    private List<Integer> genres;

    public Film() {
        super();
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = new HashSet<>();
        this.genres = new ArrayList<>();
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = new HashSet<>();
        this.genres = new ArrayList<>();
    }

    public Film(String name, LocalDate releaseDate, String description, Integer duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = new HashSet<>();
        this.genres = new ArrayList<>();
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa,
                Set<Integer> likes, List<Integer> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = likes;
        this.genres = genres;
    }

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa,
                List<Integer> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = new HashSet<>();
        this.genres = genres;
    }
}
