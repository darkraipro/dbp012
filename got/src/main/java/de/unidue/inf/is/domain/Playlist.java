package de.unidue.inf.is.domain;

public class Playlist {
	
	 private int	plid;
	 private String name;


	 public Playlist() {
	 }


	 public Playlist(int plid, String name) {
	    this.plid = plid;
	    this.name = name;
	 }

	 public String getName() {
	    return name;
	 }

	 public int getPlid(){
		 return plid;
	 }
	 
}
