package com.kbtu.oop.project.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrudRepository<T> {

    List<T> findAll();

    Optional<T> findById(UUID id);

    T save(T entity);

    void deleteById(UUID id);
}