package com.e_grocery.teamsix.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.e_gro.teamsix.activity.R;
import com.e_grocery.teamsix.models.FoodItem;
import com.e_grocery.teamsix.parser.XmlParser;
import com.e_grocery.teamsix.sql.FoodItemDataSource;



public class MainPageActivity extends Activity {
	Button scanButton;
	String itemname;
	Button listButton;
	Button addButton;
	Button exitButton;
	private static final int ZBAR_SCANNER_REQUEST = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		scanButton = (Button) findViewById(R.id.button_scan);

		scanButton.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isCameraAvailable()) {
					Intent intent = new Intent(MainPageActivity.this, ZBarScannerActivity.class);
					startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
				} else {
					Toast.makeText(MainPageActivity.this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
				}
			}

			public boolean isCameraAvailable() {
				PackageManager pm = getPackageManager();
				return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
			}

		});

		addButton = (Button)findViewById(R.id.button_addmanual);
		addButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPageActivity.this, ManualAddItem.class);
				startActivityForResult(intent, 1);

			}
		});

		listButton = (Button) findViewById(R.id.button_mylist);

		listButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainPageActivity.this, ShowListActivity.class);
				startActivityForResult(intent, 2);

			}
		});
		
		exitButton = (Button)findViewById(R.id.button_exit);
		
		exitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				moveTaskToBack(true);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_page, menu);
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(" Camera is available","on activity result");
		//Log.v(String.valueOf(requestCode), "hi");
		//Log.v(String.valueOf(RESULT_OK), "hi here");
		String test = ""+resultCode;
		Log.v(" Camera is available",test);
		if (resultCode == RESULT_OK) 
		{

			Log.v(" Camera is available","result ok");
			// Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
			// Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
			String upc = data.getStringExtra(ZBarConstants.SCAN_RESULT);
			Log.v(" Camera is available",upc);
			//String itemname = 
			if(upc.length()==13){
				getUPCData(upc);
			}else{
				String[] upcarray= upc.split("@");	
				Log.v("array",upcarray[0]);
				String name=upcarray[0];  
				String date = upcarray[1];// Fruit Jam|12/04/2014
				Log.v(name,date);
				//Intent intent = new Intent(MainPageActivity.this, ManualAddItem.class);
				//intent.putExtra("ItemName", name);
				//intent.putExtra("Date", date);
				//startActivity(intent);
				//Date expiryDate = new Date(expiryPicker.getYear() -1900, expiryPicker.getMonth(), expiryPicker.getDayOfMonth());
				Date expiryDate = new Date(date);
				//Spinner spinner = (Spinner) findViewById(R.id.add_spinner_notifications);
				//int notificationSetting = spinner.getSelectedItemPosition();
				int notificationSetting = 1;
				long expiryWarningDays = notificationSetting;//notificationSetting == 0 ? 7 : 1;
				Date notificationDate = new Date(expiryDate.getTime() - expiryWarningDays * 24 * 60 * 60 * 1000); 

				FoodItemDataSource datasource = new FoodItemDataSource(MainPageActivity.this);
		        datasource.open();
		        
		        FoodItem foodItem = datasource.createFoodItem(name, expiryDate, notificationSetting);
		        
		        NotificationService noteService = new NotificationService(MainPageActivity.this);
		        noteService.scheduleExpiryWarning(notificationDate, foodItem);
		        				
				//finish();

			}
			//Log.v(" itemnamee",itemname);
			//Toast.makeText(MainPageActivity.this, itemname, Toast.LENGTH_SHORT).show();


			//Toast.makeText(MainPageActivity.this, "Scan Result = " + upc, Toast.LENGTH_SHORT).show();
			//Toast.makeText(MainPageActivity.this, "Scan Result Type = " + data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE), Toast.LENGTH_SHORT).show();
			// The value of type indicates one of the symbols listed in Advanced Options below.
		} 
		else if(resultCode == RESULT_CANCELED) {

			//Toast.makeText(MainPageActivity.this, "Camera unavailable", Toast.LENGTH_SHORT).show();
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	private void getUPCData(String upc){

		itemname="";
		String urlString ="http://api.upcdatabase.org/xml/43611e699c96c17a3078dfafa1aeb212/"+upc; 
		new AsyncTask<String, Void, String>() {
			protected String doInBackground(String... params) {
				{
					try {


						HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
						HttpGet httpget = new HttpGet(params[0]); // Set the action you want to do
						HttpResponse response = httpclient.execute(httpget); // Executeit
						HttpEntity entity = response.getEntity(); 
						InputStream is = entity.getContent(); // Create an InputStream with the response
						BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
						StringBuilder sb = new StringBuilder();
						String line = null;
						while ((line = reader.readLine()) != null) // Read line by line
							sb.append(line + "\n");

						params[0] = sb.toString(); // Result is here
						params[1] = new XmlParser().processData(params[0]);
						is.close(); // Close the stream
						return params[1];
					} catch (UnsupportedEncodingException e) {
					} catch (MalformedURLException e) {
					} catch (IOException e) {
					}
				}
				return params[1];
			}

			@Override
			protected void onPostExecute(String result)
			{
				//Call function that shows and stores item name
				Intent intent = new Intent(MainPageActivity.this, ManualAddItem.class);
				intent.putExtra("ItemName", result);
				startActivity(intent);
				
				//Toast.makeText(MainPageActivity.this, itemname, Toast.LENGTH_SHORT).show();

			}

		}.execute(urlString,itemname);

	}	
	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
		InputStream in = entity.getContent();


		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n>0) {
			byte[] b = new byte[4096];
			n =  in.read(b);


			if (n>0) out.append(new String(b, 0, n));
		}


		return out.toString();
	}

}
