package com.guntzergames.medievalwipeout.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;

import com.guntzergames.medievalwipeout.enums.GameState;
import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.exceptions.JsonException;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.utils.JsonUtils;
import com.guntzergames.medievalwipeout.views.GameView;

@Entity
@Table(name = "GAME")
@NamedQueries({
	@NamedQuery(name = Game.NQ_FIND_BY_GAME_STATE, query = "SELECT g FROM Game g WHERE g.gameState = :gameState"),
	@NamedQuery(name = Game.NQ_FIND_ALL, query = "SELECT g FROM Game g")
})
public class Game {

	public final static String NQ_FIND_BY_GAME_STATE = "NQ_FIND_GAMES_BY_GAME_STATE";
	public final static String NQ_FIND_ALL = "NQ_FIND_ALL_GAMES";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private long id;

	@OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Player> players = new ArrayList<Player>();
	
	@OneToOne(targetEntity = Player.class, cascade=CascadeType.ALL)
	@JoinColumn(name = "ACTIVE_PLAYER_KEY")
	private Player activePlayer;

    @Enumerated(EnumType.STRING)
    @Column(name = "GAME_STATE")
	private GameState gameState = GameState.WAITING_FOR_JOINER;

    @Enumerated(EnumType.STRING)
    @Column(name = "PHASE")
	private Phase phase;
    
    @JsonIgnore
    @Column(name = "DATA_DUMP")
    private String dataDump;
    
    @Column(name = "LIBEL")
    private String libel;
    
	@Transient
	private int numberOfPlayers;

	@Transient
	private ResourceDeck resourceDeck = new ResourceDeck();
	@Transient
	private ResourceDeck initialResourceDeck = new ResourceDeck();
	@Transient
	private ResourceDeckCard resourceCard1, resourceCard2;
	@Transient
	private int turn;

	@Transient
	private int trade;
	@Transient
	private int defense;
	@Transient
	private int faith;
	@Transient
	private int alchemy;
	
	@Transient
	private boolean nextPhaseDelayActivated;
	@Transient
	private Long nextPhaseTimestamp;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public Player selectPlayer(Player player) throws PlayerNotInGameException {

		return selectPlayer(player.getAccount().getFacebookUserId());
	}

	public List<Player> selectOpponents(Player player) throws PlayerNotInGameException {

		List<Player> opponents = new ArrayList<Player>();
		
		for ( Player currentPlayer : players ) {
			if ( !currentPlayer.hasSameAccount(player) ) {
				opponents.add(currentPlayer);
			}
		}
		
		return opponents;
		
	}
	
	public Player selectOpponent(Player player) throws PlayerNotInGameException {
		
		return selectOpponents(player).get(0);
		
	}

	public Player selectPlayer(String userName) throws PlayerNotInGameException {

		for ( Player player : players ) {
			if ( player.getAccount().getFacebookUserId().equals(userName) ) {
				return player;
			}
		}
		
		throw new PlayerNotInGameException();
	}

