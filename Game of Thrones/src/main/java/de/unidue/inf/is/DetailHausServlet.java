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
import javax.servlet.http.HttpSession;

import de.unidue.inf.is.domain.Episode;
import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Gehört;
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailHausServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
        List<Haus> haus = new ArrayList<>();
        List<Ort> sitz = new ArrayList<>();
        List<Gehört> listeBesitz = new ArrayList<>();
        
    	////////////////////////////////////////////////////////////////////////
    	//////////////////////// UNGETESTETER BEREICH //////////////////////////
    	////////////////////////////////////////////////////////////////////////
        
      //SQL abfragen
        Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");
			
			//Haus laden
			String sql = ("SELECT houses.name FROM houses WHERE houses.hid = ")+request.getParameter("hid");
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				haus.add(new Haus(Integer.parseInt(request.getParameter("hid")), rs.getString("name")));
			}
			//Sitz laden
			sql = ("SELECT location.name, location.lid FROM houses, location WHERE location.lid = houses.seat AND houses.hid = ")+request.getParameter("hid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				sitz.add(new Ort(rs.getInt("lid"), rs.getString("name")));
			}
			//Besitz laden
			sql = ("SELECT location.name as lname, location.lid as lid, ep1.title as etitle1, ep2.title as etitle2, "
					+ "ep1.eid as e1id, ep2.eid as e2id FROM location, episodes as ep1, episodes as ep2, belongs_to "
					+ "WHERE ep1.eid = belongs_to.episode_from AND ep2.eid = belongs_to.episode_to AND "
					+ "belongs_to.lid = location.lid AND belongs_to.hid = ")+request.getParameter("hid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBesitz.add(new Gehört(rs.getInt("lid"), haus.get(0).getHid(), haus.get(0).getName(), rs.getInt("e1id"), rs.getString("etitle1"), rs.getInt("e2id"), rs.getString("etitle2"), rs.getString("lname")));
			}
			//Bewertungen laden
		} catch (SQLException e) {e.printStackTrace();} 
		finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
        
		////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////
		
        //freemarker variablen setzen
        request.setAttribute("haus", haus);
        request.setAttribute("haussitz", sitz);
        request.setAttribute("hausbesitz", listeBesitz);
        
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_haus.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
    	//Ratings
		Connection db2Conn = null;
    	try{
    		db2Conn = DBUtil.getConnection("got");
    		String sql = "Insert into Rating (usid, rating, text) values (?,?,?)";
    		PreparedStatement ps = db2Conn.prepareStatement(sql);
    		ps.setInt(1, 1);
    		ps.setInt(2, request.getParameter("select_bewertung"));
    		ps.setString(2, request.getParameter("textarea_bewertung"));
    		
    	}catch(SQLException e){
			try {
				db2Conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
    	}finally {if (db2Conn != null) {try {db2Conn.close();} catch (SQLException e) {e.printStackTrace();}}}
    	if (request.getParameter("btn_playlist") != null) {
    		response.sendRedirect("/detailhaus?hid="+request.getParameter("haus.hid"));
    	}else{response.sendRedirect("/detailhaus?hid="+request.getParameter("haus.hid"));}
    }
}
