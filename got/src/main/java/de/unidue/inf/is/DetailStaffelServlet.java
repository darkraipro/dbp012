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

import de.unidue.inf.is.domain.Bewertung;
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

		List<Bewertung> listeBewertung = new ArrayList<>();
		Bewertung bewertung = new Bewertung();
		try {
			StringBuffer outb = new StringBuffer();
			outb.append("SELECT * FROM Episodes, Season WHERE Episodes.sid = Season.sid and Season.sid = ")
					.append(request.getParameter("sid"));
			db2Conn = DBUtil.getConnection("got");
			String sql1 = outb.toString();
			outb.delete(0, outb.length());
			outb.append("SELECT number FROM Season WHERE Season.sid = ").append(request.getParameter("name"));
			String sql2 = outb.toString();
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
			// Durchschnittsbewertung laden
			String sql = ("SELECT DOUBLE(AVG(r.rating)) as average from rating r, rat_for_sea rs WHERE rs.rid = r.rid and rs.sid = ")
					+ request.getParameter("sid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				bewertung.setAvgrating(rs.getDouble("average"));
			}
			// alle Bewertungen laden
			sql = ("SELECT users.name, rating, text FROM users, rat_for_sea rs, rating r "
					+ "WHERE r.usid = users.usid and r.rid = rs.rid and rs.sid = ") + request.getParameter("sid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBewertung.add(new Bewertung(rs.getString("name"), rs.getInt("rating"), rs.getString("text")));
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
		request.setAttribute("listeBewertung", listeBewertung);
		request.setAttribute("bewertung", bewertung);

		// Put the user list in request and let freemarker paint it.
		request.getRequestDispatcher("detail_staffel.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection db2Conn = null;
		Boolean exists = false;
		if (request.getParameter("btn_bewerten") != null) {

			try {
				db2Conn = DBUtil.getConnection("got");

				db2Conn.setAutoCommit(false);
				// Checking if rating exists
				String sql = ("Select rating.usid as usidtest, rating.rid as rarid FROM rating, rat_for_sea WHERE rating.usid = ?"
						+ " and rating.rid = rat_for_sea.rid and rat_for_sea.sid = " + request.getParameter("sid"));
				PreparedStatement ps = db2Conn.prepareStatement(sql);
				ps.setInt(1, 21);
				ResultSet rs = ps.executeQuery();
				int ratingrid = -1;
				while (rs.next()) {
					if (rs.getInt("usidtest") == 21) {
						System.out.println("Userrating already exists. Now proceed to Update");
						exists = true;
						ratingrid = rs.getInt("rarid");
					}
				}
				if (exists) {
					ps.close();
					sql = "UPDATE rating SET rating = " + request.getParameter("select_bewertung") + ", text = ?"
							+ " WHERE usid = ? and rating.rid = " + Integer.toString(ratingrid);
					// Deny SQL Injection
					ps = db2Conn.prepareStatement(sql);
					ps.setString(1, request.getParameter("textarea_bewertung"));
					ps.setInt(2, 21);
					ps.executeUpdate();
					db2Conn.commit();
					System.out.println("Bewertung update!");
				} else {
					ps.close();
					sql = "Insert into Rating (usid, rating, text) values (?,?,?)";
					ps = db2Conn.prepareStatement(sql, new String[] { "rid" });
					ps.setInt(1, 21);
					ps.setInt(2, Integer.parseInt(request.getParameter("select_bewertung")));
					ps.setString(3, request.getParameter("textarea_bewertung"));
					ps.executeUpdate();
					db2Conn.commit();
					// RID finden und im Rat_for_house einfügen
					int rid = -1;

					ResultSet rs2 = ps.getGeneratedKeys();
					while (rs2.next()) {
						rid = rs2.getShort(1);
					}
					System.out.println("New RID: " + rid);
					ps.close();
					sql = "Insert into rat_for_sea (rid,sid) values (?,?)";
					ps = db2Conn.prepareStatement(sql);
					ps.setInt(1, rid);
					ps.setInt(2, Integer.parseInt(request.getParameter("sid")));
					ps.executeUpdate();
					System.out.println("Bewertung eingefügt!");
					db2Conn.commit();
				}

			} catch (SQLException e) {
				try {
					db2Conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				if (db2Conn != null) {
					try {
						db2Conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			doGet(request, response);
		} else {
			doGet(request, response);
		}
	}
}
