package com.kbtu.oop.project.repository.impl;

import com.kbtu.oop.project.model.organization.StudentOrganization;
import com.kbtu.oop.project.repository.StudentOrganizationRepository;
import com.kbtu.oop.project.util.DataPaths;

public class JsonStudentOrganizationRepository extends AbstractJsonRepository<StudentOrganization>
        implements StudentOrganizationRepository {

    public JsonStudentOrganizationRepository() {
        super(DataPaths.studentOrganizationsPath(), StudentOrganization[].class);
    }
}
