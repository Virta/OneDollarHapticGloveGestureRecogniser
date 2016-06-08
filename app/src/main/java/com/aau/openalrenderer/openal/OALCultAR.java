package com.aau.openalrenderer.openal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aau.openalrenderer.openal.cultar.Beacon;
import com.aau.openalrenderer.openal.cultar.Beacon.TYPE;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Handler;

public class OALCultAR {
	private static final Logger logger = Logger.getLogger(OALCultAR.class.getName());
	private static OALCultAR sInstance;
	private static String mPath = "";
	private static OpenALRenderer mOpenAL;

//	private float mHeadOrientation;

	private float MAX_GAIN = 1f;
	private float MIN_GAIN = 0f;
	private int MAX_DISTANCE = 100;
	private int MIN_DISTANCE = 10;
	
	
	private Buffer mBufferTitle, mBufferDescription, mBufferBeacon, mBufferNotification;
	private HashMap<Beacon.TYPE, Buffer> mCategories = new HashMap<Beacon.TYPE, Buffer>(Beacon.TYPE.values().length);

	private Source mSourceTitle, mSourceDescription, mSourceBeacon, mSourceNotification;
	private HashMap<Beacon.TYPE, Source> mCategorySource = new HashMap<Beacon.TYPE, Source>(Beacon.TYPE.values().length);
	
	
//	private Buffer mTestBuffer;
//	private Source mTestSource;
	
	public static OALCultAR getInstance(Activity activity, String path, boolean useDebug) {
		if (sInstance == null)
			sInstance = new OALCultAR(activity, path, useDebug);

		return sInstance;
	}

	private OALCultAR(Activity activity, String path, boolean useDebug) {
		mOpenAL = OpenALRenderer.getInstance(activity, useDebug);
		mOpenAL.setListenerOrientation(0);
		mOpenAL.setListenerPos(0, 0, 0);
		mPath = path;
		loadCategorySounds();
		loadBeaconSound();
		
	}

	/**
	 * Sets the title to be played as ID passed on.
	 * Use this function if you wish to know duration of title playback before playing it.
	 * Otherwise use playTitle function.
	 * @param id - POI ID
	 * @param locale - Which language to use
	 * @return title playback duration in ms. If no audio file found with related ID returns 0.
	 */
	public synchronized long setTitle(long id, Locale locale){
        String mTitlePath = mPath + "PoiSounds/" + (locale == Locale.ITALIAN ? "it/" : "en/" + id + "/title");
        File file = new File(mTitlePath + ".wav");
		if (!file.exists()) {
			logger.log(Level.SEVERE, "File: " + file.getPath() + " doesn't exist!");
			if (mSourceTitle != null) mSourceTitle.release();
			return 0;
		}

		if (checkIfBufferNeedsUpdating(mBufferTitle, String.valueOf(id))) {
			try {
				mBufferTitle = mOpenAL.addBuffer(String.valueOf(id), file);
			} catch (IOException e) {
				e.printStackTrace();
				mBufferTitle = null;
			}
		}

		if (mSourceTitle != null && !mSourceTitle.isBufferSame(mBufferTitle)) {
			mSourceTitle.stop();
			mSourceTitle.release();
			mSourceTitle = null;
		}

		if (mBufferTitle == null) return 0;

		mSourceTitle = mOpenAL.addSource(mBufferTitle);
		return mSourceTitle.getDuration();
	}

	/**
	 * Plays a title if audio file exists for that ID.
	 * @param id - POI ID
	 * @param locale - Which language to use
	 * @return title playback duration in ms. If no audio file found with related ID returns 0.
	 */
	public long playTitle(long id, Locale locale) {
		setTitle(id, locale);

        if (mSourceTitle == null) return 0;

        return mSourceTitle.play(false);
	}

