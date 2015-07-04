package com.guntzergames.medievalwipeout.persistence;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.exceptions.JsonException;
import com.guntzergames.medievalwipeout.singletons.GameSingleton;

@Stateless
public class GameDao {

	private final static Logger LOGGER = Logger.getLogger(GameDao.class);

	@PersistenceContext(unitName = "mwpu")
	private EntityManager em;

	@EJB
	private AccountDao accountDao;
	@EJB
	private GameSingleton gameSingleton;

	public Game saveGame(Game game) {

		try {
			game.setDataDump(game.toJson());
		}
		catch ( JsonException e ) {
			LOGGER.error("Error in JSON serializing while saving game dump", e);
		}
		Game mergedGame = mergeGame(game);
		gameSingleton.addGame(mergedGame);
		return mergedGame;

	}

	public Game mergeGame(Game game) {

		for (Player player : game.getPlayers()) {
			LOGGER.debug(String.format("Just before merge, player in game: %s", player));
			if (game.getActivePlayer() != null && game.getActivePlayer().getId() == player.getId()) {
				game.setActivePlayer(player);
			}
		}

		LOGGER.debug(String.format("Before mergeGame() in GameDao, game=%s", game));
		LOGGER.debug(String.format("Before mergeGame() in GameDao, player=%s", game.getPlayers().get(0).getPlayerDeckCard1()));
		Game mergedGame = em.merge(game);

		// Set transient fields that have been erased with the merge
		mergedGame.setTransientFields(game);

		em.flush();
		LOGGER.debug(String.format("After mergeGame() in GameDao, game=%s", mergedGame));
		LOGGER.debug(String.format("After mergeGame() in GameDao, player=%s", mergedGame.getPlayers().get(0).getPlayerDeckCard1()));

		return mergedGame;

	}

	public Player mergePlayer(Player player) {

		LOGGER.info(String.format("Merging player %s", player));
		return em.merge(player);

	}

	public DeckTemplate findDeckTemplateById(long deckId) {

		return em.find(DeckTemplate.class, deckId);

	}

	public List<DeckTemplate> findDeckTemplatesByAccount(Account account) {

		return em.createNamedQuery(DeckTemplate.NQ_FIND_BY_ACCOUNT, DeckTemplate.class).setParameter("account", account).getResultList();

	}

	public List<Game> findGamesByGameState(GameState gameState) {

		return em.createNamedQuery(Game.NQ_FIND_BY_GAME_STATE, Game.class).setParameter("gameState", gameState).getResultList();

	}

	public List<Game> findAllGames() {

		return em.createNamedQuery(Game.NQ_FIND_ALL, Game.class).getResultList();

	}

	public Game findGameById(long gameId) {

		return em.find(Game.class, gameId);

	}

	public void deleteGame(Game game) {

		em.remove(game);

	}

}
