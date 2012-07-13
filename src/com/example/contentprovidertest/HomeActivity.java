package com.example.contentprovidertest;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
;

public class HomeActivity extends FragmentActivity {

	
	private static final String TAG = HomeActivity.class.getSimpleName();
	
	private SimpleCursorAdapter adapter;
	
	private ListView namesList;
	
	// These are the Contacts rows that we will retrieve.
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
        Contacts._ID,
        Contacts.DISPLAY_NAME,
        Contacts.CONTACT_STATUS,
        Contacts.CONTACT_PRESENCE,
        Contacts.PHOTO_ID,
        Contacts.LOOKUP_KEY,
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        namesList = (ListView)this.findViewById(R.id.namesListView);
        
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, 
        		new String[] { Contacts.DISPLAY_NAME, Contacts.CONTACT_STATUS },
                new int[] { android.R.id.text1, android.R.id.text2 }, 0);
        
        namesList.setAdapter(adapter);
        namesList.setOnItemClickListener(new ItemListener());
        
        getSupportLoaderManager().initLoader(0, null, new MyLoaderCallbacks());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

   
   
    private class ItemListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long arg3) {
			Log.d(TAG, "clicked "+position);
			
			Cursor c = adapter.getCursor();
			int idIdx = c.getColumnIndex(Contacts._ID);
			if(c.moveToPosition(position)){
				Uri singleUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,c.getInt(idIdx));
				Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
				intent.putExtra(DetailActivity.INTENT_URI, singleUri);
				startActivity(intent);
			}
			
			//Uri rowUri = ContentUri.
			/*
			 * Contacts._ID,
        Contacts.DISPLAY_NAME,
        Contacts.CONTACT_STATUS,
        Contacts.CONTACT_PRESENCE,
        Contacts.PHOTO_ID,
        Contacts.LOOKUP_KEY,
        */
//			int nameIdx = c.getColumnIndex(Contacts.DISPLAY_NAME);
//			int statusIdx = c.getColumnIndex(Contacts.CONTACT_STATUS);
//			int presenceIdx = c.getColumnIndex(Contacts.CONTACT_PRESENCE);
//			int photoIdIdx = c.getColumnIndex(Contacts.PHOTO_ID);
//			int lookupKeyIdx = c.getColumnIndex(Contacts.LOOKUP_KEY);
//			if(c.moveToPosition(position)){
//				Log.d(TAG, "id: "+c.getInt(idIdx));
//				Log.d(TAG, "name: "+c.getString(nameIdx));
//				Log.d(TAG, "status: "+c.getString(statusIdx));
//				Log.d(TAG, "presence: "+c.getInt(presenceIdx));
//				Log.d(TAG, "photo id ref: "+c.getInt(photoIdIdx));
//				Log.d(TAG, "lookup key id: "+c.getInt(lookupKeyIdx));
//				
//			}
		}
    	
    }
    
    
    private class MyLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor>{

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			Uri baseUri = Contacts.CONTENT_URI;
			String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND (" + Contacts.HAS_PHONE_NUMBER + "=1) AND ("+ Contacts.DISPLAY_NAME + " != '' ))";
	        return new CursorLoader(HomeActivity.this, baseUri, CONTACTS_SUMMARY_PROJECTION, select, null, Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
			
			adapter.swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			adapter.swapCursor(null);
		}

		

    	
    }


    
}
