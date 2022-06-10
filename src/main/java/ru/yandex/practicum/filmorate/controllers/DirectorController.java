package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RequestMapping("/directors")
@RestController
@Slf4j
public class DirectorController {
    private final DirectorService service;

    @Autowired
    public DirectorController(DirectorService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Director> getAllDirectors() {
        return service.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Optional<Director> getDirector(@PathVariable Integer id) {
        return service.findDirectorById(id);
    }

    @PostMapping
    public Optional<Director> createDirector(@Valid @RequestBody Director director) {
        return service.createDirector(director);
    }

    @PutMapping
    public Optional<Director> updateDirector(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Integer id) {
        service.removeDirector(id);
    }
}
