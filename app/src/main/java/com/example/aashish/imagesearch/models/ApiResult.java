package com.example.aashish.imagesearch.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aashish on 6/6/16.
 * Model class for the response
 */
public class ApiResult {

    List<Page> pages;

    public ApiResult() {
        pages = new ArrayList<>();
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

}
