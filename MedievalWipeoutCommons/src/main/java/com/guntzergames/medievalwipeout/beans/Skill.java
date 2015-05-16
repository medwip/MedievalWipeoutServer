package com.guntzergames.medievalwipeout.beans;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.guntzergames.medievalwipeout.enums.Phase;
import com.guntzergames.medievalwipeout.enums.SkillPhase;

@Entity
@Table(name = "SKILL")
public class Skill {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ID")
	private int id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PHASE")
	private Phase phase;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SKILL_PHASE")
	private SkillPhase skillPhase;
	
	@Column(name = "SKILL_CODE")
	private String skillCode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public SkillPhase getSkillPhase() {
		return skillPhase;
	}

	public void setSkillPhase(SkillPhase skillPhase) {
		this.skillPhase = skillPhase;
	}

	public String getSkillCode() {
		return skillCode;
	}

	public void setSkillCode(String skillCode) {
		this.skillCode = skillCode;
	}

}