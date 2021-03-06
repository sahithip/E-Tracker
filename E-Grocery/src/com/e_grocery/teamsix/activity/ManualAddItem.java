package com.e_grocery.teamsix.activity;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.e_gro.teamsix.activity.R;
import com.e_grocery.teamsix.models.FoodItem;
import com.e_grocery.teamsix.sql.FoodItemDataSource;




public class ManualAddItem extends Activity {
	
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_add_item);
	// Show the Up button in the action bar.
	setupActionBar(); //changed from setupActionBar to getActionBar
	
	//Notification Settings Spinner...
	String itemname ="";
	String date ="";
	Intent i = getIntent();
	itemname= i.getStringExtra("ItemName");

	if(itemname==null||"".equalsIgnoreCase(itemname)){
		
	}
	else{
	//	R.string. =itemname;
		TextView tv = (TextView)findViewById(R.id.add_name);
		tv.setText(itemname);

	}
			Spinner spinner = (Spinner) findViewById(R.id.add_spinner_notifications);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
					R.array.notifications_spinner_array, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			
			//Save Button...
			Button saveButton = (Button) findViewById(R.id.add_button_save);
			        
			saveButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					EditText nameEditText = (EditText) findViewById(R.id.add_name);
					String name = nameEditText.getText().toString();
					
					DatePicker expiryPicker = (DatePicker) findViewById(R.id.edit_picker_expiry);
					Date expiryDate = new Date(expiryPicker.getYear() -1900, expiryPicker.getMonth(), expiryPicker.getDayOfMonth());
					String e = expiryDate+"";
					Log.v("date", e);
					
					Spinner spinner = (Spinner) findViewById(R.id.add_spinner_notifications);
					int notificationSetting = spinner.getSelectedItemPosition();
			
					long expiryWarningDays = notificationSetting == 0 ? 7 : 1;
					Date notificationDate = new Date(expiryDate.getTime() - expiryWarningDays * 24 * 60 * 60 * 1000); 

					FoodItemDataSource datasource = new FoodItemDataSource(ManualAddItem.this);
			        datasource.open();
			        
			        FoodItem foodItem = datasource.createFoodItem(name, expiryDate, notificationSetting);
			        
			        NotificationService noteService = new NotificationService(ManualAddItem.this);
			        noteService.scheduleExpiryWarning(notificationDate, foodItem);
			        				
					finish();
					Log.v("finished","added");
				}
			});
			
			//Cancel Button...
			Button cancelButton = (Button) findViewById(R.id.add_button_cancel);
			
			cancelButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					finish();
					
				}
			});
			
		}

		/**
		 * Set up the {@link android.app.ActionBar}.
		 */
		private void setupActionBar() {

			getActionBar().setDisplayHomeAsUpEnabled(true);

		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.add_item, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			}
			return super.onOptionsItemSelected(item);
			
}
	
}
