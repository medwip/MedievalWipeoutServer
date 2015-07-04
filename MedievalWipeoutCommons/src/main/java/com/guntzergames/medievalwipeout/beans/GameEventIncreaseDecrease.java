package com.guntzergames.medievalwipeout.beans;


public class GameEventIncreaseDecrease extends GameEvent {
	
	private int quantity;
	private int index;
	private Target target;
	private PlayerDeckCard card;
	
	public enum Target {
		PLAYER_LIFE_POINTS,
		PLAYER_CURRENT_DEFENSE,
		PLAYER_CARD_LIFE_POINTS
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}
	
	public PlayerDeckCard getCard() {
		return card;
	}

	public void setCard(PlayerDeckCard card) {
		this.card = card;
	}

	public GameEventIncreaseDecrease duplicate() {
		GameEventIncreaseDecrease event = new GameEventIncreaseDecrease();
		event.setPlayerType(getPlayerType() == PlayerType.PLAYER ? PlayerType.OPPONENT : PlayerType.PLAYER);
		event.setQuantity(quantity);
		event.setTarget(target);
		event.setIndex(index);
		event.setCard(card);
		return event;
	}
	
}
