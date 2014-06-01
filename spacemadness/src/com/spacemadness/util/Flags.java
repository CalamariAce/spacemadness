package com.spacemadness.util;

import java.util.Arrays;
import java.util.List;

public class Flags {

	private static List<String> flags;
	
	public static String getFlag(String flagName) {
		int index = flags.indexOf(flagName);
		if (index > -1) {
			return flags.get(index + 1);
		}
		return null;
	}

	public static boolean isSet(String flagName) {
		return flags.indexOf(flagName) > 0;
	}
	
	public static void parse(String args[]) {
		flags = Arrays.asList(args);
	}
}
