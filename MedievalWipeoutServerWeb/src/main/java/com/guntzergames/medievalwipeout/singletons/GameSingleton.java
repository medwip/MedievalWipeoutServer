package com.guntzergames.medievalwipeout.singletons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.persistence.GameDao;

@Singleton
public class GameSingleton {

	private final static Logger LOGGER = Logger.getLogger(GameSingleton.class);
	
	@EJB
	private GameDao gameDao;

	private Map<Long, Game> ongoingGames = new TreeMap<Long, Game>();

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

}
