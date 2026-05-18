package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.request.Complaint;
import com.kbtu.oop.project.repository.ComplaintRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonComplaintRepository extends AbstractJsonRepository<Complaint> implements ComplaintRepository {

    public JsonComplaintRepository() {
        super(DataPaths.complaintsPath(), Complaint[].class);
    }
}