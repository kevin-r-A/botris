package com.csl.cs108ademoapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;


import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.ResponseToken;

public class LoginViewModel extends ViewModel {
    private final WebServiceRepository webServiceRepository;

    public LoginViewModel(@NonNull Application application) {

        webServiceRepository = new WebServiceRepository(application);
    }

    public static LoginViewModel getInstance(Application application) {
        return new LoginViewModel(application);
    }

    public ResponseToken Login(String userName, String password, String tenant){
        return webServiceRepository.Login(userName, password, tenant);
    }
}
