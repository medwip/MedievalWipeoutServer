package com.guntzergames.medievalwipeout.beans;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.guntzergames.medievalwipeout.abstracts.AbstractCard;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class PlayerDeckCard extends AbstractCard {

	@JsonIgnore
	protected DeckTemplate deckTemplate;
	
	protected String drawableResourceName;
	protected String name;
	
	protected int attack;
	
	protected int lifePoints;
	protected int actualLifePoints;
	
	protected int goldCost;
	protected int faithCost;
	protected int alchemyCost;
	
	protected List<Skill> skills;
	
	protected boolean archer, defensor;

	public PlayerDeckCard(int id, String drawableResourceName, String name, int attack, int lifePoints) {
		this.id = id;
		this.drawableResourceName = drawableResourceName;
		this.name = name;
		this.attack = attack;
		this.lifePoints = lifePoints;
		this.actualLifePoints = lifePoints;
	}

	public PlayerDeckCard() {

	}

	public PlayerDeckCard(CardModel model) {
		this.id = model.getId();
		this.drawableResourceName = model.getDrawableResourceName();
		this.attack = model.getAttack();
		this.lifePoints = model.getLifePoints();
		this.actualLifePoints = model.getLifePoints();
		this.name = model.getName();
		this.goldCost = model.getGoldCost();
		this.faithCost = model.getFaithCost();
		this.alchemyCost = model.getAlchemyCost();
		this.archer = model.isArcher();
		this.defensor = model.isDefensor();
		this.skills = model.getSkills();
	}

	public PlayerDeckCard(PlayerDeckCard playerDeckCard) {
		this.id = playerDeckCard.getId();
		this.drawableResourceName = playerDeckCard.getDrawableResourceName();
		this.attack = playerDeckCard.getAttack();
		this.lifePoints = playerDeckCard.getLifePoints();
		this.actualLifePoints = playerDeckCard.getActualLifePoints();
		this.name = playerDeckCard.getName();
		this.goldCost = playerDeckCard.getGoldCost();
		this.faithCost = playerDeckCard.getFaithCost();
		this.alchemyCost = playerDeckCard.getAlchemyCost();
		this.archer = playerDeckCard.isArcher();
		this.defensor = playerDeckCard.isDefensor();
		this.skills = playerDeckCard.getSkills();
	}

	public DeckTemplate getDeckTemplate() {
		return deckTemplate;
	}

	public void setDeckTemplate(DeckTemplate deckTemplate) {
		this.deckTemplate = deckTemplate;
	}

	public String getDrawableResourceName() {
		return drawableResourceName;
	}

	public void setDrawableResourceName(String drawableResourceName) {
		this.drawableResourceName = drawableResourceName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getLifePoints() {
		return lifePoints;
	}

	public void setLifePoints(int lifePoints) {
		this.lifePoints = lifePoints;
	}

	public int getActualLifePoints() {
		return actualLifePoints;
	}

	public void setActualLifePoints(int actualLifePoints) {
		this.actualLifePoints = actualLifePoints;
	}

	public int getGoldCost() {
		return goldCost;
	}

	public void setGoldCost(int goldCost) {
		this.goldCost = goldCost;
	}

	public int getFaithCost() {
		return faithCost;
	}

	public void setFaithCost(int faithCost) {
		this.faithCost = faithCost;
	}

	public int getAlchemyCost() {
		return alchemyCost;
	}

	public void setAlchemyCost(int alchemyCost) {
		this.alchemyCost = alchemyCost;
	}

	public boolean isArcher() {
		return archer;
	}

	public void setArcher(boolean archer) {
		this.archer = archer;
	}

	public boolean isDefensor() {
		return defensor;
	}

	public void setDefensor(boolean defensor) {
		this.defensor = defensor;
	}

	public List<Skill> getSkills() {
		return skills;
	}

	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}

	@Override
	public String toString() {
		return String.format("[%s] %s: Attack = %s, Life Points = %s", this.getClass().getSimpleName(), name, attack, lifePoints);
	}

}
