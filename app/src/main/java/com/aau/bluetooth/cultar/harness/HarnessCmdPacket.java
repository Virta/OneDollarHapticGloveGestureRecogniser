package com.aau.bluetooth.cultar.harness;

import com.aau.bluetooth.data.LineCmdPacket;


public class HarnessCmdPacket extends LineCmdPacket {
    public static final short TRUE = 1;
    public static final short FALSE = 0;

    public static final short CMD_START_LOG = 0;
    public static final short CMD_STOP_LOG = 1;
    public static final short CMD_SAVE_LOG = 2;
    public static final short CMD_HAPTICS_ON = 3;
    public static final short CMD_HAPTICS_OFF = 4;
    public static final short CMD_POSITION = 5;
    public static final short CMD_CALIBRATE_IMU = 6;
    public static final short CMD_CALIBRATE_MOTOR = 7;
    public static final short CMD_CALIBRATE_ALL_MOTORS = 8;
    public static final short CMD_SET_PATTERN = 9;
    public static final short CMD_MOVE_PATTERN = 10;
    public static final short CMD_PLAY_PATTERN = 11;
    public static final short CMD_STOP_PATTERN = 12;
    public static final short CMD_REMOVE_PATTERN_QUEUE = 13;
    public static final short CMD_SET_HAPTIC = 14;
    public static final short CMD_MOVE_HAPTIC = 15;
    public static final short CMD_PLAY_HAPTIC = 16;
    public static final short CMD_STOP_HAPTIC = 17;
    public static final short CMD_REMOVE_HAPTIC_QUEUE = 18;
    public static final short CMD_PLAY_SINGLE = 19;
    public static final short CMD_STOP_SINGLE = 20;
    public static final short CMD_STOP_ALL = 21;
    public static final short CMD_START_ALL = 28;
    public static final short CMD_TEST_MOTORS = 22;
    public static final short CMD_CANCEL_TEST_MOTORS = 23;
    public static final short CMD_SYSTEM_STATUS = 24;
    public static final short CMD_SYNC_TIME = 26;
    public static final short CMD_SET_PATTERN_TIMES = 27;
    public static final short CMD_SET_PATTERN_DESIGN = 250;
	public static final short CMD_SET_PATTERN_DESIGN_NEW = 251;
	
    public static final int INDEX_CMD = 0;
    public static final int INDEX_LAT = 1;
    public static final int INDEX_LON = 5;
    public static final int INDEX_ARG1 = 9;
    public static final int INDEX_ARG2 = 10;
    public static final int INDEX_ARG3 = 11;
    public static final int INDEX_TEXT = 12;

    public static final int HARNESS_CMD_PACKET_MIN_LEN = 12;
    public static final int HARNESS_CMD_PACKET_MAX_LEN = 214;
    public static final int HARNESS_CMD_PACKET_TEXT_MAX_LEN = HARNESS_CMD_PACKET_MAX_LEN - INDEX_TEXT - 2;


    public HarnessCmdPacket() {
        super(HARNESS_CMD_PACKET_MIN_LEN, HARNESS_CMD_PACKET_MAX_LEN);
    }

    /**
     * Enable SD Logging on the Harness device.
     */
    public HarnessCmdPacket startSDLog() {
        clear();
        setUByte(CMD_START_LOG, INDEX_CMD);
        return this;
    }

    /**
     * Disable SD Logging on the Harness device.
     */
    public HarnessCmdPacket stopAndSaveSDLog() {
        clear();
        setUByte(CMD_STOP_LOG, INDEX_CMD);
        return this;
    }

    /**
     * Save the current log. If SD logging is enabled when using this function a new file will be created after saving the log.<br/>
     * This should be used between participants.
     */
    public HarnessCmdPacket saveSDEvent(String text) {
        clear();
        setUByte(CMD_SAVE_LOG, INDEX_CMD);
        setText(text);
        return this;
    }

    /**
     * Turns all haptics processing on.
     */
    public HarnessCmdPacket enableHaptics() {
        clear();
        setUByte(CMD_HAPTICS_ON, INDEX_CMD);
        return this;
    }

    /**
     * Turns all haptics processing off.
     */
    public HarnessCmdPacket disableHaptics() {
        clear();
        setUByte(CMD_HAPTICS_OFF, INDEX_CMD);
        return this;
    }

    /**
     * 
     * Update the central reference point position (the latitude and longitude position of the participant).<br/>
     * <br/>
     * Note: Orientation of the participant will be determined locally (with an IMU connected directly to the Arduino)<br/>
     * <br/>
     * Position is absolute GPS coordinates. All calculations require Decimal Degrees (same thing Google uses), which gives us about 1 meter accuracy
     * with four digits after the decimal point. It supports up to 8 decimals which gives the possibility of mm precision.<br/>
     * 
     * @param latitude
     *            the decimal degree latitude position of the user
     * @param longitude
     *            the decimal degree longitude position of the user
     */
    public HarnessCmdPacket setUserPosition(float latitude, float longitude) {
        clear();
        setUByte(CMD_POSITION, INDEX_CMD);
        set4Bytes(latitude, INDEX_LAT);
        set4Bytes(longitude, INDEX_LON);
        return this;
    }

