package com.example.onlinevotingapp2.Model;

public class Candidate {
    private String name;
    private String party;
    private String position;
    private String image;

    public Candidate(String name, String party, String position, String image) {
        this.name = name;
        this.party = party;
        this.position = position;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
