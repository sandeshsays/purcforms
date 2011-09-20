package org.purc.purcforms.client;


/**
 * 
 * @author danielkayiwa
 *
 */
public class FormRunnerContext {

	private static boolean warnOnClose = true;

	public static boolean isWarnOnClose() {
		return warnOnClose;
	}

	public static void setWarnOnClose(boolean warnOnClose) {
		FormRunnerContext.warnOnClose = warnOnClose;
	}
}
