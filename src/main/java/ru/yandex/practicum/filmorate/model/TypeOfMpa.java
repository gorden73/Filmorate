package ru.yandex.practicum.filmorate.model;

public enum TypeOfMpa {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String title;

    TypeOfMpa(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
