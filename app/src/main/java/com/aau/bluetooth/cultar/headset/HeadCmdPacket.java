package com.aau.bluetooth.cultar.headset;

import java.util.Locale;

import android.util.Log;

import com.aau.bluetooth.data.LineCmdPacket;

public class HeadCmdPacket extends LineCmdPacket {
    public static final short TRUE = 1;
    public static final short FALSE = 0;

    public static enum STOP_TYPE {
    	ALL, BEACON, TITLE, DESCRIPTION, STORY
    }
    
    public static enum BEACON_TYPE {
    	SCIENCE, NIGHTLIFE, CULTURE, FOOD, SERVICE, SHOP, HOTEL, EVENT, DEFAULT, 
    }
    @Deprecated public static final short CMD_AUDIO_ENABLE = 0;
    @Deprecated public static final short CMD_PLAY = 1;
    public static final short CMD_STOP = 2;
    public static final short CMD_SETVOLUME = 3;
    public static final short CMD_SETORIENTATION = 4;

    @Deprecated public static final short CMD_SETDISTANCE = 5;
    @Deprecated public static final short CMD_SETMAXDISTANCE = 6;
    @Deprecated public static final short CMD_SETMINDISTANCE = 7;
    @Deprecated public static final short CMD_SETROLLOFF = 8;

    @Deprecated public static final short CMD_BEACON = 101;
    public static final short CMD_CATEGORYBEACON = 102;
    public static final short CMD_PLAYTITLE = 103;
    public static final short CMD_PLAYDESCRIPTION = 104;
    public static final short CMD_PLAYSTORY = 105;
    public static final short CMD_PLAYNOTIFICATION = 106;
    
    public static final short CMD_PLAYTEMP = 79;

    public static final int INDEX_CMD = 0;

    private static final int HEAD_CMD_PACKET_MIN_LEN = 16;
    private static final int HEAD_CMD_PACKET_MAX_LEN = 64;


    public HeadCmdPacket() {
        super(HEAD_CMD_PACKET_MIN_LEN, HEAD_CMD_PACKET_MAX_LEN);
    }

    /**
     * @deprecated Not used 
     */
    @Deprecated public HeadCmdPacket setAudioEnable(boolean enable) {
        clear();
        int index = setUByte(CMD_AUDIO_ENABLE, INDEX_CMD);
        index = set4Bytes(0, index);
        index = set4Bytes(0, index);
        index = set2UBytes(enable ? TRUE : FALSE, index);
        set2UBytes(0, index);
        return this;
    }

