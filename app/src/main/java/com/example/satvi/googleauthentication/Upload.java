package com.example.satvi.googleauthentication;

public class Upload {


    private String title;
    private String imageUrl;
    private String description;

    public Upload() {

    }

    public Upload(String title, String imageUrl, String description) {

        if (title.trim().equals("")) {
            title = "No Title";
        }
        if (description.trim().equals("")) {
            description = "No description";
        }

        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    // public Upload(String title,String imageUrl, String description){


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
