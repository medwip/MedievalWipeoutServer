package com.guntzergames.medievalwipeout.beans;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Token {
	
	@JsonIgnore
	private Player player;
	private String uid;
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	

}
