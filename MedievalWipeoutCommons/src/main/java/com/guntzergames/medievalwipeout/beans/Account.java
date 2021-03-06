package com.guntzergames.medievalwipeout.beans;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;

import com.guntzergames.medievalwipeout.exceptions.JsonException;
import com.guntzergames.medievalwipeout.utils.JsonUtils;

@Entity
@Table(name = "ACCOUNT")
@NamedQueries({ @NamedQuery(name = Account.NQ_FIND_BY_FACEBOOK_USER_ID, query = "SELECT a FROM Account a WHERE a.facebookUserId = :facebookUserId") })
public class Account {

	public final static String NQ_FIND_BY_FACEBOOK_USER_ID = "NQ_FIND_ACCOUNT_BY_FACEBOOK_USER_ID";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private long id;

	@Column(name = "FACEBOOK_USER_ID")
	private String facebookUserId;
	
	@Column(name = "LEVEL")
	private int level;

	@Column(name = "BOT")
	private boolean bot;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
	private List<DeckTemplate> deckTemplates;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<CollectionElement> collectionElements;

	public String getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isBot() {
		return bot;
	}

	public void setBot(boolean bot) {
		this.bot = bot;
	}

	public List<DeckTemplate> getDeckTemplates() {
		return deckTemplates;
	}

	public List<CollectionElement> getCollectionElements() {
		return collectionElements;
	}

	public void setCollectionElements(List<CollectionElement> collectionElements) {
		this.collectionElements = collectionElements;
	}

	public void setDeckTemplates(List<DeckTemplate> deckTemplates) {
		this.deckTemplates = deckTemplates;
	}

	public static Account fromJson(String json) throws JsonException {
		return JsonUtils.fromJson(Account.class, json, DefaultTyping.JAVA_LANG_OBJECT);
	}
	
	public String toJson() throws JsonException {
		return JsonUtils.toJson(this, DefaultTyping.JAVA_LANG_OBJECT);
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", id, facebookUserId, bot);
	}

}
