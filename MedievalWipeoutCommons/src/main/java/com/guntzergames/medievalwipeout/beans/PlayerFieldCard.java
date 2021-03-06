package com.guntzergames.medievalwipeout.beans;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.guntzergames.medievalwipeout.beans.GameEvent.PlayerType;

public class PlayerFieldCard extends PlayerDeckCard {
	
	private boolean played = false;
	private int currentLifePoints;
	private Location location;
	
	public enum Location {ATTACK, DEFENSE};
	
	public PlayerFieldCard() {
		
	}

	public PlayerFieldCard(PlayerDeckCard playerDeckCard) {
		super(playerDeckCard);
		this.currentLifePoints = this.actualLifePoints;
		this.played = false;
	}

	public PlayerFieldCard(PlayerFieldCard playerFieldCard) {
		this((PlayerDeckCard)playerFieldCard);
		this.currentLifePoints = playerFieldCard.getLifePoints();
		this.location = playerFieldCard.getLocation();
	}

	public boolean isPlayed() {
		return played;
	}

	public void setPlayed(boolean played) {
		this.played = played;
	}

	public int getCurrentLifePoints() {
		return currentLifePoints;
	}
	
	public void removeCurrentLifePoints(int lifePoints) {
		currentLifePoints -= lifePoints;
		if ( currentLifePoints < 0 ) {
			currentLifePoints = 0;
		}
	}

	public void setCurrentLifePoints(int currentLifePoints) {
		this.currentLifePoints = currentLifePoints;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	@JsonIgnore
	public String getField(PlayerType playerType) {
		
		switch ( playerType ) {
			case OPPONENT:
				return location == Location.DEFENSE ? "opponentFieldDefense" : "opponentFieldAttack";
			case PLAYER:
				return location == Location.DEFENSE ? "playerFieldDefense" : "playerFieldAttack";
			default:
				return "";
		}
		
	}
	
}
