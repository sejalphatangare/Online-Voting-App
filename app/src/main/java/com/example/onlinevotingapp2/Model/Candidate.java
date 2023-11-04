package com.example.onlinevotingapp2.Model;

public class Candidate {
    private String name;
    private String party;
    private String image;
    private String id;
    int count=0;


    private String ele_name;

    public Candidate(String name, String party,  String image,String id,String ele_name) {
        this.name = name;
        this.party = party;
        this.image = image;
        this.id= id;
        this.ele_name=ele_name;
    }

    public String getEle_name() {
        return ele_name;
    }

    public void setEle_name(String ele_name) {
        this.ele_name = ele_name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}