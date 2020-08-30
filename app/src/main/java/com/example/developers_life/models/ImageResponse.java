package com.example.developers_life.models;

import java.util.Objects;

public class ImageResponse {

    private int id;
    private String description;
    private String gifURL;
    private String type; // "coub", "gif"

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGifURL() {
        return gifURL;
    }

    public void setGifURL(String gifURL) {
        this.gifURL = gifURL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (this == anotherObject) return true;
        if (anotherObject == null || getClass() != anotherObject.getClass()) return false;
        ImageResponse anotherImageResponse = (ImageResponse) anotherObject;
        return id == anotherImageResponse.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
