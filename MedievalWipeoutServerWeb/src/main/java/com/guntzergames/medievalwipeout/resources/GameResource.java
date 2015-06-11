package com.guntzergames.medievalwipeout.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Patch;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.Token;
import com.guntzergames.medievalwipeout.exceptions.GameException;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.managers.AccountManager;
import com.guntzergames.medievalwipeout.managers.GameManager;
import com.guntzergames.medievalwipeout.singletons.GameSingleton;
import com.guntzergames.medievalwipeout.views.GameView;

@Stateless
@Path("/game")
public class GameResource {

	private final static Logger LOGGER = Logger.getLogger(GameResource.class);

	@EJB
	private GameManager gameManager;
	@EJB
	private AccountManager accountManager;
	@EJB
	private GameSingleton gameSingleton;

	private String buildGameView(Player player, Game game) throws PlayerNotInGameException {
		GameView gameView = game.buildGameView(player);
		String ret = gameView.toJson();
		player.getEvents().clear();
		return ret;
	}

	@GET
	@Path("join/{facebookUserId}/{deckId}")
	@Produces("text/plain")
	public String joinGame(@PathParam("facebookUserId") String facebookUserId, @PathParam("deckId") long deckId) throws GameException {
		Game game = gameManager.joinGame(facebookUserId, deckId);
		Player player = gameManager.selectPlayer(game, facebookUserId);
		gameSingleton.generateToken(player);

		LOGGER.info(String.format("Game joined: %s, token=%s", game, player.getToken()));
		String ret = buildGameView(player, game);
		return ret;
	}

	@GET
	@Path("delete/{gameId}")
	@Produces("text/plain")
	public void deleteGame(@PathParam("gameId") long gameId) {
		gameManager.deleteGame(gameId);
	}

	@GET
	@Path("drawInitialHand/{gameId}/{userName}")
	@Produces("text/plain")
	public String drawInitialHand(@PathParam("gameId") long gameId, @PathParam("userName") String userName) throws GameException {
		Game game = gameManager.getGame(gameId);
		Player player = gameManager.selectPlayer(game, userName);
		gameManager.drawInitialHand(player, game);
		String ret = buildGameView(player, game);
		return ret;
	}

	@GET
	@Path("get/{gameId}/{facebookUserId}/{fullJson}")
	@Produces("application/json")
	public String getGame(@PathParam("gameId") long gameId, @PathParam("facebookUserId") String facebookUserId, @PathParam("fullJson") boolean fullJson) throws GameException {

		LOGGER.debug("Perf Monitor: before getGame " + gameId);
		Game game = gameManager.getGame(gameId);

		LOGGER.debug("Perf Monitor: after getGame " + gameId);
		Player player = gameManager.selectPlayer(game, facebookUserId);

		LOGGER.debug("Perf Monitor: player selected " + player.getAccount().getFacebookUserId());
		String json = buildGameView(player, game);

		String ret = "";

		Token token = gameSingleton.getToken(player);
		if (token != null) {
			String previousJson = gameSingleton.getPreviousGamesMap().get(token.getUid());
			if (previousJson != null) {

				if (previousJson.equals(json)) {
					ret = "";
					LOGGER.info(String.format("Previous JSON and JSON are identical"));
				} else {
					
					/*
					diff_match_patch dmp = new diff_match_patch();
					dmp.Match_Threshold = 0f;
					LinkedList<Patch> patches = dmp.patch_make(previousJson, ret);
					ret = dmp.patch_toText(patches);
					LOGGER.info(String.format("Patch has been created: %s", ret));

					// Test
					Object[] results = dmp.patch_apply(patches, previousJson);
					LOGGER.info(String.format("results[0]: %s", results[0]));
					*/
					
					ret = json;
				}

			} else {
				ret = json;
			}
			gameSingleton.getPreviousGamesMap().put(token.getUid(), json);
		} else {
			ret = json;
		}
		LOGGER.debug("Token = " + token);
		LOGGER.debug("getPreviousGamesMap = " + gameSingleton.getPreviousGamesMap());

		LOGGER.debug("Perf Monitor: response ready to be sent");
		
		if ( fullJson ) {
			ret = json;
		}
		
		return ret;

	}

	@GET
	@Path("nextPhase/{gameId}/{facebookUserId}")
	@Produces("text/plain")
	public String nextPhase(@PathParam("gameId") long gameId, @PathParam("facebookUserId") String facebookUserId) throws GameException {
		Game game = gameManager.nextPhase(gameId);
		Player player = gameManager.selectPlayer(game, facebookUserId);
		String ret = buildGameView(player, game);
		// LOGGER.debug("nextPhase: JSON returned for next phase: " + ret);
		return ret;
	}

	@GET
	@Path("play/{facebookUserId}/{gameId}/{sourceLayout}/{sourceCardId}/{destinationLayout}/{destinationCardId}")
	@Produces("text/plain")
	public String playCard(@PathParam("facebookUserId") String facebookUserId, @PathParam("gameId") long gameId, @PathParam("sourceLayout") String sourceLayout,
			@PathParam("sourceCardId") int sourceCardId, @PathParam("destinationLayout") String destinationLayout, @PathParam("destinationCardId") int destinationCardId)
			throws GameException {
		Game game = gameManager.playCard(facebookUserId, gameId, sourceLayout, sourceCardId, destinationLayout, destinationCardId);
		Player player = gameManager.selectPlayer(game, facebookUserId);
		String ret = buildGameView(player, game);
		// LOGGER.debug("playCard: JSON returned for next phase: " + ret);
		return ret;
	}

	@GET
	@Path("getAll")
	@Produces("text/plain")
	public String getAllGames() {
		List<Game> games = gameManager.getAllGames();
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			mapper.writeValue(out, games);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ret = new String(out.toByteArray());
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public void setGameManager(GameManager gameManager) {
		this.gameManager = gameManager;
	}

}