	@Override
	public String toString() {
		return String.format("Game %s, state=%s, turn=%s, phase=%s", id, gameState, turn, phase);
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public ResourceDeck getResourceDeck() {
		return resourceDeck;
	}

	public void setResourceDeck(ResourceDeck resourceDeck) {
		this.resourceDeck = resourceDeck;
	}

	public ResourceDeck getInitialResourceDeck() {
		return initialResourceDeck;
	}

	public void setInitialResourceDeck(ResourceDeck initialResourceDeck) {
		this.initialResourceDeck = initialResourceDeck;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public boolean isPlayerRegistered(String facebookUserId) {
		boolean found = false;
		
		for ( Player currentPlayer : players ) {
			if ( currentPlayer.getAccount().getFacebookUserId().equals(facebookUserId) ) {
				found = true;
				break;
			}
		}
		
		return found;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}

	public String getDataDump() {
		return dataDump;
	}

	public void setDataDump(String dataDump) {
		this.dataDump = dataDump;
	}

	public String getLibel() {
		return libel;
	}

	public void setLibel(String libel) {
		this.libel = libel;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public ResourceDeckCard getResourceCard1() {
		return resourceCard1;
	}

	public void setResourceCard1(ResourceDeckCard resourceCard1) {
		this.resourceCard1 = resourceCard1;
	}

	public ResourceDeckCard getResourceCard2() {
		return resourceCard2;
	}

	public void setResourceCard2(ResourceDeckCard resourceCard2) {
		this.resourceCard2 = resourceCard2;
	}
	
	public boolean isNextPhaseDelayActivated() {
		return nextPhaseDelayActivated;
	}

	public void setNextPhaseDelayActivated(boolean nextPhaseDelayActivated) {
		this.nextPhaseDelayActivated = nextPhaseDelayActivated;
	}

	public Long getNextPhaseTimestamp() {
		return nextPhaseTimestamp;
	}

	public void setNextPhaseTimestamp(Long nextPhaseTimestamp) {
		this.nextPhaseTimestamp = nextPhaseTimestamp;
	}

	public String toJson() throws JsonException {
		return JsonUtils.toJson(this, DefaultTyping.JAVA_LANG_OBJECT);
	}
	
	public static Game fromJson(String json) throws JsonException {
		return JsonUtils.fromJson(Game.class, json, DefaultTyping.JAVA_LANG_OBJECT);
	}

	public GameView buildGameView(Player player) throws PlayerNotInGameException {

		GameView gameView = new GameView();
		gameView.setId(id);

		gameView.setPlayer(selectPlayer(player));
		gameView.setOpponents(selectOpponents(player));
		System.out.println("activePlayer : " + getActivePlayer());
		if (player.getAccount().getFacebookUserId().equals(getActivePlayer().getAccount().getFacebookUserId())) {
			gameView.setActivePlayer(true);
		} else {
			gameView.setActivePlayer(false);
		}

		gameView.setResourceCard1(resourceCard1);
		gameView.setResourceCard2(resourceCard2);

		gameView.setPlayerHand(player.getPlayerHand());
		gameView.setPlayerFieldAttack(player.getPlayerFieldAttack());
		gameView.setPlayerFieldDefense(player.getPlayerFieldDefense());

		gameView.setGameState(gameState);
		gameView.setTurn(turn);
		gameView.setPhase(phase);
		gameView.setGold(player.getGold());
		gameView.setTrade(trade);
		gameView.setDefense(defense);
		gameView.setFaith(faith);
		gameView.setAlchemy(alchemy);
		
		gameView.setToken(player.getToken());

		return gameView;

	}

	public void nextTurn() {

		turn += 1;
		resetPlayable();
		adjustResources();
		selectNextActivePlayer();

	}

	private void resetPlayable(PlayerField playerField) {

		for (PlayerFieldCard card : playerField.getCards()) {
			card.setPlayed(false);
		}

	}

	public void resetPlayable() {

		resetPlayable(activePlayer.getPlayerFieldAttack());
		
	}

	public void adjustResources() {

		for ( Player player : getPlayers() ) {
			player.adjustResources();
		}

	}
	
	public void selectNextActivePlayer() {
		
		int i = 0;
		
		for ( Player player : getPlayers() ) {
			if ( player == activePlayer ) {
				if ( i < players.size() - 1 ) {
					activePlayer = players.get(i + 1);
				}
				else {
					activePlayer = players.get(0);
				}
				break;
			}
			i++;
		}
		
	}

	public int getTrade() {
		return trade;
	}

	public void setTrade(int trade) {
		this.trade = trade;
	}

	public void addTrade(int trade) {
		this.trade += trade;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public void addDefense(int defense) {
		this.defense += defense;
	}

	public int getFaith() {
		return faith;
	}

	public void setFaith(int faith) {
		this.faith = faith;
	}

	public void addFaith(int faith) {
		this.faith += faith;
	}
	
	public int getAlchemy() {
		return alchemy;
	}

	public void setAlchemy(int alchemy) {
		this.alchemy = alchemy;
	}
	
	public void addAlchemy(int alchemy) {
		this.alchemy += alchemy;
	}

	public void setTransientFields(Game model) {
		
		setTurn(model.getTurn());
		setNumberOfPlayers(model.getNumberOfPlayers());
		setResourceDeck(model.getResourceDeck());
		setResourceCard1(model.getResourceCard1());
		setResourceCard2(model.getResourceCard2());
		setTrade(model.getTrade());
		setDefense(model.getDefense());
		setFaith(model.getFaith());
		setNextPhaseDelayActivated(model.isNextPhaseDelayActivated());
		setNextPhaseTimestamp(model.getNextPhaseTimestamp());
		
		for ( Player player : getPlayers() ) {
			
			for ( Player modelPlayer : model.getPlayers() ) {
				
				if ( player.getId() == modelPlayer.getId() ) {
					player.setTransientFields(modelPlayer);
				}
				
			}
			
		}
		
	}

}
