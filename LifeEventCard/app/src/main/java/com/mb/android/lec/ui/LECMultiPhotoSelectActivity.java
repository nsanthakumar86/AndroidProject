package com.mb.android.lec.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mb.android.lec.R;
import com.mb.android.lec.adapter.ImageAdapter;
import com.mb.android.lec.util.ItemOffsetDecoration;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * @author Paresh Mayani (@pareshmayani)
 */
public class LECMultiPhotoSelectActivity extends AppCompatActivity {

	public static final String SELECTED_IMAGES ="selected_images" ;
	public static final int GALLERY_REQUEST_CODE = 123;
	public static final int GALLERY_RESULT_CODE = 321;
	private ImageAdapter imageAdapter;
	private static final int REQUEST_FOR_STORAGE_PERMISSION = 123;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_multi_photo_select);

		populateImagesFromGallery();
	}

	public void btnChoosePhotosClick(){
		
		ArrayList<String> selectedItems = imageAdapter.getCheckedItems();

		if (selectedItems!= null && selectedItems.size() > 0) {
			Toast.makeText(LECMultiPhotoSelectActivity.this, "Total photos selected: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
			Log.d(LECMultiPhotoSelectActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putStringArrayList(SELECTED_IMAGES, selectedItems);
;			intent.putExtras(bundle);
			setResult(GALLERY_RESULT_CODE, intent);
			finish();
		}
	}

	private void populateImagesFromGallery() {
		if (!mayRequestGalleryImages()) {
			return;
		}

		ArrayList<String> imageUrls = loadPhotosFromNativeGallery();
		initializeRecyclerView(imageUrls);
	}

	private boolean mayRequestGalleryImages() {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}

		if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}

		if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
			//promptStoragePermission();
			showPermissionRationaleSnackBar();
		} else {
			requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_FOR_STORAGE_PERMISSION);
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_img, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_choose_img_cancel) {

			finish();
			return true;
		}else if(id == R.id.action_choose_img_done){
            btnChoosePhotosClick();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {

		switch (requestCode) {

			case REQUEST_FOR_STORAGE_PERMISSION: {

				if (grantResults.length > 0) {
					if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
						populateImagesFromGallery();
					} else {
						if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
							showPermissionRationaleSnackBar();
						} else {
							Toast.makeText(this, "Go to settings and enable permission", Toast.LENGTH_LONG).show();
						}
					}
				}

				break;
			}
		}
	}

	private ArrayList<String> loadPhotosFromNativeGallery() {
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
		Cursor imagecursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy + " DESC");

		ArrayList<String> imageUrls = new ArrayList<String>();

		for (int i = 0; i < imagecursor.getCount(); i++) {
			imagecursor.moveToPosition(i);
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
			imageUrls.add(imagecursor.getString(dataColumnIndex));

			System.out.println("=====> Array path => "+imageUrls.get(i));
		}

		return imageUrls;
	}

	private void initializeRecyclerView(ArrayList<String> imageUrls) {
		imageAdapter = new ImageAdapter(this, imageUrls);

		RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
		recyclerView.setAdapter(imageAdapter);
	}

	private void showPermissionRationaleSnackBar() {
		Snackbar.make(findViewById(R.id.button1), getString(R.string.permission_rationale),
				Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Request the permission
				ActivityCompat.requestPermissions(LECMultiPhotoSelectActivity.this,
						new String[]{READ_EXTERNAL_STORAGE},
						REQUEST_FOR_STORAGE_PERMISSION);
			}
		}).show();

	}
}