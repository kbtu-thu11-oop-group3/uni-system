package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.common.IdEntity;
import com.kbtu.oop.project.repository.CrudRepository;
import com.kbtu.oop.project.util.JsonUtil;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractJsonRepository<T extends IdEntity> implements CrudRepository<T> {

    private final Path filePath;
    private final Class<T[]> arrayType;

    protected AbstractJsonRepository(Path filePath, Class<T[]> arrayType) {
        this.filePath = filePath;
        this.arrayType = arrayType;
    }

    @Override
    public synchronized List<T> findAll() {
        return JsonUtil.readList(filePath, arrayType);
    }

    @Override
    public synchronized Optional<T> findById(UUID id) {
        return findAll().stream()
                .filter(entity -> id.equals(entity.getId()))
                .findFirst();
    }

    @Override
    public synchronized T save(T entity) {
        List<T> entities = findAll();
        int index = findIndex(entities, entity.getId());
        if (index >= 0) {
            entities.set(index, entity);
        } else {
            entities.add(entity);
        }
        JsonUtil.writeCollection(filePath, entities);
        return entity;
    }

    @Override
    public synchronized void deleteById(UUID id) {
        List<T> entities = findAll();
        entities.removeIf(entity -> id.equals(entity.getId()));
        JsonUtil.writeCollection(filePath, entities);
    }

    private int findIndex(List<T> entities, UUID id) {
        for (int i = 0; i < entities.size(); i++) {
            if (id.equals(entities.get(i).getId())) {
                return i;
            }
        }
        return -1;
    }
}