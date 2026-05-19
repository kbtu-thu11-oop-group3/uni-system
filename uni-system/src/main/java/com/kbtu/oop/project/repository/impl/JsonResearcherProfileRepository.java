package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.research.ResearcherProfile;
import com.kbtu.oop.project.repository.ResearcherProfileRepository;
import com.kbtu.oop.project.util.DataPaths;

import java.util.Optional;
import java.util.UUID;

public class JsonResearcherProfileRepository extends AbstractJsonRepository<ResearcherProfile>
        implements ResearcherProfileRepository {

    public JsonResearcherProfileRepository() {
        super(DataPaths.researcherProfilesPath(), ResearcherProfile[].class);
    }

    @Override
    public Optional<ResearcherProfile> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(profile -> userId.equals(profile.getUserId()))
                .findFirst();
    }
}
