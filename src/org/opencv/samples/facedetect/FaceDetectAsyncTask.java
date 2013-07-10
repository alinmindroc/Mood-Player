package org.opencv.samples.facedetect;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;

public class FaceDetectAsyncTask extends AsyncTask<Mat[], Void, String> {

	String str;
	Activity mContext;
	MyCallBack callBack;
	String source1, source2;
	Mat[] mats;
	Mat mT, mHappy, mSad, face;
	String mood;

	public FaceDetectAsyncTask(Mat[] mT, Activity context, MyCallBack callBack) {
		this.mats = mT;
		this.mContext = context;
		this.callBack = callBack;
	}

	@Override
	protected String doInBackground(Mat[]... arg0) {

		MatOfRect faces;
		Rect[] facesArray;
		Mat face;
		long startTime = System.currentTimeMillis();
		long detectionThreshold = 5000;

		face = null;
		mT = mats[0];
		mHappy = mats[1];
		mSad = mats[2];

		for (;;) {
			faces = new MatOfRect();
			FdActivity.mNativeDetector.detect(mT, faces);

			facesArray = faces.toArray();
			if (facesArray.length != 0) {
				try {
					face = mT.submat(facesArray[0]);
				} catch (Exception e) {
					face = mT;
				}

				Highgui.imwrite(Environment.getExternalStorageDirectory().getPath()
						+ "/moodplayer/mood2.jpg", face);
				break;
			} else if (System.currentTimeMillis() - startTime >= detectionThreshold) {
				break;
			}
		}

		return getMood(face, mSad, mHappy);
	}

	public String getMood(Mat face, Mat mSad, Mat mHappy) {
		return (compareProp(face, mSad) > compareProp(face, mHappy) ? "happy"
				: "sad");
	}

	public double compareProp(Mat m1, Mat m2) {
		// daca au dimensiuni diferite, fa-o pe m2 de aceeasi dimensiune cu m1
		if (m1 == null || m2 == null)
			return -1;
		if (m1.rows() != m2.rows() || m1.cols() != m2.cols()) {
			Mat m3 = new Mat(m1.rows(), m1.cols(), CvType.CV_8UC1);
			Imgproc.resize(m2, m3, m3.size());
			return Core.norm(m1, m3);
		} else {
			return Core.norm(m1, m2);
		}
	}

	protected void onPostExecute(String str) {
		callBack.run(str);
	}

	public static class MyCallBack {
		public void run(String s) {
		}
	};

}
