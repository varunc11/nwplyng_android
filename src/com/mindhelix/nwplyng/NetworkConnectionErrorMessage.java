package com.mindhelix.nwplyng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

public class NetworkConnectionErrorMessage {
	
	public static AlertDialog showNetworkError(Context context, Activity activity) {
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View customTitle = inflater.inflate(R.layout.no_network_dialog_title, null);
		
		AlertDialog.Builder noNetworkBuilder = new AlertDialog.Builder(context);
		noNetworkBuilder.setCustomTitle(customTitle)
						.setMessage("The internet connection appears to be offline. Check your network or try again in a minute.")
			   			.setCancelable(false)
			   			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			   				public void onClick(DialogInterface dialog, int id) {
			   					dialog.dismiss();
			   				}
			   			});
		
		AlertDialog showError = noNetworkBuilder.create();
		
		
		return showError;
	}

}
