package com.asset.tracker.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;

public class BaseActivity extends Activity{
	
	public void showAlertDialog(Context context,String title, String message){
		AlertDialog.Builder builderAlert = new AlertDialog.Builder(context);
		builderAlert.setTitle(title);
		builderAlert.setMessage(message);
		builderAlert.setCancelable(true);
		builderAlert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builderAlert.create();
		alert.show();
		
	}
	
	public static boolean isNetworkAvailable(Context context) 
	{
		return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}
}
