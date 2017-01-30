package de.unidue.inf.is.domain;

public final class Beziehung {

    private String name;
    private String art;
    private int target;

    public Beziehung() {
    }


    public Beziehung(int target, String name, String art) {
    	this.target = target;
        this.name = name;
        this.art = art;

    }

    public int getTarget(){
    	return target;
    }

    public String getName(){
    	return name;
    }

    public String getArt(){
    	return art;
    }
}