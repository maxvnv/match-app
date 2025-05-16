package com.tennismatch.matchapp.model;

public enum Sex {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other"),
    PREFER_NOT_TO_SAY("Prefer not to say");

    private final String displayName;

    Sex(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 