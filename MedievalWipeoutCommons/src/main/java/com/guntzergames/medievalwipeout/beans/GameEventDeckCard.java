package com.guntzergames.medievalwipeout.beans;


public class GameEventDeckCard extends GameEvent {

	private PlayerDeckCard playerDeckCard;
	private int resourceId;
	
	public GameEventDeckCard() {
		super();
	}

	public GameEventDeckCard(GameEvent.PlayerType playerType) {
		super(playerType);
	}
	
	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public PlayerDeckCard getPlayerDeckCard() {
		return playerDeckCard;
	}

	public void setPlayerDeckCard(PlayerDeckCard playerDeckCard) {
		this.playerDeckCard = playerDeckCard;
	}

	public GameEventDeckCard duplicate() {
		GameEventDeckCard event = new GameEventDeckCard();
		event.setPlayerType(getPlayerType() == PlayerType.PLAYER ? PlayerType.OPPONENT : PlayerType.PLAYER);
		event.setResourceId(resourceId);
		event.setPlayerDeckCard(playerDeckCard);
		return event;
	}

}
