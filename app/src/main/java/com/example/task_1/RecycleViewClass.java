package com.example.task_1;

public class RecycleViewClass {

    private String name;
    private String ImageLink;
    private String likes;
    private String status;

    public RecycleViewClass(String name, String imageLink,String likes,String status) {
        this.name = name;
        this.status = status;
        this.ImageLink = imageLink;
        this.likes = likes;
    }

    public RecycleViewClass() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }
}
