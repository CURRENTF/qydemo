package com.example.qydemo0.bean;

import java.util.List;

public class WorkDataBean {
    private int id;
    private String name,introduction,classifications;
    private CoverBean cover_url = new CoverBean();
    private List<String> tags;
    private videoUrlBean video_url;
    private int play_num, comment_num, like_num, dislike_num, favorites_num, learning_num;

    private Boolean is_like, is_dislike, is_follow;
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

    public Boolean getIs_dislike() {
        return is_dislike;
    }

    public Boolean getIs_follow() {
        return is_follow;
    }

    public Boolean getIs_like() {
        return is_like;
    }

    public void setIs_dislike(Boolean is_dislike) {
        this.is_dislike = is_dislike;
    }

    public void setIs_follow(Boolean is_follow) {
        this.is_follow = is_follow;
    }

    public void setIs_like(Boolean is_like) {
        this.is_like = is_like;
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

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public void setDislike_num(int dislike_num) {
        this.dislike_num = dislike_num;
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

    public int getLike_num() {
        return like_num;
    }

    public int getDislike_num() {
        return dislike_num;
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
        return cover_url;
    }

    public void setCover_url(CoverBean cover_url) {
        this.cover_url = cover_url;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setVideo_url(videoUrlBean video_url) {
        this.video_url = video_url;
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

    public videoUrlBean getVideo_url() {
        return video_url;
    }
}
