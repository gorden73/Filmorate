package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.FeedDbStorage;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FeedService {
    private final FeedDbStorage feedDbStorage;
    private final UserStorage userDbStorage;

    @Autowired
    public FeedService(FeedDbStorage feedDbStorage, UserStorage userDbStorage) {
        this.feedDbStorage = feedDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public List<Feed> getFeedByUserId(Integer id) {
        feedDbStorage.checkUser(id);
        return feedDbStorage.getFeedByUserId(id);
    }
}
