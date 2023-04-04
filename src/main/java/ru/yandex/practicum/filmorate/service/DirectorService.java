package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    private static int currentId = 1;

    public Director addDirector(@Valid Director director) {
        log.info("Adding new director");
        director.setId(currentId);
        ++currentId;
        return directorDbStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Updating director with id {}", director.getId());
        return directorDbStorage.updateDirector(director);
    }

    public Director getDirector(int id) {
        log.info("Getting director with id: {}", id);
        return directorDbStorage.getDirector(id);
    }

    public List<Director> getAllDirectors() {
        log.info("Getting list of all directors");
        return directorDbStorage.getAllDirectors();
    }

    public void deleteDirector(int id) {
        directorDbStorage.deleteDirector(id);
        log.info("Director with id: {} successfully deleted", id);
    }

    public void deleteAllDirectors() {
        directorDbStorage.deleteAllDirectors();
        log.info("All directors are deleted");
    }
}