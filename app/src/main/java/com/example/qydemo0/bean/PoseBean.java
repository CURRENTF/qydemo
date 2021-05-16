package com.example.qydemo0.bean;

public class PoseBean {
    double[][][] pose_model, pose_input;

    public double[][][] getPose_model() {
        return pose_model;
    }

    public void setPose_model(double[][][] pose_model) {
        this.pose_model = pose_model;
    }

    public double[][][] getPose_input() {
        return pose_input;
    }

    public void setPose_input(double[][][] pose_input) {
        this.pose_input = pose_input;
    }
}
