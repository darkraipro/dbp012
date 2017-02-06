package de.unidue.inf.is.domain;

public class Person {
	
	private int cid, birthplace;
	 private String title, bio, name;

	 public Person() {
	 }


	 public Person(int cid, int birthplace, String title, String bio, String name){
		 this.cid=cid;
		 this.birthplace = birthplace;
		 this.title = title;
		 this.bio = bio;
		 this.name = name;
	 }
	 
	 public int getCid(){
		 return cid;
	 }
	 
	 public int getBirthplace(){
		 return birthplace;
	 }
	 
	 public String getTitle(){
		 return title;
	 }
	 
	 public String getBio(){
		 return bio;
	 }
	 
	 public String getName(){
		 return name;
	 }
}
