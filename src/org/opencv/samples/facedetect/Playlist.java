package org.opencv.samples.facedetect;

import java.util.ArrayList;
import java.util.Random;

public class Playlist {

	ArrayList<String> playlist;

	public Playlist(String[] list) {
		this.playlist = new ArrayList<String>();
		ArrayList<String> alist = new ArrayList<String>();
		Random r = new Random();
		for ( String s : list){			
			alist.add(s);		
		}
		while (!alist.isEmpty()) {

			this.playlist.add(alist.remove(r.nextInt(alist.size())));

		}

	}

}
