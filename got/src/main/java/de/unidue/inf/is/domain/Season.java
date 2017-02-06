package de.unidue.inf.is.domain;
import java.sql.Date;

public class Season {
	
	private int number;
	private int numberofe;
	private Date date;
	private int sid;

	public Season(){}
	
	public Season(int numberSeason, int numberofe, Date date, int sid){
		this.numberofe = numberofe;
		this.number = numberSeason;
		this.date = date;
		this.sid = sid;
	}
	
	public Season(int sid, int numberSeason){
		this.number = numberSeason;
		this.sid = sid;
	}
	
	public int getNumber(){
		return number;
	}
	
	public int getNumberOfE(){
		return numberofe;
	}
	
	public Date getDate(){
		return date;
	}
	
	public int getSid(){
		return sid;
	}
	
}
