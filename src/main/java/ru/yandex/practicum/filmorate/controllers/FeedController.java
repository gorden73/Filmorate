package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.Collection;
import java.util.List;

@RequestMapping("/users")
@RestController
public class FeedController {
    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/{id}/feed")
    public Collection<Feed> getFeedsByUserId(@PathVariable Integer id) {
        return feedService.getFeedByUserId(id);
    }
}
