package com.guntzergames.medievalwipeout.test;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.guntzergames.medievalwipeout.managers.TokenManager;

@RunWith(Arquillian.class)
public class TestGameManager {

	@Deployment
	public static JavaArchive createDeployment() {
		System.out.println("yo");
		JavaArchive archive = null;

		archive = ShrinkWrap.create(JavaArchive.class).addClass(TokenManager.class).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		System.out.println("3");
		return archive;
	}

	/*
	 * @Inject FooManager fooManager;
	 */
	@Test
	public void simpleTest() {
		System.out.println("yo");
		// Assert.assertEquals(0, fooManager);
	}

}
