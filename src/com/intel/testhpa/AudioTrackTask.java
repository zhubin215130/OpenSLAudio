package com.intel.testhpa;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;

public class AudioTrackTask extends AsyncTask<Integer, Integer, Void> {
//	private static final String TAG = "AudioTrackTask";

	public AudioTrackTask(Context context, File audioFile) {
		super();
//		this.context = context;
		this.audioFile = audioFile;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

//	private Context context;
	private File audioFile;
	private boolean stopPlaying = false;

	public void stopPlaying() {
		stopPlaying = true;
	}

	@Override
	protected Void doInBackground(Integer... params) {
		// params[]: 0 is sampleRate, 1 is channelConfig, 2 is encodingFormat
		int bufferSize = AudioTrack.getMinBufferSize(params[0], params[1],
				params[2]);
		short[] buffers = new short[bufferSize / 4];
		AudioTrack track = null;
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(audioFile)));
			track = new AudioTrack(AudioManager.STREAM_MUSIC, params[0],
					params[1], params[2], bufferSize, AudioTrack.MODE_STREAM);
			track.play();
			while (!stopPlaying && dis.available() > 0) {
				int i = 0;
				while (dis.available() > 0 && i < buffers.length) {
					buffers[i] = dis.readShort();
					i++;
				}
				track.write(buffers, 0, buffers.length);
			}
			track.stop();
			track.release();
			track = null;
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (track != null) {
				track.stop();
				track.release();
				track = null;
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

}
