package com.tove.ws_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameApi {

    private int id;
    private String name;
    private String storyline;
    private double rating;
    @JsonProperty("first_release_date")
    private Long firstReleaseDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Long getFirstReleaseDate() {
        return firstReleaseDate;
    }

    public void setFirstReleaseDate(Long firstReleaseDate) {
        this.firstReleaseDate = firstReleaseDate;
    }

    public Game toGame() {
        Game game = new Game();
        game.setId((long) this.id);
        game.setName(this.name);
        game.setStoryline(this.storyline);
        game.setRating(this.rating);
        game.setFirstReleaseDateFromUnix(this.firstReleaseDate);
        return game;
    }
}
