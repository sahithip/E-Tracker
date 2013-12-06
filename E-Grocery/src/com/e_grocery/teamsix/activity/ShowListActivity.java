package com.e_grocery.teamsix.activity;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.e_gro.teamsix.activity.R;
import com.e_grocery.teamsix.models.FoodItem;
import com.e_grocery.teamsix.sql.FoodItemDataSource;


public class ShowListActivity extends Activity {
	
	private ArrayAdapter<FoodItem> adapter;
	private FoodItemDataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.grocery_list);
		
	//	ListView lv = (ListView) findViewById(R.id.foodlist);
		showArray();
	}
	
	protected void showArray(){
		datasource = new FoodItemDataSource(this);
		datasource.open();

		List<FoodItem> items = datasource.getAllFoodItems();

		//Populate ListView...
		ListView listview = (ListView) findViewById(R.id.foodlist);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				FoodItemAdapter.FoodItemHolder h = (FoodItemAdapter.FoodItemHolder) view.getTag();
				long foodItemID = h.foodItem.getId();

				Intent intent = new Intent(ShowListActivity.this, EditItemActivity.class);
				intent.putExtra("ID", foodItemID);

				startActivityForResult(intent, 1);

			}

		});
		adapter = new FoodItemAdapter(this,
				R.layout.food_item_cell, items);

		listview.setAdapter(adapter);

		datasource.close();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		showArray();
		
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

}
