package com.intel.testhpa;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.pm.PackageManager;

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = "MainActivity";

	private AudioRecorderTask recorderTask;
	private AudioTrackTask trackTask;
	private RealtimeAudioTask realtimeAudioTask;
	private OpenSLRealtimeAudioTask openSLRealtimeAudioTask;
	private int outputFramesPerBuffer;
	private int outputSampleRate;
	private boolean isSupportHPA;

	private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
	private int audioEncodingFormat = AudioFormat.ENCODING_PCM_16BIT;

	private Button startRecordingButton;
	private Button stopRecordingButton;
	private Button playRecordButton;
	private Button stopPlayButton;
	private Button realtimePlayButton;
	private Button stopRealtimePlayButton;
	private Button openSLRealtimePlayButton;
	private Button openSLStopRealtimePlayButton;

	private File recordFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);

		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		outputFramesPerBuffer = Integer.parseInt(audioManager
				.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER));
		outputSampleRate = Integer.parseInt(audioManager
				.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));

		PackageManager pm = getPackageManager();
		isSupportHPA = pm
				.hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY);

		Log.i(TAG, "outputFramesPerBuffer:" + outputFramesPerBuffer
				+ ",outputSampleRate:" + outputSampleRate + ",isSupportHPA:"
				+ isSupportHPA);

		startRecordingButton = (Button) findViewById(R.id.start_recording);
		startRecordingButton.setOnClickListener(this);
		stopRecordingButton = (Button) findViewById(R.id.stop_recording);
		stopRecordingButton.setOnClickListener(this);
		stopRecordingButton.setEnabled(false);
		playRecordButton = (Button) findViewById(R.id.play_record_file);
		playRecordButton.setEnabled(false);
		playRecordButton.setOnClickListener(this);
		stopPlayButton = (Button) findViewById(R.id.stop_play_record);
		stopPlayButton.setEnabled(false);
		stopPlayButton.setOnClickListener(this);

		realtimePlayButton = (Button) findViewById(R.id.realtime_play);
		realtimePlayButton.setOnClickListener(this);
		stopRealtimePlayButton = (Button) findViewById(R.id.stop_realtime_play);
		stopRealtimePlayButton.setOnClickListener(this);
		stopRealtimePlayButton.setEnabled(false);
		

		openSLRealtimePlayButton = (Button) findViewById(R.id.openslrealtime_play);
		openSLRealtimePlayButton.setOnClickListener(this);
		openSLRealtimePlayButton.setEnabled(true);
		openSLStopRealtimePlayButton = (Button) findViewById(R.id.stop_openslrealtime_play);
		openSLStopRealtimePlayButton.setOnClickListener(this);
		openSLStopRealtimePlayButton.setEnabled(false);
		
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void startRecording() {
		recorderTask = new AudioRecorderTask(this);
		recorderTask.execute(outputSampleRate, channelConfig,
				audioEncodingFormat);
		startRecordingButton.setEnabled(false);
		stopRecordingButton.setEnabled(true);
	}

	public void finishRecording() {

	}

	private void stopRecording() {
		recorderTask.stopRecording();
		stopRecordingButton.setEnabled(false);
		playRecordButton.setEnabled(true);
		recordFile = recorderTask.getRecordFile();
	}

	private void stopPlayRecord() {
		trackTask.stopPlaying();
		stopPlayButton.setEnabled(false);
		startRecordingButton.setEnabled(true);
	}

	private void playRecordFile() {
		if (recordFile != null) {
			trackTask = new AudioTrackTask(this, recordFile);
			trackTask.execute(outputSampleRate, channelConfig,
					audioEncodingFormat);
			playRecordButton.setEnabled(false);
			stopPlayButton.setEnabled(true);
		}
	}

	private void startRealtimePlay() {
		realtimeAudioTask = new RealtimeAudioTask(outputSampleRate,
				channelConfig, audioEncodingFormat);
		realtimeAudioTask.execute();
		realtimePlayButton.setEnabled(false);
		stopRealtimePlayButton.setEnabled(true);
	}

	private void stopRealtimePlay() {
		realtimeAudioTask.stopRecording();
		realtimePlayButton.setEnabled(true);
		stopRealtimePlayButton.setEnabled(false);
	}

	private void startOpenSLRealtimePlay() {
		openSLRealtimeAudioTask = new OpenSLRealtimeAudioTask(outputSampleRate,
				channelConfig, audioEncodingFormat);
		openSLRealtimeAudioTask.execute();
		openSLRealtimePlayButton.setEnabled(false);
		openSLStopRealtimePlayButton.setEnabled(true);
	}

	private void stopOpenSLRealtimePlay() {
		openSLRealtimeAudioTask.stopRecording();
		openSLRealtimePlayButton.setEnabled(true);
		openSLStopRealtimePlayButton.setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.start_recording:
			startRecording();
			break;
		case R.id.stop_recording:
			stopRecording();
			break;
		case R.id.play_record_file:
			playRecordFile();
			break;
		case R.id.stop_play_record:
			stopPlayRecord();
			break;
		case R.id.realtime_play:
			startRealtimePlay();
			break;
		case R.id.stop_realtime_play:
			stopRealtimePlay();
			break;
		case R.id.openslrealtime_play:
			startOpenSLRealtimePlay();
			break;
		case R.id.stop_openslrealtime_play:
			stopOpenSLRealtimePlay();
			break;
		}
	}

}
