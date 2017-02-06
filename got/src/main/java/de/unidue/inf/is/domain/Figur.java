package de.unidue.inf.is.domain;

public class Figur {
	
	 private String name;
	 private int birthplace;
	 private int	cid;
	 private String art;
	 private String birth;


	 public Figur() {
	 }


	 public Figur(String name, int birthplace, int cid, String art) {
	    this.birthplace = birthplace;
	    this.name = name;
	    this.cid = cid;
	    this.art = art;
	 }
	 
	 public Figur(String name, String birth, int cid, String art) {
		    this.birth = birth;
		    this.name = name;
		    this.cid = cid;
		    this.art = art;
		 }

	 public Figur(String name, int birthplace, int cid) {
		    this.birthplace = birthplace;
		    this.name = name;
		    this.cid = cid;
		    this.art = "";
		 }

	 public Figur(int cid, String name) {
		    this.name = name;
		    this.cid = cid;
		 }
	 
	 public String getName() {
	    return name;
	 }

	 public String getBirth(){
		 return birth;
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
