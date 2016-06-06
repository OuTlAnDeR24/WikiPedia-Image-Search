package com.example.aashish.imagesearch.models;

/**
 * Created by aashish on 6/6/16.
 * Model class for Page Data
 */
public class Page {

    private int pageId;

    private String title;
    private Thumbnail thumbnail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title.toLowerCase();
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
