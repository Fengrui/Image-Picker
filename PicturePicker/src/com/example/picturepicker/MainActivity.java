package com.example.picturepicker;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {


	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private static final String PHOTO_PATH = "test";
	private Uri mImageCaptureUri;
	private TextView infoTextView;
	private ImageView mImageView;
	static Context CONTEXT;
//	private String photoFilename;
//	private File photoFileParent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CONTEXT = this;
		infoTextView = (TextView) findViewById(R.id.description);
		mImageView = (ImageView) findViewById(R.id.image_view);
		Button uploadFromGallery = (Button) findViewById(R.id.upload_from_gallery);
		uploadFromGallery.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				uploadFromGallery();
			}
		});
		Button uploadFromCamera = (Button) findViewById(R.id.upload_from_camera);
		uploadFromCamera.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				uploadFromCamera();
			}		
		});
		
	}

	//upload picture by taking a picture using camera
	private void uploadFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		//photoFilename = "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
		
		//photoFileParent = Environment.getExternalStoragePublicDirectory(PHOTO_PATH);
		
		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(PHOTO_PATH), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));//photoFileParent, photoFilename));

		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		
		try {
			intent.putExtra("return-data", true);
			startActivityForResult(intent, PICK_FROM_CAMERA);
		} 
		catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//upload from local gallery
	private void uploadFromGallery(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	    Bitmap photo = null;
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	
		    	//display the picture
		    	
		    	photo = decodeFile(mImageCaptureUri.getPath());
		    	infoTextView.setText(mImageCaptureUri.getPath());
		    	mImageView.setImageBitmap(photo);

		        //File f = new File(mImageCaptureUri.getPath());            
		        //if (f.exists()) f.delete();
		        break;

		    case PICK_FROM_FILE: 
		    	
		    	//display the picture
		    	mImageCaptureUri = data.getData();
		    	//Uri filePathFromActivity = (Uri) extras.get(Intent.EXTRA_STREAM);
		    	mImageCaptureUri = Uri.parse(getRealPathFromUri( (Activity) MainActivity.this, mImageCaptureUri));
		    	infoTextView.setText(mImageCaptureUri.getPath());
		    	photo = decodeFile(mImageCaptureUri.getPath());
		    	mImageView.setImageBitmap(photo);
		    	
		    	break;	    	
	    }
	    //upload the new Tweet with the picture and refresh the TweetList to show the new Tweet
	}
	
	
	//resize the picture to fit the size of imageView
	private Bitmap decodeFile(String path){
		//Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(path,o);

	    //The new size we want to scale to
	    final int REQUIRED_SIZE=mImageView.getWidth();

	    //Find the correct scale value. It should be the power of 2.
	    int scale=1;
	    while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	    	scale*=2;

	    //Decode with inSampleSize
	    BitmapFactory.Options o2 = new BitmapFactory.Options();
	    o2.inSampleSize=scale;
	    return BitmapFactory.decodeFile(path, o2);
	}
	
	//get real file path from uri for picking a picture from the gallery
	public static String getRealPathFromUri(Activity activity, Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    //Cursor cursor = (new CursorLoader(CONTEXT, contentUri, proj, null, null, null)).loadInBackground();
		@SuppressWarnings("deprecation")
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
