package com.guntzergames.medievalwipeout.beans;

import java.util.Comparator;

public class DiffComparator implements Comparator<String> {

	public DiffComparator() {
	}

	public int compare(String o1, String o2) {
		String u1 = o1.substring(1);
		String u2 = o2.substring(1);
		String p1 = o1.substring(0, 1);
		String p2 = o2.substring(0, 1);
		
		if ( u1.equals(u2) ) {
			return p1.compareTo(p2);
		}
		else {
			return u1.compareTo(u2);
		}
	}

}
