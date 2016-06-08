package com.aau.openalrenderer.openal;

/**
 * This class represents an OpenAL source. Sources are used to play back the
 * data from a buffer. The source stores all data of the play back, such as the
 * sound's position or its pitch. Each source is assigned to one buffer.
 * However, several source can share a single buffer.
 * @author Martin Pielot
 */
public class Source {

	// ========================================================================
	// Fields
	// ========================================================================

	private int		sourceId;
	private Buffer	buffer;

	// ========================================================================
	// Constructor
	// ========================================================================

	/**
	 * Creates a new sound source for the given buffer.
	 * @param buffer the buffer
	 */
	public Source(Buffer buffer) {
		this.buffer = buffer;
		this.sourceId = OpenALRenderer.nAddSource(buffer.getId());
	}

	// ========================================================================
	// Methods
	// ========================================================================	
	
	public void setPosition(float x, float y, float z) {
//		Log.d("Source", "Trying to set pos: " + sourceId);
		// We flip the Z axis because it is a right handed cartesian system
		OpenALRenderer.nSetPosition(sourceId, x, y, -z);
	}
	
//	public float[] getPosition(){
//		return OpenALRenderer.getPosition(sourceId);
//	}
	public int getDuration(){
		return OpenALRenderer.nGetSourceDuration(sourceId);
	}
	
	public boolean isBufferSame(Buffer buffer){
		return (this.buffer == buffer) ? true : false; //TODO: HA. This won't work as expect. Only returns true if the buffer is the same instance, not identical to the other buffer.
	}
	
	public void setMaxDistance(float distance){
		OpenALRenderer.nSetMaxDistance(sourceId, distance);
	}
	
	public void setReferenceDistance(float distance){
		OpenALRenderer.nSetReferenceDistance(sourceId, distance);
	}
	public void setPitch(float pitch) {
		OpenALRenderer.nSetPitch(sourceId, pitch);
	}
	
	public void setGain(float gain) {
		OpenALRenderer.nSetGain(sourceId, gain);
	}

	public void setMinGain(float gain) {
		OpenALRenderer.nSetMinGain(sourceId, gain);
	}

	public void setMaxGain(float gain) {
		OpenALRenderer.nSetMaxGain(sourceId, gain);
	}

	
	public void setRolloffFactor(float rollOff) {
		OpenALRenderer.nSetRolloffFactor(sourceId, rollOff);
	}
	
	public long play(boolean loop) {
		OpenALRenderer.nPlay(sourceId, loop);
		return getDuration();
	}
	
	public void stop() {
		OpenALRenderer.nStop(sourceId);
	}

	public void release() {
		OpenALRenderer.nReleaseSource(sourceId);
	}

	public String toString() {
		return "source " + sourceId + " playing " + buffer;
	}

}
