package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

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
    public Director getDirector(@PathVariable Integer id) {
        return service.findDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return service.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public Integer removeDirector(@PathVariable Integer id) {
        return service.removeDirector(id);
    }
}
