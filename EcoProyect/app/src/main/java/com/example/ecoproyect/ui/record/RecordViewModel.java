package com.example.ecoproyect.ui.record;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecoproyect.Model.RecordItem;

import java.util.ArrayList;

public class RecordViewModel extends ViewModel {

    public ArrayList<RecordItem> recordItems;
    private final MutableLiveData<String> mText;

    public RecordViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}