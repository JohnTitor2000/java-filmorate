package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable int id) {
        return directorService.getDirector(id);
    }

    @DeleteMapping
    public void deleteAllDirectors() {
        directorService.deleteAllDirectors();
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorService.deleteDirector(id);
    }

    @PutMapping()
    public Director updateDirector(@RequestBody @Valid Director director) {
        return directorService.updateDirector(director);
    }

    @PostMapping()
    public Director addDirector(@RequestBody @Valid Director director) {
        return directorService.addDirector(director);
    }
}