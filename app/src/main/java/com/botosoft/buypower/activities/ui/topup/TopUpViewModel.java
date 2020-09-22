package com.botosoft.buypower.activities.ui.topup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TopUpViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TopUpViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}