    /**
     * To make sure the IMU is operating properly it needs to be calibrated.<br/>
     * <br/>
     * This initializes the calibration routine.<br/>
     */
    public HarnessCmdPacket calibrateIMU() {
        clear();
        setUByte(CMD_CALIBRATE_IMU, INDEX_CMD);
        return this;
    }

    /**
     * Calibrate the intensity of the individual haptic actuator.<br/>
     * <br/>
     * This should be done before from the Admin Panel and not the CultAR app.<br/>
     *
     * @param motorID
     *            is the number of the haptic actuator
     * @param intensity
     *            is a number between [0-2]
     */
    public HarnessCmdPacket calibrateMotor(int motorID, float intensity) {
        clear();
        setUByte(CMD_CALIBRATE_MOTOR, INDEX_CMD);
        set4Bytes(intensity, INDEX_LAT);
        setUByte((short)motorID, INDEX_ARG1);
        return this;
    }

    /**
     * Calibrate the overall motor intensity. This can be used to influence the strength of the haptic feedback if the user is in a noisy / stressful
     * environment.
     *
     * @param intensity
     *            sets the overall intensity. Range [0-2]
     */
    public HarnessCmdPacket calibrateOverallMotorIntensity(float intensity) {
        clear();
        setUByte(CMD_CALIBRATE_ALL_MOTORS, INDEX_CMD);
        set4Bytes(intensity, INDEX_LAT);
        return this;
    }

    /**
     *
     * @param queueID
     * @param patternID
     * @param latitude
     * @param longitude
     */
    public HarnessCmdPacket setPattern(int queueID, int patternID, float latitude, float longitude) {
        clear();
        setUByte(CMD_SET_PATTERN, INDEX_CMD);
        set4Bytes(latitude, INDEX_LAT);
        set4Bytes(longitude, INDEX_LON);
        setUByte((short)queueID, INDEX_ARG1);
        setUByte((short)patternID, INDEX_ARG2);
        return this;
    }

    /**
     *
     * @param queueID
     * @param latitude
     * @param longitude
     */
    public HarnessCmdPacket movePattern(int queueID, float latitude, float longitude) {
        clear();
        setUByte(CMD_MOVE_PATTERN, INDEX_CMD);
        set4Bytes(latitude, INDEX_LAT);
        set4Bytes(longitude, INDEX_LON);
        setUByte((short)queueID, INDEX_ARG1);
        return this;
    }

    /**
     * 
     * @param patternID
     *            0 = Accelerate 1 = Backstroke 2 = Stop 3 = North 4 = NorthEast 5 = NorthWest 6 = South 7 = SouthEast 8 = SouthWest
     * @param intensity
     */
    public HarnessCmdPacket playPattern(int patternID, float intensity, int repeatCount) {
        clear();
        setUByte(CMD_PLAY_PATTERN, INDEX_CMD);
        set4Bytes(intensity, INDEX_LAT);
        setUByte((short)patternID, INDEX_ARG1);
        setUByte((short) repeatCount, INDEX_ARG2);
        return this;
    }

    public HarnessCmdPacket stopPattern(int queueID) {
        clear();
        setUByte(CMD_STOP_PATTERN, INDEX_CMD);
        setUByte((short)queueID, INDEX_ARG1);
        return this;
    }

    public HarnessCmdPacket removeFromPatternQueue(int queueID) {
        clear();
        setUByte(CMD_REMOVE_PATTERN_QUEUE, INDEX_CMD);
        setUByte((short)queueID, INDEX_ARG1);
        return this;
    }

    public HarnessCmdPacket setHaptic(int hQueueID, int motorID, float latitude, float longitude) {
        clear();
        setUByte(CMD_SET_HAPTIC, INDEX_CMD);
        set4Bytes(latitude, INDEX_LAT);
        set4Bytes(longitude, INDEX_LON);
        setUByte((short)hQueueID, INDEX_ARG1);
        setUByte((short)motorID, INDEX_ARG2);
        return this;
    }

    public HarnessCmdPacket moveHaptic(int hQueueID, float latitude, float longitude) {
        clear();
        setUByte(CMD_MOVE_HAPTIC, INDEX_CMD);
        set4Bytes(latitude, INDEX_LAT);
        set4Bytes(longitude, INDEX_LON);
        setUByte((short)hQueueID, INDEX_ARG1);
        return this;
    }

    public HarnessCmdPacket playHaptic(int hQueueID) {
        clear();
        setUByte(CMD_PLAY_HAPTIC, INDEX_CMD);
        setUByte((short)hQueueID, INDEX_ARG1);
        return this;
    }

    public HarnessCmdPacket stopHaptic(int hQueueID) {
        clear();
        setUByte(CMD_STOP_HAPTIC, INDEX_CMD);
        setUByte((short)hQueueID, INDEX_ARG1);
        return this;
    }

