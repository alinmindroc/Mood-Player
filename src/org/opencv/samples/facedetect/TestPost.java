package org.opencv.samples.facedetect;

import org.opencv.samples.facedetect.R;

import ncit.android.moodplayer.AsyncFacebookRunner;
import ncit.android.moodplayer.BaseRequestListener;
import ncit.android.moodplayer.Facebook;
import ncit.android.moodplayer.SessionStore;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class TestPost extends Activity {
	private Facebook mFacebook;
	// private CheckBox mFacebookCb;
	private ProgressDialog mProgress;

	private Handler mRunOnUi = new Handler();

	private static final String APP_ID = "482636805164043";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.post);

		final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
		// mFacebookCb = (CheckBox) findViewById(R.id.cb_facebook);

		mProgress = new ProgressDialog(this);

		mFacebook = new Facebook(APP_ID);

		SessionStore.restore(mFacebook, this);

		if (mFacebook.isSessionValid()) {
			// mFacebookCb.setChecked(true);

			String name = SessionStore.getName(this);
			name = (name.equals("")) ? "Unknown" : name;

			// mFacebookCb.setText("  Facebook  (" + name + ")");
		}

		((Button) findViewById(R.id.button_logout))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						fbLogout();

						// if (mFacebookCb.isChecked()) postToFacebook("done");
					}
				});

		((Button) findViewById(R.id.button_submit))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String review = reviewEdit.getText().toString();
						if (!mFacebook.isSessionValid()) {
							Toast.makeText(TestPost.this, "Please login", Toast.LENGTH_SHORT)
									.show();

						} else {
							// if (review.equals("")) return;
							postToFacebook(review);
						}

						// if (mFacebookCb.isChecked()) postToFacebook("done");
					}
				});
	}

	private void postToFacebook(String review) {
		mProgress.setMessage("Posting ...");
		mProgress.show();
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);
		Bundle params = new Bundle();

		params.putString("message", review);
		params.putString("link", getIntent().getStringExtra("toShare"));
		params.putString("description", "Listening to...");
		mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
	}

	private final class WallPostListener extends BaseRequestListener {
		public void onComplete(final String response) {
			mRunOnUi.post(new Runnable() {
				@Override
				public void run() {
					mProgress.cancel();
					Toast.makeText(TestPost.this, "Posted to Facebook",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void fbLogout() {
		mProgress.setMessage("Disconnecting from Facebook");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				SessionStore.clear(TestPost.this);

				int what = 1;

				try {
					mFacebook.logout(TestPost.this);

					what = 0;
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 1) {
				Toast.makeText(TestPost.this, "Facebook logout failed",
						Toast.LENGTH_SHORT).show();
			} else {

				((Button) findViewById(R.id.button_logout)).setEnabled(false);
				Toast.makeText(TestPost.this, "Disconnected from Facebook",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};

}