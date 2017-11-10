package com.slurpy.glowfighter.utils;

public class Constants {
	public static final float PPM = 50f;
	public static final float MPP = 1/PPM;
	
	public static final int BLUR_PASSES = 3;
	public static final float FBO_SIZE_RATIO = 4;// 1(FBO):FBO_SIZE_RATIO(SCREEN) or FBO = SCREEN / FBO_SIZE_RATIO
	
	public static final float TIME_STEP = 1/60f;
	public static final int VELOCITY_ITERATIONS = 6;
	public static final int POSITION_ITERATIONS = 2;
	
	public static final float MAX_SHAKE = 0.5f;
	
	public static final String SETTINGS_FILE = "com.slurpy.glowfighter.settings";
}
