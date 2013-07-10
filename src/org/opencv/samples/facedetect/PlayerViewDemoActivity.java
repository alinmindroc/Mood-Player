/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencv.samples.facedetect;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.opencv.samples.facedetect.PhotoHandler;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple YouTube Android API demo application which shows how to create a
 * simple application that displays a YouTube Video in a
 * {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend
 * {@link YouTubeBaseActivity}.
 */
public class PlayerViewDemoActivity extends YouTubeFailureRecoveryActivity
		implements CvCameraViewListener2 {

	private int cameraId = 0;
	private Camera camera;

	private String htmlSource1, htmlSource2;
	private Mat mGray;
	private Mat mRgba;

	int progress1, progress2, progress3;

	private YouTubePlayer player;
	private Button b;
	private ArrayList<String> songs;
	private Mat mSad, mHappy, face;

	// actualizeaza playlistul de melodii
	public void setIdArray() {
		b.setEnabled(false);
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			final String tag = getTag(progress1, progress2, progress3);
			String URL = "http://www.last.fm/tag/";
			URL = URL + tag + "/videos";

			ServerFetchAsyncTask down1 = new ServerFetchAsyncTask(URL,
					PlayerViewDemoActivity.this,
					new ServerFetchAsyncTask.MyCallBack() {
						public void run(String[] sv) {
							htmlSource1 = sv[0];
							htmlSource2 = sv[1];

							String separator = new String("<img src=");

							// extract the first id array
							String parts1[] = htmlSource1.split(separator);
							// first and last 3 lines are not interesting
							for (int i = 1; i < parts1.length - 3; i++) {
								parts1[i] = parts1[i].substring(
										parts1[i].indexOf("vi/"),
										parts1[i].indexOf(".jpg"));
								parts1[i] = parts1[i].substring(3,
										parts1[i].length() - 2);
							}

							String parts2[] = htmlSource2.split(separator);
							for (int i = 1; i < parts2.length - 3; i++) {
								parts2[i] = parts2[i].substring(
										parts2[i].indexOf("vi/"),
										parts2[i].indexOf(".jpg"));
								parts2[i] = parts2[i].substring(3,
										parts2[i].length() - 2);
							}

							// final id array
							songs = new ArrayList<String>();

							InputStream inputStream = getResources()
									.openRawResource(getRawId(tag));
							BufferedReader br = new BufferedReader(
									new InputStreamReader(inputStream));
							String s;
							try {
								while ((s = br.readLine()) != null) {
									songs.add(s);
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							for (int i = 1; i < parts1.length - 3; i++)
								songs.add(parts1[i]);
							for (int i = 1; i < parts2.length - 3; i++)
								songs.add(parts2[i]);
							// we use it in playvideoatselection() to get random
							// id
							b.setEnabled(true);
						}
					});
			Toast.makeText(PlayerViewDemoActivity.this, "downloading id list",
					Toast.LENGTH_SHORT).show();
			down1.execute();
		} else {
			Toast.makeText(PlayerViewDemoActivity.this, "Please turn wi-fi on",
					Toast.LENGTH_SHORT).show();
		}
	}

	public int getRawId(String tag) {
		if (tag.equals("happy")) {
			return R.raw.happy;
		}
		if (tag.equals("happy%20hardcore")) {
			return R.raw.happy_hardcore;
		}
		if (tag.equals("sad%20mood")) {
			return R.raw.sad_mood;
		}
		if (tag.equals("great")) {
			return R.raw.great;
		}
		if (tag.equals("bored")) {
			return R.raw.bored;
		}
		return 0;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// timerAlert();
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}

	public void onResume() {
		super.onResume();

		if (!getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
					.show();
		} else {
			cameraId = findFrontFacingCamera();
			if (cameraId < 0) {
				Toast.makeText(this, "No front facing camera found.",
						Toast.LENGTH_LONG).show();
			} else {
				camera = Camera.open(cameraId);
			}
		}
		camera.startPreview();

		setContentView(R.layout.playerview_demo);
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);

		b = (Button) findViewById(R.id.button1);
		SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		SeekBar seekBar3 = (SeekBar) findViewById(R.id.seekBar3);

		// get seekbar values
		seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				// seekBarValue.setText(String.valueOf(progress));
				progress1 = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				setIdArray();

			}
		});

		seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				// seekBarValue.setText(String.valueOf(progress));
				progress2 = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				setIdArray();

			}
		});

		seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				// seekBarValue.setText(String.valueOf(progress));
				progress3 = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				setIdArray();

			}

		});

		Button bPhoto = (Button) findViewById(R.id.photoButton);
		bPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				camera.startPreview();

				camera.takePicture(null, null, new PhotoHandler(
						getApplicationContext()));

			};

		});

		timerAlert();

		Button bMood = (Button) findViewById(R.id.moodButton);
		bMood.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File root = Environment.getExternalStorageDirectory();
				File moodFile = new File(root, "/moodplayer/mood.jpg");
				File happyFile = new File(root, "/moodplayer/face_happy.jpg");
				File sadFile = new File(root, "/moodplayer/face_sad.jpg");

				Mat mMood = Highgui.imread(moodFile.getAbsolutePath(),
						Highgui.CV_LOAD_IMAGE_GRAYSCALE);
				mHappy = Highgui.imread(happyFile.getAbsolutePath(),
						Highgui.CV_LOAD_IMAGE_GRAYSCALE);
				mSad = Highgui.imread(sadFile.getAbsolutePath(),
						Highgui.CV_LOAD_IMAGE_GRAYSCALE);

				// 10130 diferite
				// 2998 aproape la fel
				// 0 identice

				face = null;
				Mat mT;
				Mat mMoodGray = Highgui.imread(moodFile.getAbsolutePath(),
						Highgui.CV_LOAD_IMAGE_GRAYSCALE);
				MatOfRect faces = new MatOfRect();

				mT = mMoodGray.t();
				Core.flip(mT, mT, 0);

				Rect half = new Rect(0, mT.rows() / 2, mT.cols(),
						mT.rows() / 2 - 10);
				mT = mT.submat(half);
				while (true) {
					FdActivity.mNativeDetector.detect(mT, faces);
					// Highgui.imwrite(Environment.getExternalStorageDirectory().getPath()
					// + "/moodplayer/mood2.jpg", mT);

					Rect[] facesArray = faces.toArray();
					if (facesArray.length != 0) {
						face = mT.submat(facesArray[0]);
						Toast.makeText(PlayerViewDemoActivity.this, "toast",
								Toast.LENGTH_LONG).show();
						TextView tvTop = (TextView) findViewById(R.id.topText);
						/*
						 * tvTop.setText("happy " +
						 * Double.toString(compareProp(face, mHappy)) + "sad " +
						 * Double.toString(compareProp(face, mSad)));
						 */

						Highgui.imwrite(Environment
								.getExternalStorageDirectory().getPath()
								+ "/moodplayer/mood2.jpg", face);
						break;
					}

				}
			}
		});

	}

	public double compareProp(Mat m1, Mat m2) {
		// daca au dimensiuni diferite, fa-o pe m2 de aceeasi dimensiune cu m1
		if (m1.rows() != m2.rows() || m1.cols() != m2.cols()) {
			Mat m3 = new Mat(m1.rows(), m1.cols(), CvType.CV_8UC1);
			Imgproc.resize(m2, m3, m3.size());
			return Core.norm(m1, m3);
		} else {
			return Core.norm(m1, m2);
		}
	}

	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
	}

	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		return mRgba;
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {

		setIdArray();
		this.player = player;
		if (!wasRestored) {
			playVideoAtSelection();
		}
	}

	private void playVideoAtSelection() {
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (songs != null) {
					// play youtube video based on random id
					Playlist p = new Playlist(songs);
					player.loadVideos(p.playlist);
				} else {
					Toast.makeText(PlayerViewDemoActivity.this,
							"Please turn wi-fi on", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	public String getTag(int progress1, int progress2, int progress3) {
		String tag = new String("happy");
		if (progress1 < 50 && progress2 < 50 && progress3 < 50)
			tag = new String("happy");
		else if (progress1 > 50 && progress2 < 50 && progress3 < 50)
			tag = new String("sad%20mood");
		else if (progress1 < 50 && progress2 > 50 && progress3 < 50)
			tag = new String("happy%20hardcore");
		else if (progress1 < 50 && progress2 < 50 && progress3 > 50)
			tag = new String("bored");
		else if (progress1 > 50 && progress2 > 50 && progress3 > 50)
			tag = new String("sad");
		else
			tag = new String("great");
		return tag;
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	@Override
	protected void onPause() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	public void showalert() {

		// Toast.makeText(PlayerViewDemoActivity.this, "timer",
		// Toast.LENGTH_SHORT).show();
		// camera.startPreview();
		if (camera != null) {
			camera.startPreview();
			camera.takePicture(null, null, new PhotoHandler(
					getApplicationContext()));

			File root = Environment.getExternalStorageDirectory();
			File moodFile = new File(root, "/moodplayer/mood.jpg");
			File happyFile = new File(root, "/moodplayer/face_happy.jpg");
			File sadFile = new File(root, "/moodplayer/face_sad.jpg");

			Mat mMood = Highgui.imread(moodFile.getAbsolutePath(),
					Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			mHappy = Highgui.imread(happyFile.getAbsolutePath(),
					Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			mSad = Highgui.imread(sadFile.getAbsolutePath(),
					Highgui.CV_LOAD_IMAGE_GRAYSCALE);

			// 10130 diferite
			// 2998 aproape la fel
			// 0 identice

			face = null;
			Mat mT;
			Mat mMoodGray = Highgui.imread(moodFile.getAbsolutePath(),
					Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			MatOfRect faces = new MatOfRect();

			mT = mMoodGray.t();
			Core.flip(mT, mT, 0);

			Rect half = new Rect(0, mT.rows() / 2, mT.cols(),
					mT.rows() / 2 - 10);
			mT = mT.submat(half);

			long startTime = System.currentTimeMillis();
			long threshold = 5000;

			while (true) {
				FdActivity.mNativeDetector.detect(mT, faces);
				// Highgui.imwrite(Environment.getExternalStorageDirectory().getPath()
				// + "/moodplayer/mood2.jpg", mT);

				Rect[] facesArray = faces.toArray();
				if (facesArray.length != 0) {
					face = mT.submat(facesArray[0]);
					Toast.makeText(PlayerViewDemoActivity.this, "toast",
							Toast.LENGTH_LONG).show();
					TextView tvTop = (TextView) findViewById(R.id.topText);
					
					  tvTop.setText("happy " +
					  Double.toString(compareProp(face, mHappy)) + "sad " +
					  Double.toString(compareProp(face, mSad)));
					 

					Highgui.imwrite(Environment.getExternalStorageDirectory()
							.getPath() + "/moodplayer/mood2.jpg", face);
					break;
				} else if (System.currentTimeMillis() - startTime >= threshold) {

					break;

				}

			}
		}

	}

	public void timerAlert() {

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				showalert();
				handler.postDelayed(this, 10000);
			}
		}, 5000);

	}

}
