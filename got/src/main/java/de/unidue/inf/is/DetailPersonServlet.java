package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Beziehung;
import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Gehört;
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Mitglied;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.Person;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailPersonServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
        Person person = null;
        Ort herkunft = null;
        List<Mitglied> listeHaeuser = new ArrayList<>();
        List<Beziehung> listeBeziehungen = new ArrayList<>();
        List<Figur> listeBesitzer = new ArrayList<>();
       
      //SQL abfragen
        Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");
			
			//Art laden und Auswählen ob Tier oder Person
			String sql = ("SELECT COUNT(animal.aid) as result "
					+ "FROM animal WHERE animal.aid = "+request.getParameter("cid"));
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if(rs.getInt("result")>0){
					//Weiterleiten falls Tier
					response.sendRedirect(("/detailtier?cid="+request.getParameter("cid")));
				}
			}
			//Person laden
			sql = ("SELECT characters.cid, characters.birthplace, person.title, person.biografie, characters.name, location.name as lname FROM person, characters, location WHERE location.lid = characters.birthplace AND person.pid = characters.cid AND characters.cid = ")+request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				person = new Person(rs.getInt("cid"), rs.getInt("birthplace"), rs.getString("title"), rs.getString("biografie"), rs.getString("name"));
				herkunft = new Ort(rs.getInt("birthplace"), rs.getString("lname"));
			}
			//Haeuser laden
			sql = ("SELECT houses.hid, houses.name, ep1.eid as eid1, ep2.eid as eid2, ep1.title as ename1, ep2.title as ename2 FROM houses, member_of, episodes as ep1, episodes as ep2 WHERE ep1.eid = member_of.episode_from AND ep2.eid = member_of.episode_to AND houses.hid = member_of.hid AND member_of.pid = ")+request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeHaeuser.add(new Mitglied(Integer.parseInt(request.getParameter("cid")), rs.getInt("hid"), rs.getInt("eid1"), rs.getInt("eid2"), rs.getString("name"), rs.getString("ename1"), rs.getString("ename2")));
			}
			//Beziehungen laden
			sql = ("SELECT characters.name, characters.cid, person_relationship.rel_type FROM characters, person_relationship WHERE characters.cid = person_relationship.targetpid AND person_relationship.sourcepid = ")+request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBeziehungen.add(new Beziehung(rs.getInt("cid"), rs.getString("name"), rs.getString("rel_type")));
			}
			//Tiere (Haustiere) laden
			sql = ("SELECT characters.name, characters.cid FROM characters, animal WHERE characters.cid = animal.aid AND animal.owner = ")+request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBesitzer.add(new Figur(rs.getInt("cid"), rs.getString("name")));
			}
		} catch (SQLException e) {e.printStackTrace();} 
		finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
        
        
        
        //freemarker variablen setzen
        request.setAttribute("person", person);
        request.setAttribute("herkunft", herkunft);
        request.setAttribute("personhaeuser", listeHaeuser);
        request.setAttribute("personbeziehungen", listeBeziehungen);
        request.setAttribute("personbesitzer", listeBesitzer);
    	
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_person.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
