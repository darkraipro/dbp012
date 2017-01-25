package de.unidue.inf.is.domain;

public final class Beziehung {

    private String name;
    private String art;


    public Beziehung() {
    }


    public Beziehung(String name, String art) {
        this.name = name;
        this.art = art;

    }


    public String getName(){
    	return name;
    }

    public String getArt(){
    	return art;
    }
}