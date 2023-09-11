package ru.mts.digital.mock.simplemockapp.enums;

public enum Extension {
    XML(".xml"),
    JSON(".json"),
    TXT(".txt");

    private final String extension;

    Extension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}