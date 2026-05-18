package com.kbtu.oop.project.model.user;

import com.kbtu.oop.project.model.common.ManagerType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Manager extends Employee {

    private ManagerType managerType = ManagerType.DEPARTMENT;
}