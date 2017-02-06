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
import de.unidue.inf.is.domain.Mitglied;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.Person;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailTierServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
    	Figur tier = null;
    	Figur besitzer = null;
    	Ort herkunft = null;
        
    	////////////////////////////////////////////////////////////////////////
    	//////////////////////// UNGETESTETER BEREICH //////////////////////////
    	////////////////////////////////////////////////////////////////////////
    	
    	
    	//SQL abfragen
        Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");
			
			//Tier laden
			String sql = ("SELECT characters.name FROM characters "
					+ "WHERE characters.cid = "+request.getParameter("cid"));
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
					tier = new Figur(Integer.parseInt(request.getParameter("cid")), rs.getString("name"));
				
			}
			//Besitzer laden
			sql = ("SELECT characters.name, characters.cid FROM characters, animal "
					+ "WHERE characters.cid = animal.owner AND animal.aid = "+request.getParameter("cid"));
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				besitzer = new Figur(rs.getInt("cid"), rs.getString("name"));
			}
			//Herkunftsort laden
			sql = ("SELECT location.name, location.lid FROM characters, location "
					+ "WHERE location.lid = characters.birthplace AND characters.cid = "+request.getParameter("cid"));
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				herkunft = new Ort(rs.getInt("lid"), rs.getString("name"));
			}
		} catch (SQLException e) {e.printStackTrace();} 
		finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
        
		////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
        
        
        //freemarker variablen setzen
        request.setAttribute("tier", tier);
        request.setAttribute("tierherkunft", herkunft);
        request.setAttribute("tierbesitzer", besitzer);
        
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_tier.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
