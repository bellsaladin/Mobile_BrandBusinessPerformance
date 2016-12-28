package com.bse.daisybuzz.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraActivity extends Activity {
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);

		preview = (SurfaceView) findViewById(R.id.iv_photo);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			Camera.CameraInfo info = new Camera.CameraInfo();

			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, info);

				if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					camera = Camera.open(i);
				}
			}
		}

		if (camera == null) {
			camera = Camera.open();
		}

		startPreview();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;

		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.options, menu);

		return (super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.camera) {
			if (inPreview) {
				camera.takePicture(null, null, photoCallback);
				inPreview = false;
			}
		}

		return (super.onOptionsItemSelected(item));
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (result == null) {
				result = size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;

				if (newArea < resultArea) {
					result = size;
				}
			}
		}

		return (result);
	}

	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(CameraActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);
				Camera.Size pictureSize = getSmallestPictureSize(parameters);

				if (size != null && pictureSize != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setPictureSize(pictureSize.width,
							pictureSize.height);
					parameters.setPictureFormat(ImageFormat.JPEG);
					camera.setParameters(parameters);
					cameraConfigured = true;
				}
				
				if (CameraActivity.this.getResources().getConfiguration().orientation !=
		                Configuration.ORIENTATION_LANDSCAPE)
		        {
		            parameters.set("orientation", "portrait");
		            // For Android Version 2.2 and above
		            camera.setDisplayOrientation(90);
		            // For Android Version 2.0 and above
		            parameters.setRotation(90);
		        }
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview = true;
		}
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};

	Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			camera.setDisplayOrientation(90);
			new SavePhotoTask().execute(data);
			camera.startPreview();
			inPreview = true;
		}
	};

	class SavePhotoTask extends AsyncTask<byte[], String, String> {
		@Override
		protected String doInBackground(byte[]... jpeg) {
			File photo = new File(Environment.getExternalStorageDirectory(),
					"photo.jpg");

			if (photo.exists()) {
				photo.delete();
			}

			try {
				FileOutputStream fos = new FileOutputStream(photo.getPath());

				fos.write(jpeg[0]);
				fos.close();
				// THIS IS VERY IMPORTANT
				Fragment1.imgPath = photo.getPath();
				CameraActivity.this.finish();
				BitmapFactory.Options options = new BitmapFactory.Options();
				Fragment1.imageBitmap = BitmapFactory.decodeByteArray(jpeg[0], 0,
						jpeg[0].length, options);
				Matrix mtx = new Matrix();
		          mtx.preRotate(90);

		        // Rotating Bitmap & convert to ARGB_8888
		        Fragment1.imageBitmap = Bitmap.createBitmap(Fragment1.imageBitmap, 0, 0, Fragment1.imageBitmap.getWidth(), Fragment1.imageBitmap.getHeight(), mtx, false);
		        Fragment1.imageBitmap = Fragment1.imageBitmap.copy(Bitmap.Config.RGB_565, true);
				
			} catch (java.io.IOException e) {
				Log.e("PictureDemo", "Exception in photoCallback", e);
			}

			return (null);
		}
		
		@Override
		protected void onPostExecute(String result) {
			Fragment1.imageView.setImageBitmap(Fragment1.imageBitmap);
			//Fragment1.imageBitmap.recycle();
			//Fragment1.imageBitmap = null;
		}
	}
}
