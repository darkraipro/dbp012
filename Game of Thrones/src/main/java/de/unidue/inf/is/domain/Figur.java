package de.unidue.inf.is.domain;

public class Figur {
	
	 private String name;
	 private int birthplace;
	 private int	cid;
	 private String art;


	 public Figur() {
	 }


	 public Figur(String name, int birthplace, int cid, String art) {
	    this.birthplace = birthplace;
	    this.name = name;
	    this.cid = cid;
	    this.art = art;
	 }


	 public String getName() {
	    return name;
	 }

	 
	 public int	getBirthplace(){
	 	return birthplace;
	}
	
	 public int getCid(){
		 return cid;
	 }
	 
	 public String getArt(){
		 return art;
	 }
	 
}
