package com.example.pdfgen.model;

public class Experience {
    private String title;
    private String company;
    private String duration;
    private String description;

    // Constructors
    public Experience() {
    }

    public Experience(String title, String company, String duration, String description) {
        this.title = title;
        this.company = company;
        this.duration = duration;
        this.description = description;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Experience{" +
                "title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", duration='" + duration + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}