package com.navigatpeer.models;

public class Forums {

    String forumName, description;

    public Forums() {
    }

    public Forums(String forumName, String description) {
        this.forumName = forumName;
        this.description = description;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
