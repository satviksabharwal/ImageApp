package com.example.satvi.googleauthentication;

public class Upload {


    private String title;
    private String imageUrl;
    private String description;
    private String uploadId;

    public Upload() {

    }

    public Upload(String title, String imageUrl, String description,String uploadId) {

        if (title.trim().equals("")) {
            title = "No Title";
        }
        if (description.trim().equals("")) {
            description = "No description";
        }

        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.uploadId = uploadId;
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

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
