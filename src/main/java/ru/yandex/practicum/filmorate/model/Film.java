package model;

import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    @NonNull
    private final int filmId;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Duration duration;
}
