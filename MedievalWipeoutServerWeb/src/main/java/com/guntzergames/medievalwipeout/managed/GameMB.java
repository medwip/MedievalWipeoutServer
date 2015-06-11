package com.guntzergames.medievalwipeout.managed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.Token;
import com.guntzergames.medievalwipeout.exceptions.GameException;
import com.guntzergames.medievalwipeout.managers.AccountManager;
import com.guntzergames.medievalwipeout.managers.GameManager;
import com.guntzergames.medievalwipeout.singletons.GameSingleton;

@ManagedBean(name = "gameMB")
@SessionScoped
public class GameMB  {

	@EJB
	private GameManager gameManager;
	@EJB
	private AccountManager accountManager;
	@EJB
	private GameSingleton gameSingleton;
	
	public List<Game> getAllGames() {
		return gameManager.getAllGames();
	}
	
	public List<Token> getTokens() {
		
		Map<String, Token> tokensMap = gameSingleton.getTokensUidMap();
		return new ArrayList<Token>(tokensMap.values());
		
	}
	
	public void deleteGame(long gameId) {
		gameManager.deleteGame(gameId);
	}
	
	public void nextPhase(long gameId) throws GameException {
		gameManager.nextPhase(gameId);
	}
	
	public void joinGame() throws GameException {
		gameManager.joinGame("BOT", 11);
	}
	
	public String getPlayers(long gameId) throws GameException {
		String ret = "";
		Game game = gameManager.getGame(gameId);
		
		for ( Player player : game.getPlayers() ) {
			ret += player + ", ";
		}
		
		return ret;
	}
	
	public void clearGames() {
		gameSingleton.clearGames();
	}
	
	public void clearPreviousGames() {
		gameSingleton.clearPreviousGames();
	}
	
	public void refresh() {
	}
	
}
