package com.sagar.snaphub.HomeFragment;

import java.io.Serializable;

public class HomeModel implements Serializable {
    public String photoId, image_4k, image_1080p, image_720p, image_480p, name;
    public long color, height, width, likes, downloads, views;
    public boolean isLiked;

    public HomeModel(String photoId, String image_4k, String image_1080p, String image_720p, String image_480p, String name, long color, long height, long width, long likes, long downloads, long views) {
        this.photoId = photoId;
        this.image_4k = image_4k;
        this.image_1080p = image_1080p;
        this.image_720p = image_720p;
        this.image_480p = image_480p;
        this.name = name;
        this.color = color;
        this.height = height;
        this.width = width;
        this.likes = likes;
        this.downloads = downloads;
        this.views = views;
    }
}
