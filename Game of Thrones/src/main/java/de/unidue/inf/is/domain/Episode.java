package de.unidue.inf.is.domain;

import java.sql.Date;

public class Episode {
	private String summary;
	private Date releaseDate;
	private String title;
	private int sid;
	private int eid;
	private int number;
	
	public Episode(){}
	
	public Episode(int eid, int number, String title, String summary, Date releaseDate, int sid){
		
		this.eid = eid;
		this.number = number;
		this.title = title;
		this.summary = summary;
		this.releaseDate = releaseDate;
		this.sid = sid;
	}
	
public Episode(int eid, String title){
		
		this.eid = eid;
		this.title = title;
	}
	
	public int getEid(){
		return eid;
	}
	
	public int getNumber(){
		return number;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getSummary(){
		return summary;
	}
	
	public Date getReleaseDate(){
		return releaseDate;
	}
	
	public int getSid(){
		return sid;
	}
	
}
