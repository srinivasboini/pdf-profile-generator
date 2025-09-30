package com.example.pdfgen.model;

import java.util.List;

public class CandidateProfile {
    private String name;
    private String email;
    private String phone;
    private String location;
    private String summary;
    private List<String> skills;
    private List<Experience> experience;
    private List<Education> education;
    private List<String> certifications;

    // Constructors
    public CandidateProfile() {
    }

    public CandidateProfile(String name, String email, String phone, String location,
                           String summary, List<String> skills, List<Experience> experience,
                           List<Education> education, List<String> certifications) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.summary = summary;
        this.skills = skills;
        this.experience = experience;
        this.education = education;
        this.certifications = certifications;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<Experience> getExperience() {
        return experience;
    }

    public void setExperience(List<Experience> experience) {
        this.experience = experience;
    }

    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public List<String> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }

    @Override
    public String toString() {
        return "CandidateProfile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location='" + location + '\'' +
                ", summary='" + summary + '\'' +
                ", skills=" + skills +
                ", experience=" + experience +
                ", education=" + education +
                ", certifications=" + certifications +
                '}';
    }
}