package jdepend.parse.util;

import jdepend.metadata.util.ParseUtil;

public class ParseTool {

	public static String getType(String s) {
		if (s.startsWith("[")) {
			if (s.startsWith("[[[D")) {
				s = s.substring(4);
			}
			if (s.startsWith("[[D")) {
				s = s.substring(3);
			}
			if (s.startsWith("[D")) {
				s = s.substring(2);
			}
			if (s.startsWith("[[[L")) {
				s = s.substring(4);
			}
			if (s.startsWith("[[L")) {
				s = s.substring(3);
			}
			if (s.startsWith("[L")) {
				s = s.substring(2);
			}
			if (s.startsWith("[[I")) {
				s = s.substring(3);
			}
			if (s.startsWith("[[J")) {
				s = s.substring(3);
			}
			if (s.startsWith("[J")) {
				s = s.substring(2);
			}
			if (s.startsWith("[[F")) {
				s = s.substring(3);
			}
			if (s.startsWith("[F")) {
				s = s.substring(2);
			}
			if (s.startsWith("[I")) {
				s = s.substring(2);
			}
			if (s.startsWith("[B")) {
				s = s.substring(2);
			}
			if (s.startsWith("[Z")) {
				s = s.substring(2);
			}
			if (s.startsWith("[C")) {
				s = s.substring(2);
			}
			if (s.startsWith("[S")) {
				s = s.substring(2);
			}
		}
		if (s.endsWith(";")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	public static String slashesToDots(String s) {
		return s.replace('/', '.');
	}

	public static String getPackageName(String className) {
		int index = className.lastIndexOf(".");
		if (index > 0) {
			int startPos = 0;
			if (className.startsWith("[L")) {
				startPos = 2;
			}
			return className.substring(startPos, index);
		} else {
			if (ParseUtil.isBaseType(className)) {
				return null;
			} else {
				return "Default";
			}
		}
	}
}
