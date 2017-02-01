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

import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.Season;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class StartseiteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	final String databaseToCheck = "got";
		boolean databaseExists = DBUtil.checkDatabaseExists(databaseToCheck);
		
		//init
		List<Figur> listeFiguren = new ArrayList();
		List<Haus> listeHaeuser = new ArrayList();
		List<Season> listeStaffeln = new ArrayList();

		
		//SQL abfragen
        Connection db2Conn = null;
        String sql = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int anzahlTestFiguren = 5;
        int anzahlTestHaeuser = 5;
        int anzahlTestStaffeln = 5;
		try {
			db2Conn = DBUtil.getConnection("got");
			
			//Anzahl Figuren prüfen
			sql = ("SELECT COUNT(characters.cid) as result "
					+ "FROM characters");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				if(rs.getInt("result")<5)anzahlTestFiguren=rs.getInt("result");
			}
			//Anzahl Haeuser prüfen
			sql = ("SELECT COUNT(houses.hid) as result "
					+ "FROM houses");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				if(rs.getInt("result")<5)anzahlTestHaeuser=rs.getInt("result");
			}
			//Anzahl Staffeln prüfen
			sql = ("SELECT COUNT(season.sid) as result "
					+ "FROM season");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				if(rs.getInt("result")<5)anzahlTestStaffeln=rs.getInt("result");
			}
			
			
			//Figuren laden
			for(int i=0; i<anzahlTestFiguren; i++){
				sql = ("SELECT characters.name, characters.cid FROM characters "
						+ "WHERE characters.cid = "+i);
				ps = db2Conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					listeFiguren.add(new Figur(rs.getInt("cid"), rs.getString("name")));
				}
			}
			//Haeuser laden
			for(int i=0; i<anzahlTestHaeuser; i++){
				sql = ("SELECT houses.name, houses.hid FROM houses "
						+ "WHERE houses.hid = "+i);
				ps = db2Conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					listeHaeuser.add(new Haus(rs.getInt("hid"), rs.getString("name")));
				}
			}
			//Staffeln laden
			for(int i=0; i<anzahlTestStaffeln; i++){
				sql = ("SELECT season.number, season.sid FROM season "
						+ "WHERE season.sid = "+i);
				ps = db2Conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					listeStaffeln.add(new Season(rs.getInt("sid"), rs.getInt("number")));
				}
			}
			
		} catch (SQLException e) {e.printStackTrace();} 
		finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}

		//Vorschau in ftl setzen
		request.setAttribute("vorschaufiguren", listeFiguren);
		request.setAttribute("vorschauhaeuser", listeHaeuser);
		request.setAttribute("vorschaustaffeln", listeStaffeln);
		if (databaseExists) {request.setAttribute("db2exists", "");}else {request.setAttribute("db2exists", "nicht vorhanden :-(");}
        request.getRequestDispatcher("startseite.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
    	String[] forbidden = {"\0","\'", "\"", "\b", "\n","\r","\t","\\", "%", "_"};
    	response.sendRedirect("start");
        if (request.getParameter("btn_suchfigur") != null) {
        	String textfeld = request.getParameter("txt_suchfigur");
        	boolean legit = true;
        	for(String f: forbidden){
        		if(textfeld.contains(f))legit=false;
        	}
        	if(legit){
        		Connection db2Conn = null;
                String sql = "";
                PreparedStatement ps = null;
                ResultSet rs = null;
                
                try {
                sql = ("SELECT characters.cid FROM characters WHERE characters.name = "+textfeld);
    			ps = db2Conn.prepareStatement(sql);
    			rs = ps.executeQuery();
    			while (rs.next()) {
    				if(rs.getInt("result")>0){
    					response.sendRedirect(("/detailperson?cid="+request.getParameter("cid")));
    				}
    			}
    			
                } catch (SQLException e) {e.printStackTrace();} 
        		finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
        	}else{
        		
        	}
        	
        }
        

        
    }
}
