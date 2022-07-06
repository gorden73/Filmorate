package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {
    List<Feed> getFeedByUserId(Integer id);

    void addFeed(Feed feed);
}
