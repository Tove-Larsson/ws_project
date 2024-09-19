package com.tove.ws_project.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public Review() {
    }

    public Review(UUID id, String title, String content, Game game) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.game = game;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}