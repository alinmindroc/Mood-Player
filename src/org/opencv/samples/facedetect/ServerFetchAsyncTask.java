package org.opencv.samples.facedetect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;

public class ServerFetchAsyncTask extends AsyncTask<String, Void, String[]> {

	String str;
	Activity mContext;
	MyCallBack callBack;
	String source1, source2;
	String page2 = "?page=2";
	
	String sourcev[] = new String[2];

	public ServerFetchAsyncTask(String str, Activity context,
			MyCallBack callBack) {
		this.str = str;
		this.mContext = context;
		this.callBack = callBack;
	}

	@Override
	protected String[] doInBackground(String... arg0) {

		// TODO Auto-generated method stub
		HttpGet request1 = new HttpGet(str);
		HttpGet request2 = new HttpGet(str + page2);
		HttpClient client1 = new DefaultHttpClient();
		HttpClient client2 = new DefaultHttpClient();
		try {
			HttpResponse response1 = client1.execute(request1);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response1.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null)
				buffer.append(line);
			this.source1 = buffer.toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			HttpResponse response2 = client2.execute(request2);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response2.getEntity().getContent()));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null)
				buffer.append(line);
			this.source2 = buffer.toString();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sourcev[0] = source1;
		sourcev[1] = source2;
		
		return sourcev;
	}

	protected void onPostExecute(String[] str) {
		callBack.run(str);
		/*
		TextView lala = (TextView) this.mContext.findViewById(R.id.source);
		lala.setMovementMethod(new ScrollingMovementMethod());
		lala.setText(this.str);
		*/
		}

	public static class MyCallBack {
		public void run(String[] s) {
		}
	};

}
