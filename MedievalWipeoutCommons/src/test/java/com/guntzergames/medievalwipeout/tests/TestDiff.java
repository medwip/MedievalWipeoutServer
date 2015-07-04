package com.guntzergames.medievalwipeout.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.guntzergames.medievalwipeout.beans.DiffResult;
import com.guntzergames.medievalwipeout.utils.DiffUtils;

import difflib.Patch;

public class TestDiff {

	private void testDiff(String from, String to) throws Exception {
		
		DiffResult result = DiffUtils.compute(from, to);
		System.out.println("res");
		System.out.println(result.toString());
		
		System.out.println("patch");
		String res = DiffUtils.patch(from, result);
		System.out.println(res);
		Assert.assertEquals(to.trim(), res.trim());
		
	}
	
	private String strFromFile(File f) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String str;
		
		while ( (str = br.readLine()) != null ) {
			sb.append(str + "\n");
		}
		
		br.close();
		
		return sb.toString();
		
	}
	
	private List<String> listStrFromFile(File f) throws Exception {
		
		List<String> ret = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String str;
		
		while ( (str = br.readLine()) != null ) {
			ret.add(str);
		}
		
		br.close();
		
		return ret;
		
	}
	
	@Test
	public void testDiffs() throws Exception {
		
		testDiff("test\ntest3\ntest4", "test\ntest2\ntest3");
		testDiff("test\ntest3\ntest4", "test\ntest2\ntest3\ntest5");
		testDiff("test\ntest3\ntest4", "test\ntest2\ntest3\ntest5\ntest6");
		testDiff("test\ntest2\ntest3\ntest5\ntest6", "test\ntest3\ntest4");
		
	}
	
	@Test
	public void testDiffsGameView() throws Exception {
		
		String previousJson = strFromFile(new File("src/test/resources/diff/previousJson.json"));
		String json = strFromFile(new File("src/test/resources/diff/json.json"));
		testDiff(previousJson, json);
		
	}
	
	@Test
	public void testDiffsGameView2() throws Exception {
		
		List<String> original = listStrFromFile(new File("src/test/resources/diff/previousJson.json"));
		List<String> revised = listStrFromFile(new File("src/test/resources/diff/json.json"));
		Patch patch = difflib.DiffUtils.diff(original, revised);
		
		System.out.println(patch);
		
		List<String> result = (List<String>)(difflib.DiffUtils.patch(original, patch));
		Assert.assertEquals(result.size(), revised.size());
		for ( int i = 0; i < result.size(); i++ ) {
			Assert.assertEquals(result.get(i), revised.get(i));
		}
		
	}
	
}