    /**
     * Set Master volume for headset. This is currently multiplied to beacon and stereo volume.
     * @param volume - (0.0 - 1.0)
     * @return
     */
    public HeadCmdPacket setMasterVolume(float volume) {
        clear();
        int index = setUByte(CMD_SETVOLUME, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set4Bytes(0, index);
        index = set2UBytes(0, index);
        set2UBytes(0, index);
        return this;
    }

    /**
     * @deprecated Use playTitle, playDescription, beacon or categoryBeacon instead. 
     */
    @Deprecated public HeadCmdPacket play(int id, boolean loop) {
        clear();
        int index = setUByte(CMD_PLAY, INDEX_CMD);
        index = set4Bytes(0, index);
        index = set4Bytes(0, index);
        index = set2UBytes(id, index);
        set2UBytes(loop ? TRUE : FALSE, index);
        return this;
    }
    
    /**
     * Play POI title
     * @param id - POI id
     * @param volume - Volume (0-1)
     * @param language - Use Locale.ENGLISH or Locale.ITALIAN or Locale.ITALY
     * @return
     */
    public HeadCmdPacket playTitle(int id, float volume, Locale language) {
        clear();
        int index = setUByte(CMD_PLAYTITLE, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set4Bytes((float)id, index);
        index = set2UBytes(0, index);
        if (language == Locale.ITALIAN || language == Locale.ITALY){
            set2UBytes(1, index);
        } else {
        	set2UBytes(0, index);
        }
        return this;
    }

    /**
     * Play POI description
     * @param id - POI id
     * @param volume - Volume (0-1)
     * @param language - Use Locale.ENGLISH or Locale.ITALIAN or Locale.ITALY
     * @return
     */
    public HeadCmdPacket playDescription(int id, float volume, Locale language) {
        clear();
        int index = setUByte(CMD_PLAYDESCRIPTION, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set4Bytes(id, index);
        index = set2UBytes(0, index);
        if (language == Locale.ITALIAN || language == Locale.ITALY){
            set2UBytes(1, index);
        } else {
        	set2UBytes(0, index);
        }
        return this;
    }

    /**
     * Play notification sound
     * @param id - Notification ID (3 and 4 seems to work best)
     * @param volume
     * @return
     */
    public HeadCmdPacket playNotification(int id, float volume) {
        clear();
        int index = setUByte(CMD_PLAYNOTIFICATION, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set4Bytes((float)id, index);
        index = set2UBytes(0, index);
        index =	set2UBytes(0, index);
        return this;
    }
    
    
    /**
     * Play general beacon sound.
     * @param volume - (0.0 - 1.0)
     * @param directionality - Direction of sound
     * @param on - Bob?
     * @return
     */
    public HeadCmdPacket beacon(float volume, int directionality, boolean on) {
//        clear();
//        int index = setUByte(CMD_BEACON, INDEX_CMD);
//        index = set4Bytes(volume, index);
//        index = set4Bytes(0, index);
//        index = set2UBytes((short)directionality, index);
//        set2UBytes(on ? ONE : ZERO, index);
//        return this;
    	return categoryBeacon(BEACON_TYPE.DEFAULT, volume, directionality, on);
    }

    public HeadCmdPacket categoryBeacon(BEACON_TYPE categoryType, float volume, int directionality, boolean on) {
        clear();
        int index = setUByte(CMD_CATEGORYBEACON, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set4Bytes((float)categoryType.ordinal(), index);
        index = set2Bytes((short)directionality, index);
        set2UBytes(on ? ONE : ZERO, index);
        return this;
    }

    
    public HeadCmdPacket stop(STOP_TYPE type) {
        clear();
        int index = setUByte(CMD_STOP, INDEX_CMD);
        index = set4Bytes(0, index);
        index = set4Bytes(0, index);
        index = set2UBytes(type.ordinal(), index);
        set2UBytes(0, index);
        return this;
    }

    /**
     * @deprecated Not used anymore.
     * Calculate in Android instead. 
     */
    @Deprecated public HeadCmdPacket setMaxDistance(float meters){
        clear();
        int index = setUByte(CMD_SETMAXDISTANCE, INDEX_CMD);
        index = set4Bytes(meters, index);
        index = set4Bytes(0, index);
        index = set2UBytes(0, index);
        set2UBytes(0, index);
        return this;
    }

    /**
     * @deprecated Not used anymore. 
     * Calculate in Android instead. 
     */
    @Deprecated public HeadCmdPacket setMinDistance(float meters){
        clear();
        int index = setUByte(CMD_SETMINDISTANCE, INDEX_CMD);
        index = set4Bytes(meters, index);
        index = set4Bytes(0, index);
        index = set2UBytes(0, index);
        set2UBytes(0, index);
        return this;
    }

    /**
     * @deprecated Not used anymore. 
     * Calculate in Android instead. 
     */
    @Deprecated public HeadCmdPacket setDistance(float meters){
        clear();
        int index = setUByte(CMD_SETDISTANCE, INDEX_CMD);
        index = set4Bytes(meters, index);
        index = set4Bytes(0, index);
        index = set2UBytes(0, index);
        set2UBytes(0, index);
        return this;
    }

    public HeadCmdPacket setOrientation(float angle){
        clear();
        int index = setUByte(CMD_SETORIENTATION, INDEX_CMD);
        index = set4Bytes(angle, index);
        index = set4Bytes(0, index);
        index = set2UBytes(0, index);
        set2UBytes(0, index);
        return this;
    }

    public HeadCmdPacket playTemp(){
        clear();
        int index = setUByte(CMD_PLAYTEMP, INDEX_CMD);
        index = set4Bytes(0, index);
        index = set4Bytes(0, index);
        index = set2UBytes(0, index);
        set2UBytes(0, index);
        return this;
    }
    
}
