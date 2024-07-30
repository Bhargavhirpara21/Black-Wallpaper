package wallpaper.black.live.uhd.AppUtils;

import android.util.Log;

import wallpaper.black.live.uhd.BuildConfig;

public class LoggerCustom {

	public static final boolean isDebuggable = BuildConfig.DEBUG;

	/**
	 * Set an info log message.
	 * 
	 * @param tag
	 *            for the log message.
	 * @param message
	 *            Log to output to the console.
	 */
	public static void i(String tag, String message) {
		try {
			if (isDebuggable) {
				Log.i(tag, message);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Set an error log message.
	 * 
	 * @param tag
	 *            for the log message.
	 * @param message
	 *            Log to output to the console.
	 */
	public static void erorr(String tag, String message) {
		try {
			// if (BuildConfig.DEBUG)
			if (isDebuggable) {
				Log.e(tag, message);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Set a warning log message.
	 * 
	 * @param tag
	 *            for the log message.
	 * @param message
	 *            Log to output to the console.
	 */

	public static void w(String tag, String message) {
		try {
			// if (BuildConfig.DEBUG)
			if (isDebuggable) {
				Log.w(tag, message);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Set a debug log message.
	 * 
	 * @param tag
	 *            for the log message.
	 * @param message
	 *            Log to output to the console.
	 */
	public static void d(String tag, String message) {
		try {
			// if (BuildConfig.DEBUG)
			if (isDebuggable) {
				Log.d(tag, message);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * Set a verbose log message.
	 * 
	 * @param tag
	 *            for the log message.
	 * @param message
	 *            Log to output to the console.
	 */
	public static void v(String tag, String message) {
		try {
			if (isDebuggable) {
				Log.v(tag, message);
			}
		} catch (Exception e) {
		}
	}

	public static void s(String message) {
		try {
			if (isDebuggable) {
				System.out.println("safestart : " + message);
			}
		} catch (Exception e) {
		}
	}

	public static void printStackTrace(Exception e) {
		try {
			if (isDebuggable) {
				e.printStackTrace();
			}
		} catch (Exception e1) {
		}
	}
}
