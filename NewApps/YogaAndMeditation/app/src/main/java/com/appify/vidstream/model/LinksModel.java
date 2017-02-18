package com.appify.vidstream.model;

/**
 * Created by ams on 06-02-2017.
 */

public class LinksModel {

    private String linkID, linkName, linkImage, linkURL;

    public LinksModel(){}

    public LinksModel(String linkID, String linkName, String linkImage, String linkURL) {
        this.linkID = linkID;
        this.linkName = linkName;
        this.linkImage = linkImage;
        this.linkURL = linkURL;
    }

    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String linkID) {
        this.linkID = linkID;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }
}
