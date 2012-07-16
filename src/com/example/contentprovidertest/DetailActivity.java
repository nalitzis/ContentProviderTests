package com.example.contentprovidertest;


import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends FragmentActivity {

	public static final String INTENT_URI = "INTENT_URI";
	
	private TextView phoneValue, statusValue;
	private ImageView image;
	
	private Uri uri;
	
	static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
        Contacts._ID,
        Contacts.DISPLAY_NAME,
        Contacts.CONTACT_STATUS,
        Contacts.CONTACT_PRESENCE,
        Contacts.PHOTO_ID,
        Contacts.LOOKUP_KEY,
        Contacts.HAS_PHONE_NUMBER,
    };
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		uri = (Uri)this.getIntent().getParcelableExtra(INTENT_URI);
		
		setContentView(R.layout.activity_detail);
		phoneValue = (TextView)this.findViewById(R.id.textView1);
		statusValue = (TextView)this.findViewById(R.id.textView3);
		image = (ImageView)this.findViewById(R.id.imageView1);
		
		getSupportLoaderManager().initLoader(0, null, new MyLoaderCallbacks());
	}
	
	 private class MyLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor>{

		 private String loadPhoneNumber(ContentResolver cr, long id){
			 String number = "";
			 
			 Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, null, null); 
		      if (phones.moveToFirst()) { 
		    	  number = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));                 
		      } 
		   phones.close(); 
		   
		   

		     return number;
		 }
		 
		 private Bitmap loadContactPhoto(ContentResolver cr, long  id,long photo_id) 
		 {

		     Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
		     InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
		     if (input != null) 
		     {
		         return BitmapFactory.decodeStream(input);
		     }
		     else
		     {
		         Log.d("PHOTO","first try failed to load photo");
		     }
		     byte[] photoBytes = null;
		     Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
		     Cursor c = cr.query(photoUri, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
		     try 
		     {
		         if (c.moveToFirst()) 
		             photoBytes = c.getBlob(0);

		     } catch (Exception e) {
		         e.printStackTrace();
		     } finally {
		         c.close();
		     }           

		     if (photoBytes != null)
		         return BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.length);
		     else
		         Log.d("PHOTO","second try also failed");
		     return null;
		 }
		 
			@Override
			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
				return new CursorLoader(DetailActivity.this, uri, CONTACTS_SUMMARY_PROJECTION, "", null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
				if(c.moveToFirst()){
					int idIdx = c.getColumnIndex(Contacts._ID);
					int nameIdx = c.getColumnIndex(Contacts.DISPLAY_NAME);
					int statusIdx = c.getColumnIndex(Contacts.CONTACT_STATUS);
					int photoIdIdx = c.getColumnIndex(Contacts.PHOTO_ID);
					Bitmap bmp = loadContactPhoto(getContentResolver(), c.getInt(idIdx), c.getInt(photoIdIdx));
					String hasPhone = c.getString(c.getColumnIndex(Contacts.HAS_PHONE_NUMBER)); 
					String phone = "";
					if (Boolean.parseBoolean(hasPhone)) { 
						 phone = loadPhoneNumber(getContentResolver(), c.getInt(idIdx));
						 phoneValue.setText(phone);
					}  
					
					
					
					
					
					statusValue.setText(c.getString(statusIdx));
					image.setImageBitmap(bmp);
					
					setTitle(c.getString(nameIdx));
				}
				//Log.d(TAG, "photo id ref: "+c.getInt(photoIdIdx));
			
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				
			}

			

	    	
	    }
}
