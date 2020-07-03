package com.smart.himalaya.beans;

public class Execution {
    private int viewId;
    private String action;
    private String description;

    public Execution(int viewId, String action) {
        this.viewId = viewId;
        this.action = action;
    }

    public Execution(int viewId, String action, String description) {
        this.viewId = viewId;
        this.action = action;
        this.description = description;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
