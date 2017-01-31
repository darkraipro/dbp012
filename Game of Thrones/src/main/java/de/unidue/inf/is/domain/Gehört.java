package de.unidue.inf.is.domain;

public final class Gehört {

    private int lid, hid, epfromid, eptoid;
    String housename, epfromtitle, eptotitle, locationname;

    public Gehört() {
    }


    public Gehört(int lid, int hid, String housename, int epfromid, String epfromtitle, int eptoid, String eptotitle) {
        this.lid = lid;
        this.hid = hid;
        this.housename = housename;
        this.epfromid = epfromid;
        this.eptoid = eptoid;
        this.epfromtitle = epfromtitle;
        this.eptotitle = eptotitle;
    }
    
    public Gehört(int lid, int hid, String housename, int epfromid, String epfromtitle, int eptoid, String eptotitle, String locationname) {
        this.lid = lid;
        this.hid = hid;
        this.housename = housename;
        this.epfromid = epfromid;
        this.eptoid = eptoid;
        this.epfromtitle = epfromtitle;
        this.eptotitle = eptotitle;
        this.locationname = locationname;
    }


    public int getLid(){
    	return lid;
    }
    
    public int getHid(){
    	return hid;
    }
    
    public String getHousename(){
    	return housename;
    }
    
    public int getEpfromid(){
    	return epfromid;
    }
    
    public String getLocationname(){
    	return locationname;
    }
    
    public int getEptoid(){
    	return eptoid;
    }
    
    public String getEpfromtitle(){
    	return epfromtitle;
    }
    
    public String getEptotitle(){
    	return eptotitle;
    }
}