	/**
	 * Plays a title if audio file exists for that ID after x amount of delay in millisecond.
	 * @param uiHandler - Handler for posting the runnable.
	 * @param id - POI ID
	 * @param locale - Which language to use
	 * @param delay - Time in ms to wait before playing title
	 * @return the duration when title playback is done after the delay.
	 */
	public long playTitleDelayed(Handler uiHandler, long id, Locale locale, long delay){
		long duration = setTitle(id, locale);
        if (mSourceTitle == null) return 0;
		uiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mSourceTitle.getDuration() > 0){
					mSourceTitle.play(false);
				}
			}
		}, delay);
		return duration + delay;
	}

	/**
	 * Stops title playback.
	 */
	public void stopTitle(){
		if(mSourceTitle != null) mSourceTitle.stop();
	}
	
	/**
	 * Sets the description to be played as ID passed on.
	 * Use this function if you wish to know duration of description playback before playing it.
	 * Otherwise use playDescription function.
	 * @param id - POI ID
	 * @param locale - Which language to use
	 * @return description playback duration in ms. If no audio file found with related ID returns 0.
	 */
	public synchronized long setDescription(long id, Locale locale){
        String mTitlePath = mPath + "PoiSounds/" + (locale == Locale.ITALIAN ? "it/" : "en/" + id + "/description");
        File file = new File(mTitlePath + ".wav");
		if (!file.exists()) {
			logger.log(Level.SEVERE, "File: " + file.getPath() + " doesn't exist!");
			if (mSourceDescription != null) mSourceDescription.release();
			return 0;
		}

		if (checkIfBufferNeedsUpdating(mBufferDescription, String.valueOf(id))) {
			try {
				mBufferDescription = mOpenAL.addBuffer(String.valueOf(id), file);
			} catch (IOException e) {
				e.printStackTrace();
				mBufferDescription = null;
			}
		}

		if (mSourceDescription != null && !mSourceDescription.isBufferSame(mBufferDescription)) {
			mSourceDescription.stop();
			mSourceDescription.release();
			mSourceDescription = null;
		}

		if (mBufferDescription == null) return 0;
        mSourceDescription = mOpenAL.addSource(mBufferDescription);
		return mSourceDescription.getDuration();
	}
	
	/**
	 * Plays a description if audio file exists for that ID.
	 * @param id - POI ID
	 * @param locale - Which language to use
	 * @return description playback duration in ms. If no audio file found with related ID returns 0.
	 */
	public synchronized long playDescription(long id, Locale locale) {
		setDescription(id, locale);
        if (mSourceDescription == null) return 0;
		return mSourceDescription.play(false);
	}
	
	/**
	 * Plays a description if audio file exists for that ID.
	 * @param uiHandler - Handler for posting the runnable.
	 * @param id - POI ID
	 * @param locale - Which language to use
	 * @param delay - Time in ms to wait before playing description
	 * @return the duration when description playback is done after the delay.
	 */
	public synchronized long playDescriptionDelayed(Handler uiHandler, long id, Locale locale, long delay){
		long duration = setDescription(id, locale);
        if (mSourceDescription == null) return 0;
		uiHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (mSourceDescription.getDuration() > 0) mSourceDescription.play(false);
			}
		}, delay);
		return duration + delay;
	}
	
	/**
	 * Stops description playback
	 */
	public synchronized void stopDescription(){
		if(mSourceDescription != null) mSourceDescription.stop();
	}

	
	/**
	 * Set which notification to be played. Think of this as regular feedback sounds as we have them in Windows and iOS when clicking stuff or turning volume up and down.
	 * 
	 * @param notificationNumber - which notification to be played. Notifications folder in CultARMedia!
	 * @param volume - Volume between 0-1
	 * @return duration of notification.
	 */
	public synchronized long setNotification(int notificationNumber, float volume){
		String mTitlePath = mPath + "Notifications/" + notificationNumber;
		File file = new File(mTitlePath + ".wav");
		if (!file.exists()) {
			logger.log(Level.SEVERE, "File: " + file.getPath() + " doesn't exist!");
			return 0;
		}

		if (checkIfBufferNeedsUpdating(mBufferNotification, String.valueOf(notificationNumber))) {
			try {
				mBufferNotification = mOpenAL.addBuffer(String.valueOf(notificationNumber), file);
			} catch (IOException e) {
				e.printStackTrace();
				mBufferNotification = null;
			}
		}

		if (mSourceNotification != null && !mSourceNotification.isBufferSame(mBufferNotification)) {
			mSourceNotification.stop();
			mSourceNotification.release();
			mSourceNotification = null;
		}

		if (mBufferNotification == null) return 0;
		mSourceNotification = mOpenAL.addSource(mBufferDescription);

        mSourceNotification.setGain(volume);
		return mSourceNotification.getDuration();
	}
	
	/**
	 * Play notification delayed.
	 * @param uiHandler - Handler for posting the runnable.
	 * @param notificationNumber - which notification to be played. Notifications folder in CultARMedia!
	 * @param volume - Volume between 0-1
	 * @param delay - Time in ms to wait before playing description
	 * @return the duration till playback is finished. This includes delay.
	 */
	public synchronized long playNotificationDelayed(Handler uiHandler, final int notificationNumber, final float volume, long delay){
		long duration = setNotification(notificationNumber, volume);
        if (mSourceNotification == null) return 0;
		uiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				playNotification(notificationNumber, volume);;
			}
		}, delay);
		return duration + delay;
	}

	/**
	 * Play notification sound.
	 * @param notificationNumber - which notification to be played. Notifications folder in CultARMedia!
	 * @param volume - Volume between 0-1
	 * @return duration of notification.
	 */
	public synchronized long playNotification(int notificationNumber, float volume){
		setNotification(notificationNumber, volume);
        if (mSourceNotification == null) return 0;
		return mSourceNotification.play(false);
	}
	
	/**
	 * Play beacon sound.
	 * @param x - x in meters (Use LogicUtils.getHRTFCoordinate)
	 * @param y - y in meters (Use LogicUtils.getHRTFCoordinate)
	 */
	public void playBeacon(float x, float y){
		mSourceBeacon.setPosition(x, 1, y);
		mSourceBeacon.play(false);
	}
	
	/**
	 * Play beacon sound.
	 * @param point - 2-coordinate point in float (Use LogicUtils.getHRTFCoordinate)
	 */
	public void playBeacon(PointF point){
		playBeacon(point.x, point.y);
	}
	
	
	private boolean checkIfBufferNeedsUpdating(Buffer buffer, String name){
		if (buffer == null) return true;
		if (buffer != null && !buffer.getName().equalsIgnoreCase(name)) return true;
		if (buffer != null && buffer.getName().equalsIgnoreCase(name)) return false;
		return true;
	}
	
	/**
	 * Sets the audio engine orientation.
	 * @param heading - Use Headset IMU (z-axis from euler data) to get proper orientation
	 */
	public synchronized void setHeadOrientation(float heading){
//		mHeadOrientation = heading;
		mOpenAL.setListenerOrientation(heading);
	}


	/**
	 * Stop the Audio Icon
	 * @param type - Which POI type (Use LogicUtils.getHRTFBeaconTypeFromPoi)
	 */
	public void stopCategoryBeacon(TYPE type){
		if (mCategorySource.containsKey(type)){
			mCategorySource.get(type).stop();
		}
	}

	/**
	 * Play Category Beacon
	 * @param type - Which POI type (Use LogicUtils.getHRTFBeaconTypeFromPoi)
	 * @param point - Coordinate point - (Use LogicUtils.getHRTFCoordinate)
	 * @return  The length of the playing audio clip
	 */
	public long playCategoryBeacon(TYPE type, PointF point){
		return playCategoryBeacon(type, point.x, point.y);
	}
	
	/**
	 * Play Category Beacon
	 * @param type - Which POI type (Use LogicUtils.getHRTFBeaconTypeFromPoi)
	 * @param x - Coordinate point - (Use LogicUtils.getHRTFCoordinate)
	 * @param y - Coordinate point - (Use LogicUtils.getHRTFCoordinate)
	 * @return  The length of the playing audio clip
	 */
	public long playCategoryBeacon(TYPE type, float x, float y){
		if (mCategorySource.containsKey(type)){
			mCategorySource.get(type).setPosition(x, 0, y);
			return mCategorySource.get(type).play(false);
		}
		return 0;
	}

	/**
	 * Play Category Beacon delayed
	 * @param uiHandler - Handler for posting the runnable.
	 * @param type - Which POI type (Use LogicUtils.getHRTFBeaconTypeFromPoi)
	 * @param point - Coordinate point - (Use LogicUtils.getHRTFCoordinate)
	 * @param delay - Time in ms to wait before playing description
	 */
	public void playCategoryBeaconDelayed(Handler uiHandler, final TYPE type, final PointF point, int delay){
		uiHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				playCategoryBeacon(type, point.x, point.y);
			}
		}, delay);
	}
	
	/**
	 * Play Category Beacon delayed
	 * @param uiHandler - Handler for posting the runnable.
	 * @param type - Which POI type (Use LogicUtils.getHRTFBeaconTypeFromPoi)
	 * @param x - Coordinate point - (Use LogicUtils.getHRTFCoordinate)
	 * @param y - Coordinate point - (Use LogicUtils.getHRTFCoordinate)
	 * @param delay - Time in ms to wait before playing description
	 */
	public void playCategoryBeaconDelayed(Handler uiHandler, final TYPE type, final float x, final float y, int delay){
		uiHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				playCategoryBeacon(type, x, y);
			}
		}, delay);
	}

	private static Buffer createBuffer(String name, String path) throws IOException {
	    return mOpenAL.addBuffer(name, new File(path + name + ".wav"));
	}
	
	private void loadBeaconSound(){
		String mBeaconPath = mPath + "CategoryCues/";
		
		try {
			mBufferBeacon = createBuffer("Beacon#2", mBeaconPath);
		}catch (IOException e) {
			e.printStackTrace();
		}
		mSourceBeacon = mOpenAL.addSource(mBufferBeacon);
		mSourceBeacon.setMaxDistance(MAX_DISTANCE);
		mSourceBeacon.setReferenceDistance(MIN_DISTANCE);
		mSourceBeacon.setMinGain(MIN_GAIN);
		mSourceBeacon.setMaxGain(MAX_GAIN);
		mSourceBeacon.setRolloffFactor(1f);
	}
	
	private void loadCategorySounds(){
		String mCatPath = mPath + "CategoryCues/";
		try {
			mCategories.put(Beacon.TYPE.SCIENCE, createBuffer("Science", mCatPath));
			mCategorySource.put(Beacon.TYPE.SCIENCE, mOpenAL.addSource(mCategories.get(Beacon.TYPE.SCIENCE)));

			mCategories.put(Beacon.TYPE.NIGHTLIFE, createBuffer("Nightlife", mCatPath));
			mCategorySource.put(Beacon.TYPE.NIGHTLIFE, mOpenAL.addSource(mCategories.get(Beacon.TYPE.NIGHTLIFE)));
			
			mCategories.put(Beacon.TYPE.CULTURE, createBuffer("Culture", mCatPath));
			mCategorySource.put(Beacon.TYPE.CULTURE, mOpenAL.addSource(mCategories.get(Beacon.TYPE.CULTURE)));
			
			mCategories.put(Beacon.TYPE.FOOD, createBuffer("Food", mCatPath));
			mCategorySource.put(Beacon.TYPE.FOOD, mOpenAL.addSource(mCategories.get(Beacon.TYPE.FOOD)));

			mCategories.put(Beacon.TYPE.CAFE, createBuffer("Food", mCatPath));
			mCategorySource.put(Beacon.TYPE.CAFE, mOpenAL.addSource(mCategories.get(Beacon.TYPE.FOOD)));

			mCategories.put(Beacon.TYPE.SERVICE, createBuffer("Service", mCatPath));
			mCategorySource.put(Beacon.TYPE.SERVICE, mOpenAL.addSource(mCategories.get(Beacon.TYPE.SERVICE)));
			
			mCategories.put(Beacon.TYPE.SHOP, createBuffer("Shop", mCatPath));
			mCategorySource.put(Beacon.TYPE.SHOP, mOpenAL.addSource(mCategories.get(Beacon.TYPE.SHOP)));
			
			mCategories.put(Beacon.TYPE.HOTEL, createBuffer("Hotel", mCatPath));
			mCategorySource.put(Beacon.TYPE.HOTEL, mOpenAL.addSource(mCategories.get(Beacon.TYPE.HOTEL)));
			
			mCategories.put(Beacon.TYPE.EVENT, createBuffer("Event", mCatPath));
			mCategorySource.put(Beacon.TYPE.EVENT, mOpenAL.addSource(mCategories.get(Beacon.TYPE.EVENT)));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Source s : mCategorySource.values()){
			s.setMaxDistance(MAX_DISTANCE);
			s.setReferenceDistance(MIN_DISTANCE);
			s.setMinGain(MIN_GAIN);
			s.setMaxGain(MAX_GAIN);
			s.setRolloffFactor(1f);
		}
	}

	public void setMinGain(float gain){
		MIN_GAIN = gain;
		mSourceBeacon.setMinGain(MIN_GAIN);
		for (Source s : mCategorySource.values()){
			s.setMinGain(MIN_GAIN);
		}
		
	}

	public float getMinGain(){
		return MIN_GAIN;
	}
	
	public void setMaxGain(float gain){
		MAX_GAIN = gain;
		mSourceBeacon.setMinGain(MAX_GAIN);
		for (Source s : mCategorySource.values()){
			s.setMaxGain(MAX_GAIN);
		}
	}
	
	public float getMaxGain(){
		return MAX_GAIN;
	}
	
	public void setMaxDistance(int distance){
		MAX_DISTANCE = distance;
		mSourceBeacon.setMaxDistance(distance);
		for (Source s : mCategorySource.values()){
			s.setMaxDistance(distance);
		}
	}
	
	public int getMaxDistance(){
		return MAX_DISTANCE;
	}
	
	public void setMinDistance(int distance){
		MIN_DISTANCE = distance;
		mSourceBeacon.setReferenceDistance(distance);
		for (Source s : mCategorySource.values()){
			s.setReferenceDistance(distance);
		}
	}
		
}
