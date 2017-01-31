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

import de.unidue.inf.is.domain.Episode;
import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Gehört;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.Playlist;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailPlaylistServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
        Playlist playlist = null;
        List<Episode> listeEpisoden = new ArrayList<>();
        
        
    	////////////////////////////////////////////////////////////////////////
    	//////////////////////// UNGETESTETER BEREICH //////////////////////////
    	////////////////////////////////////////////////////////////////////////
        
      //SQL abfragen
        Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");
			
			//Playlist laden
			String sql = ("SELECT playlist.name FROM playlist WHERE plid = ")+request.getParameter("plid");
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				playlist = new Playlist(Integer.parseInt(request.getParameter("plid")), rs.getString("name"));
			}
			//Episoden laden
			sql = ("SELECT episodes.name, episodes.eid FROM episodes, playlist_contains_episode "
					+ "WHERE episodes.eid = playlist_contains_episode.eid AND playlist_contains_episode.plid = ")+request.getParameter("plid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeEpisoden.add(new Episode(rs.getInt("eid"), rs.getString("name")));
			}
		} catch (SQLException e) {e.printStackTrace();} 
		finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
        
		////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
        
        
        //freemarker variablen setzen
        request.setAttribute("playlist", playlist);
        request.setAttribute("playlistepisoden", listeEpisoden);
    	
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_playlist.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
