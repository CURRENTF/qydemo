package com.example.qydemo0.bean;

import java.util.List;

public class WorkDataBean {
    private int id;
    private String name,introduction,classifications;
    private CoverBean cover = new CoverBean();
    private List<String> tags;
    private videoUrlBean video;
    private int play_num, comment_num, favorites_num, learning_num;

    Belong belong = new Belong(-1,"","");

    public int getLearning_num() {
        return learning_num;
    }

    public void setLearning_num(int learning_num) {
        this.learning_num = learning_num;
    }

    public Belong getBelong() {
        return belong;
    }

    public void setBelong(Belong belong) {
        this.belong = belong;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlay_num(int play_num) {
        this.play_num = play_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public void setFavorites_num(int favorites_num) {
        this.favorites_num = favorites_num;
    }

    public int getPlay_num() {
        return play_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public int getFavorites_num() {
        return favorites_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setClassifications(String classifications) {
        this.classifications = classifications;
    }

    public CoverBean getCover_url() {
        return cover;
    }

    public void setCover_url(CoverBean cover_url) {
        this.cover = cover_url;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setVideo(videoUrlBean video) {
        this.video = video;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getClassifications() {
        return classifications;
    }


    public List<String> getTags() {
        return tags;
    }

    public videoUrlBean getVideo() {
        return video;
    }
}
