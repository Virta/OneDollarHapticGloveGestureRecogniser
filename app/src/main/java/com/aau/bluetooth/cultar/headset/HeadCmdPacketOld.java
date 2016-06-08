package com.aau.bluetooth.cultar.headset;

import com.aau.bluetooth.data.LineCmdPacket;

public class HeadCmdPacketOld extends LineCmdPacket {
    public static final short TRUE = 1;
    public static final short FALSE = 0;

    public static final short CMD_HAT_ID = 0;
    public static final short CMD_SPEAKERS_NUMBER = 1;
    public static final short CMD_AUDIO_ENABLE = 2;
    public static final short CMD_FILEPATH = 3;
    public static final short CMD_AMBIENT = 4;
    public static final short CMD_REVERB = 5;
    public static final short CMD_VOLUME = 6;
    public static final short CMD_VIEWPOINT = 7;
    public static final short CMD_SOUND = 8;
    public static final short CMD_MOVE = 9;
    public static final short CMD_PLAY = 10;
    public static final short CMD_PLAY_FILE = 100;
    public static final short CMD_BEACON = 101;
    public static final short CMD_STOP = 11;
    public static final short CMD_STOP_ALL = 12;
    public static final short CMD_TEST_CHANNEL = 13;
    public static final short CMD_TEST_PAN = 14;
    public static final short CMD_TEST_SOUNDS = 15;
    public static final short CMD_TEST = 16;

    public static final short CMD_SETMAXDISTANCE = 200;
    public static final short CMD_SETMINDISTANCE = 201;
    public static final short CMD_SETDISTANCE = 202;
    public static final short CMD_SETROLLOFF = 203;
    public static final short CMD_SETORIENTATION = 204;
    public static final short CMD_SETVOLUME = 205;
    public static final short CMD_PYTHON_TESTNUMBER = 129;
    public static final int INDEX_CMD = 0;

    private static final int HEAD_CMD_PACKET_MIN_LEN = 16;
    private static final int HEAD_CMD_PACKET_MAX_LEN = 64;


    public HeadCmdPacketOld() {
        super(HEAD_CMD_PACKET_MIN_LEN, HEAD_CMD_PACKET_MAX_LEN);
    }

    public HeadCmdPacketOld setHatId(short id) {
        clear();
        int index = setUByte(CMD_HAT_ID, INDEX_CMD);
        setUByte(id, index);
        return this;
    }

    public HeadCmdPacketOld setAudioEnable(boolean enable) {
        clear();
        int index = setUByte(CMD_AUDIO_ENABLE, INDEX_CMD);
        setUByte(enable ? TRUE : FALSE, index);
        return this;
    }

    public HeadCmdPacketOld setSpeakersNumber(int numSpeakers) {
        clear();
        int index = setUByte(CMD_SPEAKERS_NUMBER, INDEX_CMD);
        setUByte((short)numSpeakers, index);
        return this;
    }

    public HeadCmdPacketOld setFilepath(String filepath) {
        clear();
        int index = setUByte(CMD_FILEPATH, INDEX_CMD);
        int maxLen = HEAD_CMD_PACKET_MAX_LEN - index;
        setString(filepath, maxLen, index);
        return this;
    }

