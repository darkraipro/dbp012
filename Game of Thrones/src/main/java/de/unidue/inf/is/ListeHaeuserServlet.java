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
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class ListeHaeuserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static List<Haus> houseList = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Put the user list in request and let freemarker paint it.
		Haus haus;
		Connection db2Conn = null;
		String out = "";
		try {
			db2Conn = DBUtil.getConnection("got");
			final String sql1 = "SELECT name, words, seat FROM houses";
			PreparedStatement ps = db2Conn.prepareStatement(sql1);
			ResultSet rs = ps.executeQuery();
			StringBuffer outb = new StringBuffer();
			while(rs.next()){
				String name = rs.getString("name");
				String words = rs.getString("words");
				int seat = rs.getInt("seat");
				haus = new Haus(name, words, seat);
				houseList.add(haus);
				outb.append(name).append(" ").append(words).append(" ").append(seat).append("\n");
			}
			out = outb.toString();
			System.out.println(out);
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
		request.setAttribute("haeuser", houseList);
		request.getRequestDispatcher("liste_haeuser.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
