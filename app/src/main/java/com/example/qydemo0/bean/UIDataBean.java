package com.example.qydemo0.bean;

public class UIDataBean {

    private Boolean is_like, is_dislike, is_follow, is_learning;
    private int like_num, dislike_num;

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public int getDislike_num() {
        return dislike_num;
    }

    public void setDislike_num(int dislike_num) {
        this.dislike_num = dislike_num;
    }

    public UIDataBean() {
    }

    public Boolean getIs_like() {
        return is_like;
    }

    public void setIs_like(Boolean is_like) {
        this.is_like = is_like;
    }

    public Boolean getIs_dislike() {
        return is_dislike;
    }

    public void setIs_dislike(Boolean is_dislike) {
        this.is_dislike = is_dislike;
    }

    public Boolean getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(Boolean is_follow) {
        this.is_follow = is_follow;
    }

    public Boolean getIs_learning() {
        return is_learning;
    }

    public void setIs_learning(Boolean is_learning) {
        this.is_learning = is_learning;
    }
}
