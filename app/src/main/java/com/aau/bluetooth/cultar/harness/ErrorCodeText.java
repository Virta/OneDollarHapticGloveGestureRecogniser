package com.aau.bluetooth.cultar.harness;

public class ErrorCodeText {
	public static String getDescription(int ecode) {
		final String pretext = "(" + ecode + "): "; 
		switch (ecode) {
		case 90:
			return pretext + "BioHarness reconnecting";
		case 91:
			return pretext + "BioHarness lost connection";
		case 92:
			return pretext + "BioHarness BT module rebooting";
		case 93:
			return pretext + "BioHarness connection: Too many retries";
		case 150:
			return pretext + "Pattern started playing";
		case 151:
			return pretext + "Pattern stopped playing";
		case 152:
			return pretext + "Can't play new pattern while pattern is playing";
		case 153:
			return pretext + "Repeating pattern playback";
		case 154:
			return pretext + "Succesfully stopped pattern playback";
		case 180:
			return pretext + "Disabling BioHarness";
		case 181:
			return pretext + "Enabling BioHarness";
		case 182:
			return pretext + "Disabling BioHarness transmissions";
		case 183:
			return pretext + "Enabling BioHarness transmissions";
		default:
			return pretext + "No known error text for ecode";
		}
	}
}