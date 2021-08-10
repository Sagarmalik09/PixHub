package com.example.snaphub.TrendingFragment.Collection;

import java.util.ArrayList;
import java.util.List;

public class CollectionModel {

    public String collectionId,  collectionTitle;
    public int totalCount;
    public ArrayList<String> imageUrlList_upTo_4;

    public CollectionModel(String collectionId, String collectionTitle, int totalCount, ArrayList<String> imageUrlList_upTo_4) {
        this.collectionId = collectionId;
        this.collectionTitle = collectionTitle;
        this.totalCount = totalCount;
        this.imageUrlList_upTo_4 = imageUrlList_upTo_4;
    }
}
