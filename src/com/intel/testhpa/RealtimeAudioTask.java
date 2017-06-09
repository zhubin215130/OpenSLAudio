package com.intel.testhpa;

import java.util.LinkedList;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class RealtimeAudioTask {
	private static final String TAG = "RealtimeAudioTask";

	protected int minInputBufferSize;
	private AudioRecord record;
	private byte[] inputBuffer;
	private LinkedList<byte[]> inputBufferQueue;
	private int minOutputBufferSize;
	private AudioTrack track;
	private byte[] outputBuffer;
	private Thread recordThread;
	private Thread playThread;
	private boolean stopRecording = false;

	public RealtimeAudioTask(int sampleRate, int channelConfig, int encodeFormat) {
		super();

		minInputBufferSize = AudioRecord.getMinBufferSize(sampleRate,
				channelConfig, encodeFormat);
		minOutputBufferSize = AudioTrack.getMinBufferSize(sampleRate,
				channelConfig, encodeFormat);

		record = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
				channelConfig, encodeFormat, minInputBufferSize);
		inputBuffer = new byte[minInputBufferSize];
		inputBufferQueue = new LinkedList<byte[]>();
		track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
				channelConfig, encodeFormat, minOutputBufferSize, AudioTrack.MODE_STREAM);
		outputBuffer = new byte[minOutputBufferSize];
		Log.i(TAG, "minInputBufferSize " + minInputBufferSize+"   minOutputBufferSize "+minOutputBufferSize);

	}

	public void stopRecording() {
		stopRecording = true;
	}

	public void execute() {

		recordThread = new Thread(new recordSound());
		playThread = new Thread(new playRecord());
		playThread.start();
		recordThread.start();

	}

	class recordSound implements Runnable {
		@Override
		public void run() {
			Log.e(TAG, "start recording!  ");
			byte[] bytes_pkg;
			record.startRecording();

			while (!stopRecording) {
				record.read(inputBuffer, 0, minInputBufferSize);
				bytes_pkg = inputBuffer.clone();
				Log.i(TAG, "........recordSound bytes_pkg==" + bytes_pkg.length);
				inputBufferQueue.add(bytes_pkg);
			}
			Log.e(TAG, "stop recording!  ");
			record.stop();
			record.release();
			record = null;
		}

	}

	class playRecord implements Runnable {
		@Override
		public void run() {
			Log.e(TAG, "start playing!  ");
			byte[] bytes_pkg = null;
			track.play();

			while (!stopRecording) {

				try {
					if (inputBufferQueue.size() > 0) {
						outputBuffer = inputBufferQueue.getFirst();
						bytes_pkg = outputBuffer.clone();
						if (inputBufferQueue.size() >= 2) {
							inputBufferQueue.removeFirst();
						}
						Log.i(TAG, "........playRecord bytes_pkg=="
								+ bytes_pkg.length);
						track.write(bytes_pkg, 0, bytes_pkg.length);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Log.e(TAG, "stop playing!  ");
			track.stop();
			track.release();
			track = null;
		}
	}

}
