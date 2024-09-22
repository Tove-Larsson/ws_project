package com.tove.ws_project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
public class Game {

    @Id
    private Long id;
    private String name;
    @Column(columnDefinition="TEXT")
    private String storyline;
    private double rating;
    @Column(name = "first_release_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime firstReleaseDate;

    public Game() {
    }

    public Game(Long id, String name, String storyline, double rating, LocalDateTime firstReleaseDate) {
        this.id = id;
        this.name = name;
        this.storyline = storyline;
        this.rating = rating;
        this.firstReleaseDate = firstReleaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setFirstReleaseDateFromUnix(long unixTimestamp) {
        System.out.println(unixTimestamp);
        this.firstReleaseDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp), ZoneOffset.UTC);
    }

    public LocalDateTime getFirstReleaseDate() {
        return firstReleaseDate;
    }
}
