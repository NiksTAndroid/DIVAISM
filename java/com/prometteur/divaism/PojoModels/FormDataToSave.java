package com.prometteur.divaism.PojoModels;

import java.io.File;
import java.util.List;

public class FormDataToSave {


    String savebodyType;
    String saveheightcm;
    String saveheightInch;
    String savegender;
    String saveAge;
    String saveFavBrands;
    String saveUnFavColor;
    String savePrice;
    List<File> imagesAdded;


    public FormDataToSave(String savebodyType, String saveheightcm, String saveheightInch,
                          String savegender, String saveAge, String saveFavBrands, String saveUnFavColor,
                          String savePrice, List<File> imagesAdded) {

        this.savebodyType = savebodyType;
        this.saveheightcm = saveheightcm;
        this.saveheightInch = saveheightInch;
        this.savegender = savegender;
        this.saveAge = saveAge;
        this.saveFavBrands = saveFavBrands;
        this.saveUnFavColor = saveUnFavColor;
        this.savePrice = savePrice;
        this.imagesAdded = imagesAdded;
    }



    public String getSavebodyType() {
        return savebodyType;
    }

    public void setSavebodyType(String savebodyType) {
        this.savebodyType = savebodyType;
    }

    public String getSaveheightcm() {
        return saveheightcm;
    }

    public void setSaveheightcm(String saveheightcm) {
        this.saveheightcm = saveheightcm;
    }

    public String getSaveheightInch() {
        return saveheightInch;
    }

    public void setSaveheightInch(String saveheightInch) {
        this.saveheightInch = saveheightInch;
    }

    public String getSavegender() {
        return savegender;
    }

    public void setSavegender(String savegender) {
        this.savegender = savegender;
    }

    public String getSaveAge() {
        return saveAge;
    }

    public void setSaveAge(String saveAge) {
        this.saveAge = saveAge;
    }

    public String getSaveFavBrands() {
        return saveFavBrands;
    }

    public void setSaveFavBrands(String saveFavBrands) {
        this.saveFavBrands = saveFavBrands;
    }

    public String getSaveUnFavColor() {
        return saveUnFavColor;
    }

    public void setSaveUnFavColor(String saveUnFavColor) {
        this.saveUnFavColor = saveUnFavColor;
    }

    public String getSavePrice() {
        return savePrice;
    }

    public void setSavePrice(String savePrice) {
        this.savePrice = savePrice;
    }

    public List<File> getImagesAdded() {
        return imagesAdded;
    }

    public void setImagesAdded(List<File> imagesAdded) {
        this.imagesAdded = imagesAdded;
    }
}
