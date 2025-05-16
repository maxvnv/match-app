package com.tennismatch.matchapp.model;

public enum NtrpLevel {
    BEGINNER_2_0("2.0 - Beginner"),
    BEGINNER_2_5("2.5 - Beginner"),
    INTERMEDIATE_3_0("3.0 - Intermediate"),
    INTERMEDIATE_3_5("3.5 - Intermediate"),
    ADVANCED_4_0("4.0 - Advanced"),
    ADVANCED_4_5("4.5 - Advanced"),
    ADVANCED_5_0_PLUS("5.0+ - Highly Advanced");

    private final String displayName;

    NtrpLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 