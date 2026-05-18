package com.kbtu.oop.project.repository;

import com.kbtu.oop.project.model.user.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User> {

    Optional<User> findByEmail(String email);
}