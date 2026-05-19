package com.kbtu.oop.project.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kbtu.oop.project.model.common.BaseEntity;
import com.kbtu.oop.project.model.common.Language;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "userType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Student.class, name = "STUDENT"),
        @JsonSubTypes.Type(value = GraduateStudent.class, name = "GRADUATE_STUDENT"),
        @JsonSubTypes.Type(value = Teacher.class, name = "TEACHER"),
        @JsonSubTypes.Type(value = Manager.class, name = "MANAGER"),
        @JsonSubTypes.Type(value = Admin.class, name = "ADMIN"),
        @JsonSubTypes.Type(value = TechSupportSpecialist.class, name = "TECH_SUPPORT")
})
public abstract class User extends BaseEntity {

    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String passwordHash;
    private Language language = Language.EN;
    private boolean active = true;

    @JsonIgnore
    public String getFullName() {
        return Arrays.asList(firstName, middleName, lastName)
                .stream()
                .filter(s -> s != null && !s.isBlank())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}