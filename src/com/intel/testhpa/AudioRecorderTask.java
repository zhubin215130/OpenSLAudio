package com.intel.testhpa;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class AudioRecorderTask extends AsyncTask<Integer, Integer, Void> {
	private static final String TAG = "AudioRecorderTask";

	public AudioRecorderTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected void onPostExecute(Void result) {
		Toast.makeText(context, "save file: " + recordFile.getAbsolutePath(),
				Toast.LENGTH_LONG);
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	private Context context;
	private boolean stopRecording = false;
	private File recordFile;

	public void stopRecording() {
		stopRecording = true;
	}

	public File getRecordFile() {
		return recordFile;
	}

	@Override
	protected Void doInBackground(Integer... params) {
		// params[]: 0 is sampleRate, 1 is channelConfig, 2 is encodingFormat
		AudioRecord record = null;
		DataOutputStream dos = null;
		try {
			Log.e(TAG, "start recording! params: " + params);
			recordFile = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/recording_"
					+ System.currentTimeMillis() + ".pcm");
			// recordFile = File.createTempFile("recording", ".pcm", fpath);
			dos = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(recordFile)));

			Log.e(TAG, "record file: " + recordFile.getAbsolutePath());
			int bufferSize = AudioRecord.getMinBufferSize(params[0], params[1],
					params[2]);
			record = new AudioRecord(MediaRecorder.AudioSource.MIC,
					params[0].intValue(), params[1].intValue(),
					params[2].intValue(), bufferSize);
			short[] buffers = new short[bufferSize];

			record.startRecording();
			int r = 0;
			while (!stopRecording) {
				int bufferReadResult = record.read(buffers, 0, buffers.length);

				Log.e(TAG, "recording get result length: " + bufferReadResult);
				for (int i = 0; i < bufferReadResult; i++) {
					dos.writeShort(buffers[i]);
				}
				publishProgress(Integer.valueOf(r));
				r++;
			}
			record.stop();
			record.release();
			record = null;
			dos.close();
			Log.e(TAG, "recording stop!");
		} catch (Exception e) {
			e.printStackTrace();
			if (record != null) {
				record.stop();
				record.release();
				record = null;
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

}
