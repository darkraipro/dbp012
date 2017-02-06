package de.unidue.inf.is.domain;

public final class GehörtAn {

    private int cid, hid, epfromid, eptoid;
    String charname, epfromtitle, eptotitle;

    public GehörtAn() {
    }


    public GehörtAn(int cid, int hid, String charname, int epfromid, String epfromtitle, int eptoid, String eptotitle) {
        this.cid = cid;
        this.hid = hid;
        this.charname = charname;
        this.epfromid = epfromid;
        this.eptoid = eptoid;
        this.epfromtitle = epfromtitle;
        this.eptotitle = eptotitle;
    }
    
    public GehörtAn(int cid, int hid, String housename, int epfromid, String epfromtitle, int eptoid, String eptotitle, String charname) {
        this.cid = cid;
        this.hid = hid;
        this.charname = housename;
        this.epfromid = epfromid;
        this.eptoid = eptoid;
        this.epfromtitle = epfromtitle;
        this.eptotitle = eptotitle;
        this.charname = charname;
    }


    public int getCid(){
    	return cid;
    }
    
    public int getHid(){
    	return hid;
    }
    
    public String getCharname(){
    	return charname;
    }
    
    public int getEpfromid(){
    	return epfromid;
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