package com.bsoft.mob.ienr.contancts;

/**
 * Created by hy on 14-4-28.
 */
public class UnitechActions {

	/**
	 * Enable/Disable the Scan2Key which supports keyboard emulation features.
	 */
	public static final String ACTION_SETTING_SCAN2KEY = "unitech.scanservice.scan2key_setting";

	public static final String EXTRA_SETTING_SCAN2KEY_BOOLEAN = "scan2key";

	/**
	 * 初始化扫描枪action
	 */
	public static final String INIT_INTENT = "unitech.scanservice.init";
	public static final String EXTRA_SETTING_SCAN2KEY_INIT_BOOLEAN = "enable";

	/**
	 * Save the scanner’s setting
	 */
	public static final String ACTION_SAVE_SETTINGS = "unitech.scanservice.save_setting";

	/**
	 * Load the scanner’s setting
	 */
	public static final String ACTION_LOAD_SETTINGS = "unitech.scanservice.save_setting";

	/**
	 * And default “Path” is /sdcard/ if “Path” is left empty. Make sure path
	 * exist.
	 */
	public static final String EXTRA_SAVE_SETTINGS_STRING = "Path";

	/**
	 * Receive the data from scanner.
	 */
	public static final String ACTION_RECEIVER_DATE = "unitech.scanservice.data";

}
