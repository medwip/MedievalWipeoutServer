package com.guntzergames.medievalwipeout.managers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.Game;
import com.guntzergames.medievalwipeout.beans.GameEvent.PlayerType;
import com.guntzergames.medievalwipeout.beans.GameEventDeckCard;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard;
import com.guntzergames.medievalwipeout.beans.GameEventPlayCard.EventType;
import com.guntzergames.medievalwipeout.beans.GameEventResourceCard;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.beans.PlayerDeck;
import com.guntzergames.medievalwipeout.beans.PlayerDeckCard;
import com.guntzergames.medievalwipeout.beans.PlayerField;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard;
import com.guntzergames.medievalwipeout.beans.PlayerFieldCard.Location;
import com.guntzergames.medievalwipeout.beans.PlayerHand;
import com.guntzergames.medievalwipeout.beans.PlayerHandCard;
import com.guntzergames.medievalwipeout.beans.ResourceDeck;
import com.guntzergames.medievalwipeout.beans.ResourceDeckCard;
import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.exceptions.GameException;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.exceptions.UnsupportedPhaseException;
import com.guntzergames.medievalwipeout.interfaces.CommonConstants;
import com.guntzergames.medievalwipeout.persistence.AccountDao;
import com.guntzergames.medievalwipeout.persistence.GameDao;
import com.guntzergames.medievalwipeout.singletons.GameSingleton;

@Stateless
public class GameManager {

	private final static Logger LOGGER = Logger.getLogger(GameManager.class);

	@EJB
	private GameSingleton gameSingleton;
	@EJB
	private GameDao gameDao;
	@EJB
	private AccountDao accountDao;
	@EJB
	private AccountManager accountManager;

	public Game findGameToJoin(Player player) {

		Game ret = null;

		for (Game currentGame : gameSingleton.getAllOngoingGames()) {
			if (currentGame.getGameState() == GameState.WAITING_FOR_JOINER) {
				if (!currentGame.isPlayerRegistered(player.getAccount().getFacebookUserId())) {
					ret = currentGame;
				}
			}
		}

		return ret;

	}

	public Game joinGame(String facebookUserId, long deckId) throws GameException {

		Account account = accountManager.getAccount(facebookUserId);
		LOGGER.info(String.format("Account: %s", account));
		Player player = new Player();
		DeckTemplate deckTemplate = findDeckTemplateById(deckId);
		player.setDeckTemplate(deckTemplate);
		return joinGame(player);

	}

	public Game joinGame(Player player) throws GameException {

		Game game = findGameToJoin(player);

		if (game == null) {
			game = new Game();
			LOGGER.info(String.format("Player %s joining game: %s", player, game));
			game.addPlayer(player);
			game.setActivePlayer(player);
			player.setGame(game);
			game = gameDao.saveGame(game);
			game.setGameState(GameState.WAITING_FOR_JOINER);
			game = gameSingleton.addGame(game);
			LOGGER.info(String.format("Game created, player=%s", player));
		} else {
			game.addPlayer(player);
			game.setActivePlayer(player);
			player.setGame(game);
			LOGGER.info(String.format("Player %s joining game: %s", player, game));
			game = gameDao.saveGame(game);
			LOGGER.info(String.format("After merge, game=%s", game));
			game.setGameState(GameState.INITIALIZING_CREATOR_HAND);
			initializeGame(game);
			game.setGameState(GameState.STARTED);
			game = gameDao.saveGame(game);
		}

		LOGGER.info(String.format("Before return in join, game=%s", game));
		return game;

	}

