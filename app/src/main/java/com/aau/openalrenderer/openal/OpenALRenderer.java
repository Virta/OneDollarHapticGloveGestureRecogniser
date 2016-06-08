package com.aau.openalrenderer.openal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;

class OpenALRenderer {
	private static final Logger logger = Logger.getLogger(OpenALRenderer.class.getName());

	private Activity mActivity;

	private List<Buffer> buffers;
	private List<Source> sources;

	private boolean mReleased;

	public static final int SUCCESS = 1;
	public static final int ERROR = 0;

	private static OpenALRenderer sInstance;

	public static OpenALRenderer getInstance(Activity activity, boolean useDebug) {
		if (sInstance == null)
			sInstance = new OpenALRenderer(activity, useDebug);

		return sInstance;
	}
	private OpenALRenderer(Activity activity, boolean useDebug) {
		this.mActivity = activity;
		System.loadLibrary("openal-mob");
		System.loadLibrary("OpenALRenderer");
		this.buffers = new ArrayList<Buffer>();
		this.sources = new ArrayList<Source>();
		nInit(useDebug ? 1 : 0);
	};

	/**
	 * Creates a new buffer with a sound file stored in the assets folder of the
	 * Android project and adds it to the internal list of active buffers.
	 * 
	 * @param name
	 *            Name of the sound file without file extension = "lake" instead
	 *            of "lake.wav". This will also become the name of the buffer.
	 * @throws IOException
	 *             if the sound file cannot be found
	 */
	public Buffer addBuffer(String name) throws IOException {
		Buffer buffer = Buffer.createFrom(mActivity, name);
		// logger.log(Level.WARNING, "addBuffer( " + buffer + " )");
		this.buffers.add(buffer);
		return buffer;
	}

	/**
	 * Creates a new buffer by specifying the file path of the .wav file and
	 * adds it to the internal list of active buffers.
	 * 
	 * @param name
	 *            Name of the buffer that can be used to retrieve the buffer via
	 *            {@link SoundEnv#findBufferByName(String)}.
	 * @param path
	 *            Path of the file containing the .wav sound
	 * @throws IOException
	 *             if the sound file cannot be found
	 */
	public Buffer addBuffer(String name, String path) throws IOException {
		Buffer buffer = Buffer.createFrom(name, path);
		// logger.log(Level.WARNING, "addBuffer( " + buffer + " )");
		this.buffers.add(buffer);
		return buffer;
	}

    /**
     * Creates a new buffer by specifying the file path of the .wav file and
     * adds it to the internal list of active buffers.
     * 
     * @param name
     *            Name of the buffer that can be used to retrieve the buffer via
     *            {@link SoundEnv#findBufferByName(String)}.
     * @param file
     *            File object referring to the .wav sound
     * @throws IOException
     *             if the sound file cannot be found
     */
    public Buffer addBuffer(String name, File file) throws IOException {
        Buffer buffer = Buffer.createFrom(name, file);
        // logger.log(Level.WARNING, "addBuffer( " + buffer + " )");
        this.buffers.add(buffer);
        return buffer;
    }

	/**
	 * Allows retrieving a buffer by the name specified on its creation
	 * 
	 * @param name
	 *            name of the buffer
	 * @return the buffer of NULL if no buffer with the given name was found
	 */
	public Buffer findBufferByName(String name) {
		for (Buffer buffer : buffers) {
			if (name.equals(buffer.getName())) {
				return buffer;
			}
		}
		return null;
	}

	/**
	 * Creates a new sound source for the given buffer and adds it to the
	 * internal list of sources
	 * 
	 * @param buffer
	 *            the buffer
	 */
	public Source addSource(Buffer buffer) {
		Source source = new Source(buffer);
		// logger.log(Level.WARNING, "addSource( " + source + " )");
		this.sources.add(source);
		return source;
	}

	/**
	 * Moves the listener to the given coordinates.
	 */
	public void setListenerPos(float x, float y, float z) {
		nSetListenerPos(x, y, z);
	}

