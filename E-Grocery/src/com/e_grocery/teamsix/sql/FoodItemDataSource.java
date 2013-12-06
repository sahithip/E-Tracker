package com.e_grocery.teamsix.sql;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.e_grocery.teamsix.models.FoodItem;

public class FoodItemDataSource {
	private SQLiteDatabase database;
	private GroceryDBHelper dbHelper;
	private String[] allColumns = { GroceryDBHelper.FOODITEM_COLUMN_ID,
			GroceryDBHelper.FOODITEM_COLUMN_NAME,
			GroceryDBHelper.FOODITEM_COLUMN_EXPIRY,
			GroceryDBHelper.FOODITEM_COLUMN_NOTIFICATION_SETTING};
	
	public FoodItemDataSource(Context context) {
		dbHelper = new GroceryDBHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}

	public List<FoodItem> getAllFoodItems() {
		
		List<FoodItem> items = new ArrayList<FoodItem>();

		Cursor cursor;
		

			cursor = database.query(GroceryDBHelper.FOODITEM_TABLE_NAME,
		    		allColumns, null, null, null, null, GroceryDBHelper.FOODITEM_COLUMN_EXPIRY);

		
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	FoodItem item = cursorToFoodItem(cursor);
	    	items.add(item);
	    	cursor.moveToNext();
	    }
	    
	    // Make sure to close the cursor
	    cursor.close();
	    
	    return items;
	}
	
	public FoodItem getFoodItem(long id) {
		
		FoodItem item = null;

	    Cursor cursor = database.query(GroceryDBHelper.FOODITEM_TABLE_NAME,
	    		allColumns, GroceryDBHelper.FOODITEM_COLUMN_ID + " = " + id, null, null, null, null);
	    
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	item = cursorToFoodItem(cursor);
	    	cursor.moveToNext();
	    }
	    
	    // Make sure to close the cursor
	    cursor.close();
		
	    return item;
		
	}
	
	public FoodItem createFoodItem(String name, Date expiryDate, int notificationSetting) {
	    ContentValues values = new ContentValues();
	    
	    values.put(GroceryDBHelper.FOODITEM_COLUMN_NAME, name);
	    
	    String date_string = FoodItem.database_format.format(expiryDate);
	    values.put(GroceryDBHelper.FOODITEM_COLUMN_EXPIRY, date_string);
	    
	    values.put(GroceryDBHelper.FOODITEM_COLUMN_NOTIFICATION_SETTING, notificationSetting);
	    
	    long insertId = database.insertOrThrow(GroceryDBHelper.FOODITEM_TABLE_NAME, null,
	        values);
	    
	    // Get the item we just inserted so we can return it
	    Cursor cursor = database.query(GroceryDBHelper.FOODITEM_TABLE_NAME,
	        allColumns, GroceryDBHelper.FOODITEM_COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    
	    cursor.moveToFirst();
	    FoodItem newItem = cursorToFoodItem(cursor);
	    
	    cursor.close();
	    
	    return newItem;
	}
	
	public void updateFoodItem(long id, String name, Date expiryDate, int notificationSetting) {
		
		ContentValues values = new ContentValues();
	    
	    values.put(GroceryDBHelper.FOODITEM_COLUMN_NAME, name);
	    String date_string = FoodItem.database_format.format(expiryDate);
	    values.put(GroceryDBHelper.FOODITEM_COLUMN_EXPIRY, date_string);
	    values.put(GroceryDBHelper.FOODITEM_COLUMN_NOTIFICATION_SETTING, notificationSetting);
	    
	    database.update(GroceryDBHelper.FOODITEM_TABLE_NAME, values, GroceryDBHelper.FOODITEM_COLUMN_ID + " = " + id, null);
		
	}

	public void deleteFoodItem(FoodItem item) {
		long id = item.getId();
		System.out.println("Food item deleted with id: " + id);
	    database.delete(GroceryDBHelper.FOODITEM_TABLE_NAME, GroceryDBHelper.FOODITEM_COLUMN_ID
	        + " = " + id, null);
	}
	
	private FoodItem cursorToFoodItem(Cursor cursor) {
		FoodItem item = new FoodItem();
		item.setId(cursor.getLong(0));
		item.setName(cursor.getString(1));
		
		
		try {
			String date_string = cursor.getString(2);
			Date date = FoodItem.database_format.parse(date_string);
			item.setDate(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		item.setNotificationSetting(cursor.getInt(3));
		
	    return item;
	}

	public List<FoodItem> searchFoodItems(String query) {

		List<FoodItem> items = new ArrayList<FoodItem>();
		
	    Cursor cursor = database.query(GroceryDBHelper.FOODITEM_TABLE_NAME,
	    		allColumns, "name like '%" + query + "%'", null, null, null, GroceryDBHelper.FOODITEM_COLUMN_NAME);
	    
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	FoodItem item = cursorToFoodItem(cursor);
	    	items.add(item);
	    	cursor.moveToNext();
	    }
	    
	    // Make sure to close the cursor
	    cursor.close();
	    
	    return items;
	}
}
