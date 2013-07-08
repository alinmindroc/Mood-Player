package org.opencv.samples.facedetect;

import java.util.ArrayList;
import java.util.Random;

public class Playlist {

	ArrayList<String> playlist;

	public Playlist(ArrayList<String> list) {
		this.playlist = new ArrayList<String>();
		Random r = new Random();
		while (!list.isEmpty()) {

			this.playlist.add(list.remove(r.nextInt(list.size())));

		}

	}

}
