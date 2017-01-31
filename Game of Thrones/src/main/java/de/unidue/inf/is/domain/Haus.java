package de.unidue.inf.is.domain;

public class Haus {
	 private String name;
	 private String words;
	 private int	seat;
	 private int hid;

	 public Haus() {
	 }


	 public Haus(int hid, String name, String words, int seat) {
		 this.hid = hid;
	    this.name = name;
	    this.words = words;
	    this.seat = seat;
	 }

	 public Haus(String name, String words, int seat) {
	    this.name = name;
	    this.words = words;
	    this.seat = seat;
	 }
	 
	 public Haus(int hid, String name) {
		    this.hid = hid;
		    this.name = name;
		 }

	 public String getName() {
	    return name;
	 }

	 public int getHid(){
		 return hid;
	 }

	 public String getWords() {
	    return words;
	 }
	 
	 public int	getSeat(){
	 	return seat;
	}
}