	public void initializeGame(Game game) throws GameException {

		LOGGER.info(String.format("Before initialize game, game=%s", game));

		for (Player player : game.getPlayers()) {
			player.setInitialPlayerDeck(player.getDeckTemplate().toDeck());
			player.getPlayerDeck().getCards().addAll(player.getInitialPlayerDeck().getCards());
			initPlayer(player);
			drawInitialHand(player, game);
		}

		ResourceDeck initialResourceDeck = game.getInitialResourceDeck();
		initialResourceDeck.addCard(new ResourceDeckCard(3, 0, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(2, 1, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(3, 0, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(0, 0, 1));
		initialResourceDeck.addCard(new ResourceDeckCard(3, 0, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(0, 3, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(1, 2, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(1, 2, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(0, 0, 1));
		initialResourceDeck.addCard(new ResourceDeckCard(0, 3, 0));
		initialResourceDeck.addCard(new ResourceDeckCard(0, 0, 1));
		Collections.shuffle(initialResourceDeck.getCards());
		game.getResourceDeck().getCards().addAll(initialResourceDeck.getCards());
		game.setTurn(1);
		game.setPhase(Phase.BEFORE_RESOURCE_CHOOSE);
		game.setActivePlayer(game.getPlayers().get(0));
		nextPhase(game.getId());

	}

	private PlayerDeckCard drawPlayerDeckInternal(Player player) {

		PlayerDeck playerDeck = player.getPlayerDeck();
		LOGGER.debug("Here, playerDeck=" + playerDeck.getCards().size());
		LOGGER.debug("Here, player.getInitialPlayerDeck()=" + player.getInitialPlayerDeck().getCards().size());

		if (playerDeck.getCards().size() == 0) {
			playerDeck.getCards().addAll(player.getInitialPlayerDeck().getCards());
		}

		return playerDeck.pop();

	}

	private ResourceDeckCard drawResourceDeck(Game game) {

		if (game.getResourceDeck().getCards().size() == 0) {
			game.getResourceDeck().getCards().addAll(game.getInitialResourceDeck().getCards());
		}

		return game.getResourceDeck().pop();

	}

	public void initPlayer(Player player) {
		player.setDefense(5);
		player.setCurrentDefense(player.getDefense());
		player.setGold(10);
		player.setTrade(5);
		player.setLifePoints(100);
	}

	private void resolveBotResourceChoose(Player player) throws GameException {

		LOGGER.info("Resource choose for bot");
		playCard(player, null, 1, null, -1);

	}
	
	private void resolveBotDeckDraw(Player player) throws GameException {
		
		LOGGER.info("Deck draw for bot");
		playCard(player, null, 1, null, -1);
		
	}

	private void resolveBotResourceSelect(Player player) throws GameException {
		
		LOGGER.info("Resource select for bot");
		Game game = player.getGame();
		
		int sourceCardId = CommonConstants.GAME_RESOURCE_FAITH;
		
		if ( game.getTrade() > 0 ) {
			sourceCardId = CommonConstants.GAME_RESOURCE_TRADE;
		}
		else if ( game.getDefense() > 0 ) {
			sourceCardId = CommonConstants.GAME_RESOURCE_DEFENSE;
		}
		
		playCard(player, null, sourceCardId, null, -1);
		
	}

	/*
	 * public void resolveBeforeCreatorTurn(Game game) {
	 * 
	 * game.setPhase(Phase.DURING_CREATOR_PLAY);
	 * 
	 * }
	 * 
	 * public void resolveBeforeJoinerTurn(Game game) {
	 * 
	 * game.setPhase(Phase.DURING_JOINER_PLAY);
	 * 
	 * }
	 */

	private Game triggerNextPhase(Player player) throws GameException {

		Game game = player.getGame();

		if (!player.getAccount().isBot()) {
			game = nextPhase(game.getId());
		} else {
			game.setNextPhaseDelayActivated(true);
			game.setNextPhaseTimestamp((new Date()).getTime());
		}

		return game;

	}

	public Game nextPhase(long gameId) throws GameException {

		Game game = getGame(gameId);
		Player activePlayer = game.getActivePlayer();

		LOGGER.debug(String.format("Enterring nextPhase : %s", game));

		boolean triggerNextPhase = false;

		switch (game.getPhase()) {

			case BEFORE_RESOURCE_CHOOSE:
				resolveBeforeResourceDraw(activePlayer);
				game.setResourceCard1(drawResourceDeck(game));
				game.getResourceCard1().setId(1);
				game.setResourceCard2(drawResourceDeck(game));
				game.getResourceCard2().setId(2);
				game.setPhase(Phase.DURING_RESOURCE_CHOOSE);
				if (activePlayer.getAccount().isBot()) {
					resolveBotResourceChoose(activePlayer);
				}
				break;

			case DURING_RESOURCE_CHOOSE:
				game.setPhase(Phase.AFTER_RESOURCE_CHOOSE);
				triggerNextPhase = true;
				break;

			case AFTER_RESOURCE_CHOOSE:
				game.setPhase(Phase.BEFORE_DECK_DRAW);
				triggerNextPhase = true;
				break;

			case BEFORE_DECK_DRAW:
				drawPlayerDeck(game.getActivePlayer());
				game.setPhase(Phase.DURING_DECK_DRAW);
				if (activePlayer.getAccount().isBot()) {
					resolveBotDeckDraw(activePlayer);
				}
				break;

			case DURING_DECK_DRAW:
				game.setPhase(Phase.AFTER_DECK_DRAW);
				triggerNextPhase = true;
				break;

			case AFTER_DECK_DRAW:
				game.setPhase(Phase.BEFORE_RESOURCE_SELECT);
				triggerNextPhase = true;
				break;

			case BEFORE_RESOURCE_SELECT:
				game.setPhase(Phase.DURING_RESOURCE_SELECT);
				if (activePlayer.getAccount().isBot()) {
					resolveBotResourceSelect(activePlayer);
				}
				break;

			case DURING_RESOURCE_SELECT:
				game.setPhase(Phase.AFTER_RESOURCE_SELECT);
				triggerNextPhase = true;
				break;

			case AFTER_RESOURCE_SELECT:
				game.setPhase(Phase.BEFORE_PLAY);
				triggerNextPhase = true;
				break;

			case BEFORE_PLAY:
				game.setPhase(Phase.DURING_PLAY);
				break;

			case DURING_PLAY:
				game.setPhase(Phase.AFTER_PLAY);
				triggerNextPhase = true;
				break;

			case AFTER_PLAY:
				game.setPhase(Phase.BEFORE_RESOURCE_CHOOSE);
				game.nextTurn();
				triggerNextPhase = true;
				break;

			default:
				break;

		}

		// Save game state in the database
		game = gameDao.saveGame(game);

		LOGGER.debug(String.format("Leaving nextPhase: %s", game));

		if (triggerNextPhase) {
			nextPhase(gameId);
		}

		return game;

	}

	public void drawPlayerDeck(Player player) {

		player.setPlayerDeckCard1(drawPlayerDeckInternal(player));
		player.setPlayerDeckCard2(drawPlayerDeckInternal(player));

	}

	public DeckTemplate findDeckTemplateById(long deckId) {

		return gameDao.findDeckTemplateById(deckId);

	}

	public List<DeckTemplate> findDeckTemplatesByAccount(Account account) {

		return gameDao.findDeckTemplatesByAccount(account);

	}

	public void resolveBeforeResourceDraw(Player player) {

		player.setCurrentDefense(player.getDefense());

	}

	public Game playCard(Player player, String sourceLayout, int sourceCardId, String destinationLayout, int destinationCardId) throws GameException {

		return playCard(player.getAccount().getFacebookUserId(), player.getGame().getId(), sourceLayout, sourceCardId, destinationLayout, destinationCardId);

	}

	public Game playCard(String facebookUserId, long gameId, String sourceLayout, int sourceCardId, String destinationLayout, int destinationCardId) throws GameException {

		Game game = getGame(gameId);
		
		LOGGER.debug("Enterring playCard, game=" + game);
		
		Player player = selectPlayer(game, facebookUserId);
		List<Player> opponents = game.selectOpponents(player);
		// TODO
		Player opponent = opponents.get(0);
		PlayerHand hand = player.getPlayerHand();

		switch (game.getPhase()) {

			case DURING_RESOURCE_CHOOSE:
				ResourceDeckCard gameResourceDeckCard = ((sourceCardId == 1) ? game.getResourceCard1() : game.getResourceCard2());
				game.addTrade(gameResourceDeckCard.getTrade());
				game.addDefense(gameResourceDeckCard.getDefense());
				game.addFaith(gameResourceDeckCard.getFaith());
				GameEventResourceCard gameEventResourceCard = new GameEventResourceCard(PlayerType.PLAYER);
				gameEventResourceCard.setResourceId(sourceCardId);
				gameEventResourceCard.setResourceDeckCard(gameResourceDeckCard);
				player.getEvents().add(gameEventResourceCard);
				opponent.getEvents().add(gameEventResourceCard.duplicate());
				game = triggerNextPhase(player);
				break;

			case DURING_DECK_DRAW:
				PlayerDeckCard playerDeckCard = ((sourceCardId == 1) ? player.getPlayerDeckCard1() : player.getPlayerDeckCard2());
				player.getPlayerHand().getCards().add(new PlayerHandCard(playerDeckCard, player));
				GameEventDeckCard gameEventDeckCard = new GameEventDeckCard(PlayerType.PLAYER);
				gameEventDeckCard.setResourceId(sourceCardId);
				gameEventDeckCard.setPlayerDeckCard(playerDeckCard);
				player.getEvents().add(gameEventDeckCard);
				opponent.getEvents().add(gameEventDeckCard.duplicate());
				game = triggerNextPhase(player);
				break;

			case DURING_RESOURCE_SELECT:
				if (sourceCardId == CommonConstants.GAME_RESOURCE_TRADE) {
					player.addTrade(game.getTrade());
					game.setTrade(0);
				}
				if (sourceCardId == CommonConstants.GAME_RESOURCE_DEFENSE) {
					player.addDefense(game.getDefense());
					game.setDefense(0);
				}
				if (sourceCardId == CommonConstants.GAME_RESOURCE_FAITH) {
					player.addFaith(game.getFaith());
					game.setFaith(0);
				}
				game = triggerNextPhase(player);
				break;

			case DURING_PLAY:
				LOGGER.info(String.format("cardId = %d", sourceCardId));

				GameEventPlayCard playerEvent = new GameEventPlayCard(PlayerType.PLAYER);

				// Play card from hand
				if (destinationLayout.startsWith("playerField")) {
					PlayerField playerField = null;
					PlayerHandCard card = hand.getCards().get(sourceCardId);
					PlayerFieldCard fieldCard = new PlayerFieldCard(card);
					if (destinationLayout.endsWith("Attack")) {
						playerField = player.getPlayerFieldAttack();
						fieldCard.setLocation(Location.ATTACK);
						fieldCard.setPlayed(false);
					} else {
						playerField = player.getPlayerFieldDefense();
						fieldCard.setLocation(Location.DEFENSE);
						fieldCard.setPlayed(true);
					}
					playerEvent.setSource(card);
					playerEvent.setSourceIndex(sourceCardId);
					playerEvent.setDestination(fieldCard);
					playerEvent.setDestinationIndex(playerField.getLastIndex());
					player.setGold(player.getGold() - card.getGoldCost());
					playerField.addCard(fieldCard);
					hand.getCards().remove((int) sourceCardId);
					player.updatePlayableHandCards();
				}

				// Play card from field
				if (sourceLayout.equals("playerFieldAttack")) {

					PlayerField playerField = player.getPlayerFieldAttack();
					PlayerFieldCard sourceCard = playerField.getCards().get(sourceCardId);

					if (destinationLayout.equals("opponentFieldDefense")) {
						playerEvent.setDestination(new PlayerFieldCard(sourceCard));
						playerEvent.setDestinationIndex(sourceCardId);
						playerEvent.setEventType(EventType.ATTACK_DEFENSE_FIELD);
						if (opponent.getCurrentDefense() > sourceCard.getAttack()) {
							opponent.removeCurrentDefense(sourceCard.getAttack());
						} else {
							opponent.removeLifePoints(sourceCard.getAttack() - opponent.getCurrentDefense());
							opponent.setCurrentDefense(0);
						}
					}

					if (destinationLayout.startsWith("opponentCard")) {
						PlayerFieldCard destinationCard = null;
						playerEvent.setEventType(EventType.ATTACK_ATTACK_CARD);
						PlayerField destinationField = null;
						if (destinationLayout.endsWith("Attack")) {
							destinationField = opponent.getPlayerFieldAttack();
						} else if (destinationLayout.endsWith("Defense")) {
							destinationField = opponent.getPlayerFieldDefense();
						} else {
							throw new GameException(String.format("Unknown destination layout: %s", destinationLayout));
						}
						destinationCard = destinationField.getCards().get(destinationCardId);
						if (destinationCard.getCurrentLifePoints() > sourceCard.getAttack()) {
							destinationCard.removeCurrentLifePoints(sourceCard.getAttack());
						} else {
							opponent.removeLifePoints(sourceCard.getAttack() - opponent.getCurrentDefense());
							destinationCard.setCurrentLifePoints(0);
							destinationField.getCards().remove(destinationCardId);
						}
						playerEvent.setDestination(destinationCard);
						playerEvent.setDestinationIndex(destinationCardId);
					}

					LOGGER.info(String.format("Source layout: %s", sourceLayout));
					playerEvent.setSource(sourceCard);
					playerEvent.setSourceIndex(sourceCardId);
					sourceCard.setPlayed(true);

				}

				player.getEvents().add(playerEvent);
				opponent.getEvents().add(playerEvent.duplicate());
				LOGGER.info(String.format("Event added: %s", playerEvent.toString()));

				break;

			default:
				throw new UnsupportedPhaseException(String.format("Unsupported phase: %s", game.getPhase()));

		}
		
		LOGGER.debug("Leaving playCard, game=" + game);

		return game;

	}

	public void deleteGame(long gameId) {

		gameSingleton.deleteGame(gameId);
		Game game = gameDao.findGameById(gameId);
		gameDao.deleteGame(game);

	}

	public void drawInitialHand(Player player, Game game) throws PlayerNotInGameException {

		player = game.selectPlayer(player);
		for (int i = 0; i < 5; i++) {
			LOGGER.info(String.format("Player: %s, player.getHand(): %s, player.getDeck(): %s", player, player.getPlayerHand(), player.getPlayerDeck()));
			player.getPlayerHand().addCard(new PlayerHandCard(player.getPlayerDeck().pop(), player));
		}
		game.setGameState(GameState.INITIALIZING_JOINER_HAND);

	}

	public Game loadDump(Game game) {

		String json = game.getDataDump();
		if (json != null) {
			game = Game.fromJson(json);
			LOGGER.info(String.format("Loaded game %s from JSON dump", game.getId()));
		}

		for (Player player : game.getPlayers()) {

			DeckTemplate deckTemplate = player.getDeckTemplate();
			deckTemplate = accountDao.findDeckTemplateById(deckTemplate.getId());
			player.setDeckTemplate(deckTemplate);
			player.setGame(game);
			if (game.getActivePlayer() != null && game.getActivePlayer().getId() == player.getId()) {
				game.setActivePlayer(player);
			}
			LOGGER.info(String.format("Loaded deck template %s", deckTemplate));

		}
		LOGGER.info("game.getResourceCard2() " + game.getResourceCard2());
		LOGGER.info("game.getPlayers().get(0).getAccount() " + game.getPlayers().get(0).getAccount());

		return game;

	}

	public Game getGame(long gameId) throws GameException {

		// Try to get the get from the memory cache first
		Game game = gameSingleton.getGame(gameId);

		// If game is not cached, load it from database
		if (game == null) {
			game = gameDao.findGameById(gameId);
			game = loadDump(game);
		}

		gameSingleton.addGame(game);

		// If nextPhaseDelay has been activated (by a bot) since a sufficient
		// delay, launch the next phase
		if ( game.isNextPhaseDelayActivated() && 
				( (((new Date()).getTime()) - game.getNextPhaseTimestamp()) 
						> CommonConstants.BOT_PHASE_DURATION
				)
		) {
			LOGGER.info(String.format("Next phase delay activated: timeStamp=%s, now=%s", new Date(game.getNextPhaseTimestamp()), new Date()));
			game.setNextPhaseDelayActivated(false);
			game = nextPhase(gameId);
		}

		return game;

	}

	public List<Game> getAllGames() {
		return gameDao.findAllGames();
	}

	public Player selectPlayer(Game game, String facebookUserId) throws PlayerNotInGameException {

		LOGGER.debug("game.getPlayers().get(0).getAccount() " + game.getPlayers().get(0).getAccount());
		LOGGER.debug("game.getPlayers().get(0).getAccount().getFacebookUserId() " + game.getPlayers().get(0).getAccount().getFacebookUserId());
		for (Player player : game.getPlayers()) {
			if (player.getAccount().getFacebookUserId().equals(facebookUserId))
				return player;
		}

		throw new PlayerNotInGameException();

	}

}
