package com.example.ecoproyect.Model;

public class SpinnerFlagItem {
    private String telephonePrefix;
    private int spinnerItemImage;

    public SpinnerFlagItem(String country, int spinnerItemImage) {
        this.telephonePrefix = country;
        this.spinnerItemImage = spinnerItemImage;
    }

    public String getTelephonePrefix() {
        return telephonePrefix;
    }

    public void setTelephonePrefix(String telephonePrefix) {
        this.telephonePrefix = telephonePrefix;
    }

    public int getSpinnerItemImage() {
        return spinnerItemImage;
    }

    public void setSpinnerItemImage(int spinnerItemImage) {
        this.spinnerItemImage = spinnerItemImage;
    }
}
