package com.example.onlinevotingapp2.Model;

public class Election {
    private String electionName;
    private String startDate;
    private String endDate;

    private String creator;
    private String id;
    public Election(String electionName, String startDate, String endDate,String creator,String id) {
        this.electionName = electionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.id=id;
        this.creator=creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElectionName() {
        return this.electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public String getStartDate() {
        return startDate;
    }

    @Override
    public String toString() {
        return "Election{" +
                "electionName='" + electionName + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", creator='" + creator + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
