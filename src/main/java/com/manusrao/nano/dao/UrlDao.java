package com.manusrao.nano.dao;

import com.manusrao.nano.domains.Url;
import com.manusrao.nano.repository.UrlRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UrlDao {

    private final UrlRepository repository;

    public UrlDao(UrlRepository repository) {
        this.repository = repository;
    }

    public Optional<Url> findById(Long id) {
        return repository.findById(id);
    }

    public Url save(Url url) {
        return repository.save(url);
    }
}
