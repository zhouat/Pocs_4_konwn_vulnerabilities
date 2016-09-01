package com.example.serv_attack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticationService extends Service {  
  
    private static final String TAG = "AuthenticationService";  
    private Authenticator mAuthenticator;  
    @Override  
    public void onCreate() {  
         mAuthenticator = new Authenticator(this);  
    }  
    @Override  
    public IBinder onBind(Intent intent) {  
        return mAuthenticator.getIBinder();  
    }  
}  
