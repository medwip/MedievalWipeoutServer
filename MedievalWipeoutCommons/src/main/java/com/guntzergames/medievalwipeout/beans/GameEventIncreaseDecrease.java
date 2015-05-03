package com.guntzergames.medievalwipeout.beans;

public class GameEventIncreaseDecrease extends GameEvent {
	
	private int quantity;
	private Target target;
	
	public enum Target {
		PLAYER_LIFE_POINTS,
		PLAYER_CURRENT_DEFENSE
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}
	
}
