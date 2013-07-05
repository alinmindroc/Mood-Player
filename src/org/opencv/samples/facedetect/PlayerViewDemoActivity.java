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

import java.util.ArrayList;
import java.util.Random;

import org.opencv.core.MatOfRect;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
public class PlayerViewDemoActivity extends YouTubeFailureRecoveryActivity {

	private String htmlSource1, htmlSource2;
	private String id;// youtube video id

	private int beginIndex;

	int progress1, progress2, progress3;
	private static final String KEY_CURRENTLY_SELECTED_ID = "currentlySelectedId";

	private YouTubePlayerView youTubePlayerView;
	private YouTubePlayer player;
	private Button b;
	private String songs[];

	public void setIdArray() {
		b.setEnabled(false);
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			String tag = getTag(progress1, progress2, progress3);
			String URL = "http://www.last.fm/tag/";
			URL = URL + tag + "/videos";

			ServerFetchAsyncTask down1 = new ServerFetchAsyncTask(URL,
					PlayerViewDemoActivity.this, new ServerFetchAsyncTask.MyCallBack() {
						public void run(String[] sv) {
							htmlSource1 = sv[0];
							htmlSource2 = sv[1];

							String separator = new String("<img src=");

							// extract the first id array
							String parts1[] = htmlSource1.split(separator);
							// first and last 3 lines are not interesting
							for (int i = 1; i < parts1.length - 3; i++) {
								parts1[i] = parts1[i].substring(parts1[i].indexOf("vi/"),
										parts1[i].indexOf(".jpg"));
								parts1[i] = parts1[i].substring(3, parts1[i].length() - 2);
							}

							String parts2[] = htmlSource2.split(separator);
							for (int i = 1; i < parts2.length - 3; i++) {
								parts2[i] = parts2[i].substring(parts2[i].indexOf("vi/"),
										parts2[i].indexOf(".jpg"));
								parts2[i] = parts2[i].substring(3, parts2[i].length() - 2);
							}

							// final id array
							songs = new String[parts1.length + parts2.length - 8];
							int k = 0;

							for (int i = 1; i < parts1.length - 3; i++)
								songs[k++] = parts1[i];
							for (int i = 1; i < parts2.length - 3; i++)
								songs[k++] = parts2[i];
							// we use it in playvideoatselection() to get random id
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
					Toast.makeText(PlayerViewDemoActivity.this, "Please turn wi-fi on",
							Toast.LENGTH_SHORT).show();
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

}
