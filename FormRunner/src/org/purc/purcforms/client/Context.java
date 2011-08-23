package org.purc.purcforms.client;


/**
 * 
 * @author danielkayiwa
 *
 */
public class Context {

	private static boolean warnOnClose = true;

	public static boolean isWarnOnClose() {
		return warnOnClose;
	}

	public static void setWarnOnClose(boolean warnOnClose) {
		Context.warnOnClose = warnOnClose;
	}
}
