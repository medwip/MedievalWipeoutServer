package com.guntzergames.medievalwipeout.managers;

import javax.ejb.Stateless;

import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.Token;

@Stateless
public class TokenManager {

	public Token generateToken(Player player) {
		
		Token token = new Token();
		
		token.setUid(String.format("%d%d", player.getId(), player.getGame().getId()));
		token.setPlayer(player);
		
		return token;
		
	}

}
