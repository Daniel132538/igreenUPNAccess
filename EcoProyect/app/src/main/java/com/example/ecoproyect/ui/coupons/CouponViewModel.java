package com.example.ecoproyect.ui.coupons;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecoproyect.Model.CouponItem;

import java.util.ArrayList;

public class CouponViewModel extends ViewModel {
    public ArrayList<CouponItem> couponItems;
}