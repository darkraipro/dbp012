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

import de.unidue.inf.is.domain.Bewertung;
import de.unidue.inf.is.domain.Beziehung;
import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Mitglied;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.Person;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailTierServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Variablen init
		Figur tier = null;
		Figur besitzer = null;
		Ort herkunft = null;
		List<Bewertung> listeBewertung = new ArrayList<>();
		Bewertung bewertung = new Bewertung();

		// SQL abfragen
		Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");

			// Tier laden
			String sql = ("SELECT characters.name FROM characters " + "WHERE characters.cid = "
					+ request.getParameter("cid"));
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				tier = new Figur(Integer.parseInt(request.getParameter("cid")), rs.getString("name"));

			}
			// Besitzer laden
			sql = ("SELECT characters.name, characters.cid FROM characters, animal "
					+ "WHERE characters.cid = animal.owner AND animal.aid = " + request.getParameter("cid"));
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				besitzer = new Figur(rs.getInt("cid"), rs.getString("name"));
			}
			// Herkunftsort laden
			sql = ("SELECT location.name, location.lid FROM characters, location "
					+ "WHERE location.lid = characters.birthplace AND characters.cid = " + request.getParameter("cid"));
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				herkunft = new Ort(rs.getInt("lid"), rs.getString("name"));
			}
			// Durchschnittsbewertung laden
			sql = ("SELECT DOUBLE(AVG(r.rating)) as average from rating r, rat_for_char rc WHERE rc.rid = r.rid and rc.cid = ")
					+ request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				bewertung.setAvgrating(rs.getDouble("average"));
			}
			// alle Bewertungen laden
			sql = ("SELECT users.name, rating, text FROM users, rat_for_char rc, rating r "
					+ "WHERE r.usid = users.usid and r.rid = rc.rid and rc.cid = ") + request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBewertung.add(new Bewertung(rs.getString("name"), rs.getInt("rating"), rs.getString("text")));
			}
		} catch (SQLException e) {
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

		// freemarker variablen setzen
		request.setAttribute("tier", tier);
		request.setAttribute("tierherkunft", herkunft);
		request.setAttribute("tierbesitzer", besitzer);

		// Put the user list in request and let freemarker paint it.
		request.getRequestDispatcher("detail_tier.ftl").forward(request, response);
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
				String sql = ("Select rating.usid as usidtest, rating.rid as rarid FROM rating, rat_for_char WHERE rating.usid = ?"
						+ " and rating.rid = rat_for_char.rid and rat_for_char.cid = " + request.getParameter("cid"));
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
					sql = "Insert into rat_for_char (rid,cid) values (?,?)";
					ps = db2Conn.prepareStatement(sql);
					ps.setInt(1, rid);
					ps.setInt(2, Integer.parseInt(request.getParameter("cid")));
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
