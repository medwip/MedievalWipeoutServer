package com.guntzergames.medievalwipeout.beans;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.enums.SkillPhase;

@Entity
@Table(name = "PLAYER")
@NamedQueries({ @NamedQuery(name = Player.NQ_FIND_BY_ACCOUNT, query = "SELECT p FROM Player p WHERE p.deckTemplate.account = :account"), })
public class Player {

	public final static String NQ_FIND_BY_ACCOUNT = "NQ_FIND_PLAYERS_BY_ACCOUNT";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private long id;

	@ManyToOne(targetEntity = DeckTemplate.class)
	@JoinColumn(name = "DECK_TEMPLATE_KEY")
	private DeckTemplate deckTemplate;

	@JsonIgnore
	@ManyToOne(targetEntity = Game.class, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "GAME_KEY")
	private Game game;

	@Transient
	private PlayerDeck playerDeck = new PlayerDeck();
	@Transient
	private PlayerDeck initialPlayerDeck = new PlayerDeck();
	@Transient
	private PlayerHand playerHand = new PlayerHand();
	@Transient
	private PlayerField playerFieldAttack = new PlayerField(), playerFieldDefense = new PlayerField();
	@Transient
	private PlayerDiscard playerDiscard = new PlayerDiscard();
	@Transient
	private PlayerDeckCard playerDeckCard1, playerDeckCard2;
	@Transient
	private LinkedList<GameEvent> events = new LinkedList<GameEvent>();
	@Transient
	private int lifePoints;
	@Transient
	private int trade;
	@Transient
	private int gold;
	@Transient
	private int defense;
	@Transient
	private int currentDefense;
	@Transient
	private int faith;
	@Transient
	private int alchemy;
	
	@Transient
	private Token token;

	@JsonIgnore
	public Account getAccount() {
		return deckTemplate != null ? deckTemplate.getAccount() : null;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public PlayerDeck getPlayerDeck() {
		return playerDeck;
	}

	public void setPlayerDeck(PlayerDeck deck) {
		this.playerDeck = deck;
	}

	public PlayerDeck getInitialPlayerDeck() {
		return initialPlayerDeck;
	}

	public void setInitialPlayerDeck(PlayerDeck initialPlayerDeck) {
		this.initialPlayerDeck = initialPlayerDeck;
	}

	public PlayerHand getPlayerHand() {
		return playerHand;
	}

	public void setPlayerHand(PlayerHand hand) {
		this.playerHand = hand;
	}

	public PlayerDiscard getPlayerDiscard() {
		return playerDiscard;
	}

	public void setPlayerDiscard(PlayerDiscard discard) {
		this.playerDiscard = discard;
	}

	public DeckTemplate getDeckTemplate() {
		return deckTemplate;
	}

	public void setDeckTemplate(DeckTemplate deckTemplate) {
		this.deckTemplate = deckTemplate;
	}

	public int getLifePoints() {
		return lifePoints;
	}

	public void setLifePoints(int lifePoints) {
		this.lifePoints = lifePoints;
	}

	public void removeLifePoints(int lifePoints) {
		this.lifePoints -= lifePoints;
	}

	public PlayerField getPlayerFieldAttack() {
		return playerFieldAttack;
	}

	public void setPlayerFieldAttack(PlayerField playerFieldAttack) {
		this.playerFieldAttack = playerFieldAttack;
	}

	public PlayerField getPlayerFieldDefense() {
		return playerFieldDefense;
	}

	public void setPlayerFieldDefense(PlayerField playerFieldDefense) {
		this.playerFieldDefense = playerFieldDefense;
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

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void addGold(int gold) {
		this.gold += gold;
	}

	public void removeGold(int gold) {
		this.gold -= gold;
		if (gold < 0) {
			gold = 0;
		}
	}

	public int getCurrentDefense() {
		return currentDefense;
	}

	public void setCurrentDefense(int currentDefense) {
		this.currentDefense = currentDefense;
	}

	public void removeCurrentDefense(int currentDefense) {
		this.currentDefense -= currentDefense;
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

	public PlayerDeckCard getPlayerDeckCard1() {
		return playerDeckCard1;
	}

	public void setPlayerDeckCard1(PlayerDeckCard playerDeckCard1) {
		this.playerDeckCard1 = playerDeckCard1;
	}

	public PlayerDeckCard getPlayerDeckCard2() {
		return playerDeckCard2;
	}

	public void setPlayerDeckCard2(PlayerDeckCard playerDeckCard2) {
		this.playerDeckCard2 = playerDeckCard2;
	}

	public LinkedList<GameEvent> getEvents() {
		return events;
	}

	public boolean hasSameAccount(Player player) {
		return player != null && getAccount().getFacebookUserId().equals(player.getAccount().getFacebookUserId());
	}

	public void setEvents(LinkedList<GameEvent> events) {
		this.events = events;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public List<Skill> getSkills(Phase phase, SkillPhase skillPhase) {
		
		List<Skill> skills = new ArrayList<Skill>();

		for (PlayerDeckCard playerDeckCard : getPlayerFieldAttack().getCards()) {

			for (Skill skill : playerDeckCard.getSkills()) {

				if (skill.getPhase() == phase && skill.getSkillPhase() == skillPhase) {
					skills.add(skill);
				}

			}

		}
		
		return skills;
		
	}

	public void setTransientFields(Player model) {

		setPlayerDeck(model.getPlayerDeck());
		setInitialPlayerDeck(model.getInitialPlayerDeck());
		setPlayerHand(model.getPlayerHand());
		setPlayerFieldAttack(model.getPlayerFieldAttack());
		setPlayerDiscard(model.getPlayerDiscard());
		setPlayerDeckCard1(model.getPlayerDeckCard1());
		setPlayerDeckCard2(model.getPlayerDeckCard2());
		setEvents(model.getEvents());
		setLifePoints(model.getLifePoints());
		setTrade(model.getTrade());
		setGold(model.getGold());
		setDefense(model.getDefense());
		setCurrentDefense(model.getCurrentDefense());
		setFaith(model.getFaith());
		setToken(model.getToken());

	}

	public void updatePlayableHandCards() {
		for (PlayerHandCard card : getPlayerHand().getCards()) {
			card.setPlayableFromPlayer(this);
		}
	}

	public void adjustResources() {

		gold += trade;
		currentDefense = defense;

	}

	@Override
	public String toString() {
		return String.format("Id: %s, account: %s", id, getAccount() != null ? getAccount().getFacebookUserId() : "null");
	}

}
