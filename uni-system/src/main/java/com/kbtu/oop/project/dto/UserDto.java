package com.kbtu.oop.project.dto;

import com.kbtu.oop.project.model.common.Language;

import java.util.UUID;

public record UserDto(UUID id, String fullName, String email, Language language) {
}