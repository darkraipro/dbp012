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
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class ListeFigurenServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static List<Figur> characterList = new ArrayList<>();



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Figur character;
    	Connection db2Conn = null;
    	characterList.clear();
		//String out = "";
		try {
			db2Conn = DBUtil.getConnection("got");
			final String sql1 = "SELECT name, birthplace, cid FROM characters, person WHERE pid = cid";
			final String sql2 = "SELECT name, birthplace, cid FROM characters, animal WHERE aid = cid";
			PreparedStatement ps = db2Conn.prepareStatement(sql1);
			PreparedStatement ps2 = db2Conn.prepareStatement(sql2);
			ResultSet rs = ps.executeQuery();
			ResultSet rs2 = ps2.executeQuery();
			//StringBuffer outb = new StringBuffer();
			int i = 0;
			while(rs.next() && i <5){
				String name = rs.getString("name");
				int birthplace = rs.getInt("birthplace");
				int cid = rs.getInt("cid");
				String art = "detailperson";
				character = new Figur(name, birthplace, cid, art);
				characterList.add(character);
				//outb.append(name).append(" ").append(words).append(" ").append(seat).append("\n");
				i++;
			}
			int j = 0;
			while(rs2.next() && j<5){
				String name = rs2.getString("name");
				int birthplace = rs2.getInt("birthplace");
				int cid = rs2.getInt("cid");
				String art = "detailtier";
				character = new Figur(name, birthplace, cid, art);
				characterList.add(character);
				//outb.append(name).append(" ").append(words).append(" ").append(seat).append("\n");
				j++;
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
		}
		request.setAttribute("figuren", characterList);
        request.getRequestDispatcher("liste_figuren.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