    public HarnessCmdPacket removeFromHapticQueue(int hQueueID) {
        clear();
        setUByte(CMD_REMOVE_HAPTIC_QUEUE, INDEX_CMD);
        setUByte((short)hQueueID, INDEX_ARG1);
        return this;
    }

    /**
     * Start playing a haptic object.
     * 
     * @param motorID
     *            motorID is the haptic object ID.
     * @param intensity
     *            intensity is the level (0-1) at which the haptic stimulus should be rendered when the play command (below) is called.
     * @param decaytime
     *            if this is not specified, the vibrations will remain at the specified intensity forever (or until another command is received to
     *            turn them off). If specified, the vibration intensity will gradually decay to zero over the period specifed by decaytime (in
     *            milliseconds).
     * @param loop
     *            sets whether the sound / haptic source should play as a one-shot (0), or should loop continuously (1). Looping only affects haptic
     *            objects if the decaytime parameter (above) has been set.
     */
    public HarnessCmdPacket playSingle(int motorID, float intensity, int decaytime, boolean loop) {
        clear();
        setUByte(CMD_PLAY_SINGLE, INDEX_CMD);
        set4Bytes(intensity, INDEX_LAT);
        setUByte((short)motorID, INDEX_ARG1);
        setUByte((short)decaytime, INDEX_ARG2);
        setUByte(loop ? TRUE : FALSE, INDEX_ARG3);
        return this;
    }

    /**
     * Stop actuator from vibrating.
     *
     * @param motorID
     *            is the ID of the actuator
     */
    public HarnessCmdPacket stopSingle(int motorID) {
        clear();
        setUByte(CMD_STOP_SINGLE, INDEX_CMD);
        setUByte((short)motorID, INDEX_ARG1);
        return this;
    }

    /**
     * Stop all the actuators from vibrating
     */
    public HarnessCmdPacket stopAllMotors() {
        clear();
        setUByte(CMD_STOP_ALL, INDEX_CMD);
        return this;
    }

    /**
     * Start all the actuators vibrating
     */
    public HarnessCmdPacket startAllMotors() {
        clear();
        setUByte(CMD_START_ALL, INDEX_CMD);
        return this;
    }

    /**
     * Vibrates all motors in succession
     */
    public HarnessCmdPacket testMotors() {
        clear();
        setUByte(CMD_TEST_MOTORS, INDEX_CMD);
        return this;
    }

    /**
     * Cancels the motor test
     */
    public HarnessCmdPacket cancelTestMotors() {
        clear();
        setUByte(CMD_CANCEL_TEST_MOTORS, INDEX_CMD);
        return this;
    }

    /**
     * Returns the current state of the system.
     */
    public HarnessCmdPacket systemStatus() {
        clear();
        setUByte(CMD_SYSTEM_STATUS, INDEX_CMD);
        return this;
    }

    public HarnessCmdPacket syncTimeDate() {
        clear();
        long epochTime = System.currentTimeMillis() / 1000;
        setUByte(CMD_SYNC_TIME, INDEX_CMD);
        set4UBytes(epochTime, INDEX_LAT);
        return this;
    }

    /**
     *
     * @param which
     *            0 = Accelerate 1 = Back 2 = Navi 3 = Stop
     * @param amplitude
     * @param duration
     * @param overlap
     */

    public HarnessCmdPacket setPatternTimes(int which, int amplitude, int duration, int overlap) {
        clear();
        setUByte(CMD_SET_PATTERN_TIMES, INDEX_CMD);
        set4UBytes(duration, INDEX_LAT);
        set4UBytes(overlap, INDEX_LON);
        set2UBytes(amplitude, INDEX_ARG1);
        setUByte((short)which, INDEX_ARG3);

//        int index = setUByte(CMD_SET_PATTERN_TIMES, INDEX_CMD);
//        index = set4UBytes(amplitude, index);
//        index = set4UBytes(duration, index);
//        index = set4UBytes(overlap, index);
        return this;
    }

    public HarnessCmdPacket setPatternDesign(int amplitude, int duration, int overlap, byte[] motorData, int repeatCount) {
        clear();
        setUByte(CMD_SET_PATTERN_DESIGN, INDEX_CMD);
        set4UBytes(duration, INDEX_LAT);
        set4UBytes(overlap, INDEX_LON);
        set2UBytes(amplitude, INDEX_ARG1);
        setUByte((short)repeatCount, INDEX_ARG3);
        setExtraData(motorData);
        return this;
    }

	public HarnessCmdPacket setPatternDesignNew(byte[] motorData, int repeatCount) {
		clear();
		setUByte(CMD_SET_PATTERN_DESIGN_NEW, INDEX_CMD);
		setUByte((short) repeatCount, INDEX_ARG3);
		setExtraData(motorData);
		return this;
	}
    
    private void setText(String val) {
        setString(val, HARNESS_CMD_PACKET_TEXT_MAX_LEN, INDEX_TEXT);
    }

    private void setExtraData(byte[] val) {
        setByteArray(val, HARNESS_CMD_PACKET_TEXT_MAX_LEN, INDEX_TEXT);
    }
}
