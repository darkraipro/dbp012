package de.unidue.inf.is;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Episode;
import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailEpisodeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        	
    	//Variablen init
    	Episode episode = new Episode();
        List<Figur> listeFiguren = new ArrayList<>();
        List<Ort> listeOrte = new ArrayList<>();
        
        //SQL abfragen
        Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");
			
			//Episode laden
			String sql = ("SELECT * FROM episodes WHERE eid = ")+request.getParameter("eid");
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				episode = new Episode(rs.getInt("eid"), rs.getInt("number"), rs.getString("title"),
										rs.getString("summary"), rs.getDate("releasedate"), rs.getInt("sid"));
			}
			//Figuren laden
			sql = ("SELECT characters.cid, characters.name, characters.birthplace FROM characters, char_for_epi WHERE characters.cid = char_for_epi.cid AND "
					+ " char_for_epi.eid = ")+request.getParameter("eid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeFiguren.add(new Figur(rs.getString("name"), 0, rs.getInt("cid")));
			}
			//Orte laden
			sql = ("SELECT location.lid, location.name FROM location, loc_for_epi WHERE location.lid = loc_for_epi.lid AND "
					+ " loc_for_epi.eid = ")+request.getParameter("eid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeOrte.add(new Ort(rs.getInt("lid"),rs.getString("name")));
			}
		} catch (SQLException e) {e.printStackTrace();} 
		finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
        
        //freemarker variablen setzen
        request.setAttribute("episodeid", episode.getEid());
        request.setAttribute("episodetitel", episode.getTitle());
        request.setAttribute("episodenummer", episode.getNumber());
        request.setAttribute("episodestaffel", episode.getSid());
        request.setAttribute("episodehandlung", episode.getSummary());
        request.setAttribute("episodefiguren", listeFiguren);
        request.setAttribute("episodeorte", listeOrte);
        
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_episode.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