	/**
	 * Rotates the listener to face into the given direction. For simplification
	 * this method assumes that the listener always stands upright, like a
	 * person when walking. In fact, OpenAL also allows rotating the listener
	 * around any of the three possible axis.
	 * 
	 * @param heading
	 *            the direction the listener should face
	 */
	public void setListenerOrientation(double heading) {
		double zv = -Math.cos(Math.toRadians(heading));
		double xv = Math.sin(Math.toRadians(heading));
		this.setListenerOrientation((float) xv, 0, (float) zv);
	}

	/**
	 * Rotates the listener to face into the given direction.
	 */
	public void setListenerOrientation(float xv, float yv, float zv) {
		nSetListenerOrientation(xv, yv, zv);
	}

	/**
	 * Plays all registered sources (useful for debugging).
	 * 
	 * @param loop
	 *            'true' = endlessly repeated, 'false' = played once only
	 */
	public void playAllSources(boolean loop) {
		for (Source source : sources)
			source.play(loop);
	}

	/**
	 * Stops the playback of all registered sources.
	 */
	public void stopAllSources() {
		for (Source source : sources)
			source.stop();
	}

	/**
	 * Releases all sources and buffers and closes the OpenAL device.
	 */
	public synchronized void release() {
		if (!mReleased) {
			// logger.log(Level.INFO, "release()");

			for (Source source : sources)
				source.stop();

			for (Source source : sources)
				source.release();

			for (Buffer buffer : buffers) {
				buffer.release();
			}

			nClose();

			mReleased = true;
		}
	}

	private boolean memoryLow;

	public void onLowMemory() {
		logger.log(Level.WARNING, "memory is low, stopping to add buffers");
		this.memoryLow = true;
	}

	void testMaxBuffers() {
		try {
			logger.log(Level.INFO, "testMaxBuffers()");

			int i = 0;
			do {
				i++;
				Buffer b = addBuffer("lake");
				logger.log(Level.FINE, "\tbuffer" + i + " = " + b);
				Thread.sleep(10);
			} while (!memoryLow);
			logger.log(Level.INFO, "allocated " + i + " buffers");

		} catch (Exception e) {
			logger.log(Level.SEVERE, e + " in testEnv()", e);
		}
	}

	/** Native methods, implemented in jni folder */

	static native int nInit(int useDebug);
	static native int nClose();
	static native int nAddBuffer(String filename);
	static native int nReleaseBuffer(int bufferId);
	static native int nAddSource(int bufferId);
	static native int nGetSourceDuration(int sourceId);
	static native int nReleaseSource(int sourceId);
	static native void nSetPosition(int sourceId, float x, float y, float z);
	static native void nSetMaxDistance(int sourceId, float distance);
	static native void nSetReferenceDistance(int sourceId, float distance);
	static native void nSetMinGain(int sourceId, float gain);
	static native void nSetMaxGain(int sourceId, float gain);
	static native void nSetPitch(int sourceId, float pitch);
	static native void nSetGain(int sourceId, float gain);
	static native void nSetRolloffFactor(int sourceId, float rollOff);
	static native int nPlay(int sourceId, boolean loop);
	static native int nStop(int sourceId);
	static native int nSetListenerPos(float x, float y, float z);
	static native int nSetListenerOrientation(float xAt, float yAt, float zAt);

	public static String str(int retVal) {
		if (retVal == SUCCESS)
			return "SUCCESS";
		else if (retVal == ERROR)
			return "ERROR";

		return "UNKNOWN";
	}

	public static String getWavPath(Activity activity, String name) throws IOException {

		String filename = name + ".wav";
		File file = new File(activity.getFilesDir(), filename);
		if (!file.exists()) {
			logger.log(Level.WARNING, file + " not found, copying from assets");
			retrieveFromAssets(activity, filename);
		} else {}

		return file.getAbsolutePath();
	}

	private static void retrieveFromAssets(Activity activity, String filename) throws IOException {

		InputStream is = activity.getAssets().open(filename);

		// Destination
		File outFile = new File(activity.getFilesDir(), filename);

		logger.log(Level.INFO, "retrieveFromAssets( .. ) copying " + filename + " to " + outFile.getParent());

		FileOutputStream fos = new FileOutputStream(outFile);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			fos.write(buffer, 0, length);
		}

		// Close the streams
		fos.flush();
		fos.close();
		is.close();

	}

}
