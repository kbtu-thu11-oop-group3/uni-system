package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.request.SupportRequest;
import com.kbtu.oop.project.repository.SupportRequestRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonSupportRequestRepository extends AbstractJsonRepository<SupportRequest>
        implements SupportRequestRepository {

    public JsonSupportRequestRepository() {
        super(DataPaths.supportRequestsPath(), SupportRequest[].class);
    }
}