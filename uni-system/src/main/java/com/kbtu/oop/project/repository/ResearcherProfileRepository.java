package com.kbtu.oop.project.repository;

import com.kbtu.oop.project.model.research.ResearcherProfile;

import java.util.Optional;
import java.util.UUID;

public interface ResearcherProfileRepository extends CrudRepository<ResearcherProfile> {

    Optional<ResearcherProfile> findByUserId(UUID userId);
}
