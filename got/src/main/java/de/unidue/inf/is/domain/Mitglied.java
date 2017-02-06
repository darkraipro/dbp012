package de.unidue.inf.is.domain;

public class Mitglied {
	private int cid, hid, epfromid, eptoid;
    String housename, epfromtitle, eptotitle;

	 public Mitglied() {
	 }


	 public Mitglied(int cid, int hid, int epfromid, int eptoid, String housename, String epfromtitle, String eptotitle) {
		 this.hid = hid;
		 this.cid = cid;
	    this.epfromid = epfromid;
	    this.eptoid = eptoid;
	    this.housename = housename;
	    this.epfromtitle = epfromtitle;
	    this.eptotitle = eptotitle;
	 }

	 public int getHid() {
	    return hid;
	 }

	 public int getCid() {
		    return cid;
		 }

	 public int getEpfromid() {
		    return epfromid;
		 }
	 public int getEptoid() {
		    return eptoid;
		 }
	 public String getHousename() {
	    return housename;
	 }
	 
	 public String	getEpfromtitle(){
	 	return epfromtitle;
	}
	 public String	getEptotitle(){
		 	return eptotitle;
		}
}
