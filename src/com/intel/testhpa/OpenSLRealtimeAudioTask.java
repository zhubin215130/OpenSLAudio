package com.intel.testhpa;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

public class OpenSLRealtimeAudioTask {
	private static final String TAG = "OpenSLRealtimeAudioTask";

	private int minInputBufferSize;
	private int minOutputBufferSize;

	public OpenSLRealtimeAudioTask(int sampleRate, int channelConfig,
			int encodeFormat) {
		super();

		Log.d(TAG, "OpenSLRealtimeAudioTask constructor");
		minInputBufferSize = AudioRecord.getMinBufferSize(sampleRate,
				channelConfig, encodeFormat);
		minOutputBufferSize = AudioTrack.getMinBufferSize(sampleRate,
				channelConfig, encodeFormat);

		// initialize native audio system
		createEngine();
	}

	public void stopRecording() {
		Log.d(TAG, "OpenSLRealtimeAudioTask stopRecording");
		shutdown();
	}

	public void execute() {
		Log.d(TAG, "OpenSLRealtimeAudioTask execute");
		createBufferQueueAudioPlayer();
		createAudioRecorder();
		startRecording();

	}

	static {
		System.loadLibrary("testhpa");
	}

	public static native void createEngine();

	public static native void createBufferQueueAudioPlayer();

	public static native boolean createAudioRecorder();

	public static native void startRecording();

	public static native void shutdown();
}
