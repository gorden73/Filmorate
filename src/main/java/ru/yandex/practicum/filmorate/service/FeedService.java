package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FeedService {
    private final FeedDao feedDbStorage;
    private final UserStorage userDbStorage;

    @Autowired
    public FeedService(FeedDao feedDbStorage, UserStorage userDbStorage) {
        this.feedDbStorage = feedDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public List<Feed> getFeedByUserId(Integer id) {
        userDbStorage.getUserById(id);
        return feedDbStorage.getFeedByUserId(id);
    }
}
