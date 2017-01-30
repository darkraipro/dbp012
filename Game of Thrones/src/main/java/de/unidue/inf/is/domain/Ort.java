package de.unidue.inf.is.domain;

public class Ort {
	
	 private int	lid;
	 private String name;


	 public Ort() {
	 }


	 public Ort(int lid, String name) {
	    this.lid = lid;
	    this.name = name;
	 }

	 public String getName() {
	    return name;
	 }

	 public int getLid(){
		 return lid;
	 }
	 
}
