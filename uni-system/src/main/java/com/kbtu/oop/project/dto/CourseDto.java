package com.kbtu.oop.project.dto;

import com.kbtu.oop.project.model.common.CourseType;

import java.util.UUID;

public record CourseDto(UUID id, String code, String title, CourseType courseType, int credits) {
}