package com.navigatpeer.models;

public class Consultations {

    private String topicName, starter, dateCreated;

    public Consultations() {
    }

    public Consultations(String topicName, String starter, String dateCreated) {
        this.topicName = topicName;
        this.starter = starter;
        this.dateCreated = dateCreated;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getStarter() {
        return starter;
    }

    public void setStarter(String starter) {
        this.starter = starter;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}