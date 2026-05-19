package com.kbtu.oop.project.model.common;

public enum School {
    SEPI("School of Engineering and Physical Sciences"),
    SG("School of Geosciences"),
    SITE("School of Information Technology and Engineering"),
    BS("Business School"),
    ISE("Institute of Systems Engineering"),
    KMA("Kazakh-Turkish School of Maritime"),
    SAM("School of Agriculture and Mineralogy"),
    SCE("School of Chemical Engineering"),
    SSS("School of Social Sciences"),
    SMSGT("School of Mathematics, Science, and General Technology");

    private final String displayName;

    School(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
