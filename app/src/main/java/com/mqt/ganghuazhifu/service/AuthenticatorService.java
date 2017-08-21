package com.mqt.ganghuazhifu.service;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

/**
 * Created by danding1207 on 16/10/26.
 */

public class AuthenticatorService extends Service {

    private static final String LOG_TAG = AuthenticatorService.class.getSimpleName();
    private AccountAuthenticatorImpl accountAuthenticator;

    public AuthenticatorService() {
        super();
        accountAuthenticator = new AccountAuthenticatorImpl(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            ret = accountAuthenticator.getIBinder();
        }
        return ret;
    }

}
