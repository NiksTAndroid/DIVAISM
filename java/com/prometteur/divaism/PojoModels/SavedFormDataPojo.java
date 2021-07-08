package com.prometteur.divaism.PojoModels;

public class SavedFormDataPojo {

    private int id;
    private String UserId;
    private String data;

    public SavedFormDataPojo(String UserId, String data) {

        this.data = data;
        this.UserId = UserId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
