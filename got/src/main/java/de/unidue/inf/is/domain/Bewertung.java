package de.unidue.inf.is.domain;

public class Bewertung {
	private double avgrating;
	private String username;
	private int rating;
	private String text;
	
	public Bewertung(){
		this.avgrating = 0;
	}
	public Bewertung(double avgrating){
		this.avgrating = avgrating;
	}
	
	public Bewertung(String username,int rating,String text){
		this.username = username;
		this.rating = rating;
		this.text = text;
	}
	
	
	public double getAvgrating(){
		return avgrating;
	}
	
	public void setAvgrating(double avg){
		this.avgrating = avg;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getText(){
		return text;
	}
	
	public int getRating(){
		return rating;
	}
}
