package ru.yandex.practicum.filmorate.model;

public enum TypeOfGenre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String title;

    TypeOfGenre(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
