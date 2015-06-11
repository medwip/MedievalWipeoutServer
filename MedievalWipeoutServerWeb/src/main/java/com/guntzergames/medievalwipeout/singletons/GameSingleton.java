package com.guntzergames.medievalwipeout.singletons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.Token;
import com.guntzergames.medievalwipeout.managers.TokenManager;
import com.guntzergames.medievalwipeout.persistence.GameDao;

@Singleton
public class GameSingleton {

	private final static Logger LOGGER = Logger.getLogger(GameSingleton.class);
	
	@EJB
	private GameDao gameDao;
	@EJB
	private TokenManager tokenManager;

	private Map<Long, Game> ongoingGames = new TreeMap<Long, Game>();
	
	private Map<String, Token> tokensUidMap = new TreeMap<String, Token>();
	private Map<Long, Token> tokensPlayerMap = new TreeMap<Long, Token>();
	private Map<String, String> previousGamesMap = new TreeMap<String, String>();

	public List<Game> getAllOngoingGames() {
		return new ArrayList<Game>(ongoingGames.values());
	}

	public Game getGame(long gameId) {
		return ongoingGames.get(gameId);
	}

	public Game addGame(Game game) {
		ongoingGames.put(game.getId(), game);
		LOGGER.info("Added game: " + game + " turn=" + game.getTurn());
		return game;
	}

	public void deleteGame(long gameId) {
		ongoingGames.remove(gameId);
	}

	public void clearGames() {
		ongoingGames.clear();
	}
	
	public void clearPreviousGames() {
		previousGamesMap.clear();
	}

	public Map<String, Token> getTokensUidMap() {
		return tokensUidMap;
	}

	public void setTokensUidMap(Map<String, Token> tokensUidMap) {
		this.tokensUidMap = tokensUidMap;
	}
	
	public Token getToken(Player player) {
		return tokensPlayerMap.get(player.getId());
	}

	public Map<String, String> getPreviousGamesMap() {
		return previousGamesMap;
	}

	public void setPreviousGamesMap(Map<String, String> previousGamesMap) {
		this.previousGamesMap = previousGamesMap;
	}

	public void generateToken(Player player) {
		
		Token token = tokenManager.generateToken(player);
		player.setToken(token);
		tokensPlayerMap.put(player.getId(), token);
		tokensUidMap.put(token.getUid(), token);
		
	}
	
}
