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
import de.unidue.inf.is.domain.Season;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailStaffelServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private List<Episode> listEpisoden = new ArrayList<>();



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
    	Episode ep;
    	String nummer="";
    	/*
    	Connection db2Conn = null;
		listEpisoden.clear();
		//String out = "";
		try {
			db2Conn = DBUtil.getConnection("got");
			final String sql1 = "SELECT * FROM Episodes, Season WHERE Episodes.sid = Season." + request.getParameter();
			PreparedStatement ps = db2Conn.prepareStatement(sql1);
			ResultSet rs = ps.executeQuery();
			//StringBuffer outb = new StringBuffer();
			int i = 0;
			while(rs.next() && i<5){
				int number = rs.getInt("number");
				int numberofe = rs.getInt("numberofe");
				Date date = rs.getDate("startdate");
				ep = new Episode(number, numberofe, date);
				listEpisoden.add(ep);
				//outb.append(name).append(" ").append(words).append(" ").append(seat).append("\n");
				i++;
			}
			//out = outb.toString();
			//System.out.println(out);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Resourcen schließen
			if (db2Conn != null) {
				try {
					db2Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}*/
        
        //SQL abfragen
        nummer = request.getParameter("name");
        //haus = sql where name=name
        //burg = sql where name=name
        
        
        
        //freemarker variablen setzen
        request.setAttribute("staffelepisoden", listEpisoden);
        request.setAttribute("staffelnummer", nummer);
    	
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_staffel.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