    public HeadCmdPacketOld setAmbient(float volume,
                                    float reverberation,
                                    String filepath)
    {
        clear();
        int index = setUByte(CMD_AMBIENT, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set4Bytes(reverberation, index);
        int maxLen = HEAD_CMD_PACKET_MAX_LEN - index;
        setString(filepath, maxLen, index);
        return this;
    }

    public HeadCmdPacketOld setReverb(int roomSize,
                                   float shininess,
                                   float attenuation,
                                   float simplicity,
                                   float volume)
    {
        clear();
        int index = setUByte(CMD_REVERB, INDEX_CMD);
        index = set2UBytes(roomSize, index);
        index = set4Bytes(shininess, index);
        index = set4Bytes(attenuation, index);
        index = set4Bytes(simplicity, index);
        set4Bytes(volume, index);
        return this;
    }

    public HeadCmdPacketOld setVolume(float volume) {
        clear();
        int index = setUByte(CMD_VOLUME, INDEX_CMD);
        set4Bytes(volume, index);
        return this;
    }

    public HeadCmdPacketOld setViewpoint(float x, float y) {
        clear();
        int index = setUByte(CMD_VIEWPOINT, INDEX_CMD);
        index = set4Bytes(x, index);
        set4Bytes(y, index);
        return this;
    }

    public HeadCmdPacketOld setSound(int id,
                                  float volume,
                                  float directionality,
                                  String filepath)
    {
        clear();
        int index = setUByte(CMD_SOUND, INDEX_CMD);
        index = setUByte((short)id, index);
        index = set4Bytes(volume, index);
        index = set4Bytes(directionality, index);
        int maxLen = HEAD_CMD_PACKET_MAX_LEN - index;
        setString(filepath, maxLen, index);
        return this;
    }

    public HeadCmdPacketOld move(int id, float x, float y) {
        return _move(id, x, y, 0, false);
    }

    public HeadCmdPacketOld move(int id, float x, float y, float orientation) {
        return _move(id, x, y, orientation, true);
    }


    private HeadCmdPacketOld _move(int id,
                                float x,
                                float y,
                                float orientation,
                                boolean useOrientation)
    {
        clear();
        int index = setUByte(CMD_MOVE, INDEX_CMD);
        index = setUByte((short)id, index);
        index = set4Bytes(x, index);
        index = set4Bytes(y, index);
        if (useOrientation) {
            set4Bytes(orientation, index);
        }
        return this;
    }

    public HeadCmdPacketOld play(int id, boolean loop) {
        clear();
        int index = setUByte(CMD_PLAY, INDEX_CMD);
        index = setUByte((short)id, index);
        setUByte(loop ? TRUE : FALSE, index);
        return this;
    }

    public HeadCmdPacketOld playFile(float volume, int directionality, String filepath) {
        clear();
        int index = setUByte(CMD_PLAY_FILE, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set2UBytes((short)directionality, index);
        int maxLen = HEAD_CMD_PACKET_MAX_LEN - index;
        setString(filepath, maxLen, index);
        return this;
    }

    public HeadCmdPacketOld beacon(float volume, int directionality, boolean on) {
        clear();
        int index = setUByte(CMD_BEACON, INDEX_CMD);
        index = set4Bytes(volume, index);
        index = set2UBytes((short)directionality, index);
        setUByte(on ? ONE : ZERO, index);
        return this;
    }

    public HeadCmdPacketOld stop(int id) {
        clear();
        int index = setUByte(CMD_STOP, INDEX_CMD);
        index = setUByte((short)id, index);
        return this;
    }

    public HeadCmdPacketOld stopAll() {
        clear();
        setUByte(CMD_STOP_ALL, INDEX_CMD);
        return this;
    }

    public HeadCmdPacketOld testChannel(int channel) {
        clear();
        int index = setUByte(CMD_TEST_CHANNEL, INDEX_CMD);
        setUByte((short)channel, index);
        return this;
    }

    public HeadCmdPacketOld testPan() {
        clear();
        setUByte(CMD_TEST_PAN, INDEX_CMD);
        return this;
    }

    public HeadCmdPacketOld testSounds() {
        clear();
        setUByte(CMD_TEST_SOUNDS, INDEX_CMD);
        return this;
    }

    public HeadCmdPacketOld test() {
        clear();
        setUByte(CMD_TEST, INDEX_CMD);
        return this;
    }

    public HeadCmdPacketOld setMaxDistance(float meters){
        clear();
        int index = setUByte(CMD_SETMAXDISTANCE, INDEX_CMD);
        index = set4Bytes(meters, index);
        return this;
    }

    public HeadCmdPacketOld setMinDistance(float meters){
        clear();
        int index = setUByte(CMD_SETMINDISTANCE, INDEX_CMD);
        index = set4Bytes(meters, index);
        return this;
    }

    public HeadCmdPacketOld setDistance(float meters){
        clear();
        int index = setUByte(CMD_SETDISTANCE, INDEX_CMD);
        index = set4Bytes(meters, index);
        return this;
    }

    public HeadCmdPacketOld setOrientation(float angle){
        clear();
        int index = setUByte(CMD_SETORIENTATION, INDEX_CMD);
        index = set4Bytes(angle, index);
        return this;
    }
    public HeadCmdPacketOld pythonTestNumber(){
        clear();
        setUByte(CMD_PYTHON_TESTNUMBER, INDEX_CMD);
        return this;
    }
}
