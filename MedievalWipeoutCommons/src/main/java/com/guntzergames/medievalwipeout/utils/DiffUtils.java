package com.guntzergames.medievalwipeout.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.guntzergames.medievalwipeout.beans.DiffResult;

import difflib.PatchFailedException;

public class DiffUtils {
	
	private static List<String> toList(BufferedReader b) throws IOException {
		
		List<String> l = new ArrayList<String>();
		boolean end = false;

		while (!end) {

			String str = b.readLine();
			if (str == null) {
				end = true;
			} else {
				l.add(str);
			}

		}

		return l;
		
	}

	private static String[] toTab(BufferedReader b) throws IOException {

		List<String> l = toList(b);
		String[] tab = new String[l.size()];
		l.toArray(tab);
		return tab;

	}
	
	public static DiffResult compute(String from, String to) throws IOException {
		
		BufferedReader f1 = new BufferedReader(new StringReader(from));
		BufferedReader f2 = new BufferedReader(new StringReader(to));

		List<String> original = toList(f1);
		List<String> revised = toList(f2);
		
		return new DiffResult(difflib.DiffUtils.diff(original, revised));
		
	}
	
	public static String patch(String from, DiffResult diffResult) throws IOException, PatchFailedException {
		
		List<String> original = toList(new BufferedReader(new StringReader(from)));
		List<String> result = (List<String>)(difflib.DiffUtils.patch(original, diffResult.getPatch()));
		StringBuffer sb = new StringBuffer();
		
		for ( String str : result ) {
			sb.append(str + "\n");
		}
		
		return sb.toString().trim();
		
	}
	
	/*
	public static DiffResult compute(String from, String to) throws IOException {

		DiffResult result = new DiffResult();
		Map<String, String> diffs = result.getDiffs();

		BufferedReader f1 = new BufferedReader(new StringReader(from));
		BufferedReader f2 = new BufferedReader(new StringReader(to));

		// read in lines of each file
		String[] x = toTab(f1);
		String[] y = toTab(f2);

		// number of lines of each file
		int M = x.length;
		int N = y.length;

		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		int[][] opt = new int[M + 1][N + 1];

		// compute length of LCS and all subproblems via dynamic programming
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (x[i].equals(y[j]))
					opt[i][j] = opt[i + 1][j + 1] + 1;
				else
					opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
			}
		}

		// recover LCS itself and print out non-matching lines to standard
		// output
		int i = 0, j = 0;
		while (i < M && j < N) {
			
			if (x[i].equals(y[j])) {
				i++;
				j++;
			} else if (opt[i + 1][j] >= opt[i][j + 1]) {
				diffs.put("<" + i, x[i]);
				i++;
			}
			else {
				diffs.put(">" + i, y[j]);
				j++;
			}
		}

		// dump out one remainder of one string if the other is exhausted
		int k = i;
		while (i < M || j < N) {
			if (i == M) {
				diffs.put(">" + (k++), y[j]);
				j++;
			}
			else if (j == N) {
				diffs.put("<" + i, x[i]);
				i++;
			}
		}
		
		return result;

	}
	
	public static String patch(String from, DiffResult result) throws IOException {
		
		BufferedReader f1 = new BufferedReader(new StringReader(from));
		String[] x = toTab(f1);
		StringBuffer sb = new StringBuffer();
		Map<String, String> diffs = result.getDiffs();
		
		for ( int i = 0; i < x.length; i++ ) {
			
			if ( diffs.containsKey(">" + i) ) {
				sb.append(diffs.get(">" + i) + "\n");
				diffs.remove(">" + i);
				i--;
			}
			else if ( diffs.containsKey("<" + i) ){
				diffs.remove("<" + i);
			}
			else {
				sb.append(x[i] + "\n");
			}
			
		}
		
		// Remaining items (if result string was longer...)
		// Sort items to ensure they are inserted in the correct order
		List<String> remainingDiffs = new ArrayList<String>(diffs.keySet());
		Collections.sort(remainingDiffs);
		for ( String diff : remainingDiffs ) {
			
			if ( diff.startsWith(">") ) {
				sb.append(diffs.get(diff) + "\n");
			}
			
		}
		
		return sb.toString().trim();
		
	}
	*/
}
