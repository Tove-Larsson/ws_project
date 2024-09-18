package com.tove.ws_project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Game {

    @Id
    private Long id;
    private String name;
    private String storyline;
    private double rating;

    public Game() {
    }

    public Game(Long id, String name, String storyline, double rating) {
        this.id = id;
        this.name = name;
        this.storyline = storyline;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
