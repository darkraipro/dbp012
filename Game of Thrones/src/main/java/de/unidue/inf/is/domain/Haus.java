package de.unidue.inf.is.domain;

public class Haus {
	 private String name;
	 private String words;
	 private int	seat;


	 public Haus() {
	 }


	 public Haus(String name, String words, int seat) {
	    this.name = name;
	    this.words = words;
	    this.seat = seat;
	 }


	 public String getName() {
	    return name;
	 }


	 public String getWords() {
	    return words;
	 }
	 
	 public int	getSeat(){
	 	return seat;
	}
}
