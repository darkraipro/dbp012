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

import de.unidue.inf.is.domain.Haus;
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
    	int episode, nummer, staffel;
    	episode=nummer=staffel=0;
        String titel, handlung;
        titel=handlung="";
        List<String> listeFiguren = new ArrayList<>();
        List<String> listeOrte = new ArrayList<>();
        
      //TEST
        episode=-1;
        nummer=-1;
        staffel=-1;
        handlung="Keine Handlung";
        
        //SQL abfragen
        titel = request.getParameter("titel");
        Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");
			final String sql1 = ("SELECT eid, number, sid, summary FROM episodes WHERE title='"+titel+"'");
			System.out.println(sql1);
			PreparedStatement ps = db2Conn.prepareStatement(sql1);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				episode = rs.getInt("eid");
				nummer = rs.getInt("nummer");
				staffel = rs.getInt("sid");
				handlung = rs.getString("summary");
				System.out.println(staffel);
			}
		} catch (SQLException e) {
			System.out.println("SQL FEHLER");
			e.printStackTrace();
		} finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
        
        
        
        
        //freemarker variablen setzen
        request.setAttribute("episodeid", episode);
        request.setAttribute("episodetitel", titel);
        request.setAttribute("episodenummer", nummer);
        request.setAttribute("episodestaffel", staffel);
        request.setAttribute("episodehandlung", handlung);
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
