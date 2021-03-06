package com.guntzergames.medievalwipeout.managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.guntzergames.medievalwipeout.beans.Account;
import com.guntzergames.medievalwipeout.beans.CardModel;
import com.guntzergames.medievalwipeout.beans.CollectionElement;
import com.guntzergames.medievalwipeout.beans.DeckTemplate;
import com.guntzergames.medievalwipeout.beans.Packet;
import com.guntzergames.medievalwipeout.beans.Player;
import com.guntzergames.medievalwipeout.exceptions.PlayerNotInGameException;
import com.guntzergames.medievalwipeout.persistence.AccountDao;
import com.guntzergames.medievalwipeout.views.GameView;

@Stateless
public class AccountManager {
	
	private final static Logger LOGGER = Logger.getLogger(AccountManager.class);
	
	@EJB
	private AccountDao accountDao;
	
	public Account getAccount(String facebookUserId) {
		
		Account account = accountDao.findByFacebookUserId(facebookUserId);
		
		LOGGER.info("Account: " + account);
		LOGGER.info("Templates: " + account.getDeckTemplates());
		
		return account;
		
	}
	
	private List<CollectionElement> drawCardsFromList(Account account, List<CardModel> cardModels, int len, Random generator) {
		
		List<CollectionElement> collectionElements = new ArrayList<CollectionElement>();
		
		for ( int i = 0; i < len; i++ ) {
			CardModel cardModel = cardModels.get(generator.nextInt(len));
			LOGGER.info("Account before addCollectionElement " + account);
			CollectionElement collectionElement = accountDao.addCollectionElement(account, cardModel);
			collectionElements.add(collectionElement);
		}
		
		return collectionElements;
		
	}
	
	public Packet drawPacket(int numberOfCards, Account account) {
		
		Packet packet = new Packet();
		
		List<CardModel> cardModels = findCardModelsNotInCollection(account);
		Random generator = new Random();
		int s = cardModels.size();
		int len = Math.min(numberOfCards, s);
		LOGGER.info(String.format("number of cards not in collection: %d", s));
		
		packet.setElementsNotInCollection(drawCardsFromList(account, cardModels, len, generator));
		
		if ( s < numberOfCards ) {
			
			cardModels = findCardModelsForAccount(account);
			len = numberOfCards - s;
			packet.setElementsAlreadyInCollection(drawCardsFromList(account, cardModels, len, generator));
			
		}
		else {
			
			packet.setElementsAlreadyInCollection(new ArrayList<CollectionElement>());
			
		}
		
		return packet;
		
	}
	
	public void addDeckTemplateElement(CollectionElement collectionElement, DeckTemplate deckTemplate) {
		accountDao.addDeckTemplateElement(collectionElement, deckTemplate);
	}
	
	public List<CardModel> findAllCardModels() {
		return accountDao.findAllCardModels();
	}
	
	public DeckTemplate findDeckTemplateById(long id) {
		return accountDao.findDeckTemplateById(id);
	}
	
	public List<Player> findPlayersByAccount(Account account) {
		return accountDao.findPlayersByAccount(account);
	}
	
	public List<GameView> findGameViewsByAccount(Account account) {
		
		List<GameView> games = new ArrayList<GameView>();
		
		for ( Player player : findPlayersByAccount(account) ) {
			try {
				games.add(player.getGame().buildGameView(player));
			} catch (PlayerNotInGameException e) {
				LOGGER.warn("Was not able to select player...");
			}
		}
		
		return games;
		
	}
	
	public CollectionElement findCollectionElementById(long id) {
		return accountDao.findCollectionElementById(id);
	}
	
	public List<CardModel> findCardModelsForAccount(Account account) {
		return accountDao.findCardModelsForAccount(account);
	}
	
	public Set<CardModel> findCardModelsInCollection(Account account) {
		
		List<CollectionElement> elements = accountDao.findCollectionElementsByAccount(account);
		Set<CardModel> collection = new HashSet<CardModel>();
		for ( CollectionElement element : elements ) {
			collection.add(element.getCardModel());
		}
		return collection;
		
	}
	
	public List<CardModel> findCardModelsNotInCollection(Account account) {
		
		List<CardModel> cardModels = findCardModelsForAccount(account);
		Set<CardModel> collection = findCardModelsInCollection(account);
		cardModels.removeAll(collection);
		
		return cardModels;
		
	}
	
}
