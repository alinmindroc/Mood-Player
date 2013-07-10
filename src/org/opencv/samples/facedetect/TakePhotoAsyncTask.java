package org.opencv.samples.facedetect;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;

public class TakePhotoAsyncTask extends AsyncTask<String, Void, String> {

	String str;
	Activity mContext;
	String source1, source2;
	Camera camera;
	String mood;

	public TakePhotoAsyncTask(Camera camera, Activity context) {
		this.camera = camera;
		this.mContext = context;
	}

	@Override
	protected String doInBackground(String... arg0) {

		camera.startPreview();
		camera.takePicture(null, null,
				new PhotoHandler(mContext.getApplicationContext()));
		return "";

	}

	protected void onPostExecute(String str) {
	}

}
