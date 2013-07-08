package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.widget.Toast;

public class PhotoHandler implements PictureCallback {

  private final Context context;

  public PhotoHandler(Context context) {
    this.context = context;
  }

  @Override
  public void onPictureTaken(byte[] data, Camera camera) {

    String photoFile = "mood.jpg";

    String filename = Environment.getExternalStorageDirectory().getPath() + "/moodplayer/" + photoFile;

    File pictureFile = new File(filename);

    try {
      FileOutputStream fos = new FileOutputStream(pictureFile);
      fos.write(data);
      fos.close();
      Toast.makeText(context, "New Image saved:" + photoFile,
          Toast.LENGTH_LONG).show();
    } catch (Exception error) {
      Toast.makeText(context, "Image could not be saved.",
          Toast.LENGTH_LONG).show();
    }
  }
} 