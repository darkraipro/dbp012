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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Variablen init
		Episode ep;
		int nummer = 0;
		String num = "";
		Connection db2Conn = null;
		listEpisoden.clear();
		// String out = "";
		try {
			StringBuffer outb = new StringBuffer();
			outb.append("SELECT * FROM Episodes, Season WHERE Episodes.sid = Season.sid and Season.sid = ")
					.append(request.getParameter("sid"));
			db2Conn = DBUtil.getConnection("got");
			final String sql1 = outb.toString();
			outb.delete(0, outb.length());
			outb.append("SELECT number FROM Season WHERE Season.sid = ").append(request.getParameter("name"));
			final String sql2 = outb.toString();
			PreparedStatement ps = db2Conn.prepareStatement(sql1);
			PreparedStatement ps1 = db2Conn.prepareStatement(sql2);
			ResultSet rs = ps.executeQuery();
			int i = 0;
			while (rs.next() && i < 5) {
				int number = rs.getInt("eid");
				int numberofe = rs.getInt("number");
				int sid = rs.getInt("sid");
				String summary = rs.getString("summary");
				String title = rs.getString("title");
				Date date = rs.getDate("startdate");
				ep = new Episode(number, numberofe, title, summary, date, sid);
				listEpisoden.add(ep);

				i++;
			}
			rs = ps1.executeQuery();
			if (rs.next()) {
				nummer = rs.getInt("number");
				System.out.println("Nummer: " + Integer.toString(nummer));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Ressourcen schließen
			if (db2Conn != null) {
				try {
					db2Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		num = Integer.toString(nummer);
		// freemarker variablen setzen
		request.setAttribute("staffelepisoden", listEpisoden);
		request.setAttribute("staffelnummer", request.getParameter("sid"));

		// Put the user list in request and let freemarker paint it.
		request.getRequestDispatcher("detail_staffel.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
