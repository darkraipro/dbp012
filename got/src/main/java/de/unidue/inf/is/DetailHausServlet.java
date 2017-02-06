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
import de.unidue.inf.is.domain.Gehört;
import de.unidue.inf.is.domain.GehörtAn;
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.Person;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailHausServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Variablen init
		List<Haus> haus = new ArrayList<>();
		List<Ort> sitz = new ArrayList<>();
		List<Gehört> listeBesitz = new ArrayList<>();
		List<GehörtAn> listePersonen = new ArrayList<>();
		List<Bewertung> listeBewertung = new ArrayList<>();
		Bewertung bewertung = new Bewertung();

		// SQL abfragen
		Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");

			// Haus laden
			String sql = ("SELECT houses.name FROM houses WHERE houses.hid = ") + request.getParameter("hid");
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				haus.add(new Haus(Integer.parseInt(request.getParameter("hid")), rs.getString("name")));
			}
			// Sitz laden
			sql = ("SELECT location.name, location.lid FROM houses, location WHERE location.lid = houses.seat AND houses.hid = ")
					+ request.getParameter("hid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				sitz.add(new Ort(rs.getInt("lid"), rs.getString("name")));
			}
			// Besitz laden
			sql = ("SELECT location.name as lname, location.lid as lid, ep1.title as etitle1, ep2.title as etitle2, "
					+ "ep1.eid as e1id, ep2.eid as e2id FROM location, episodes as ep1, episodes as ep2, belongs_to "
					+ "WHERE ep1.eid = belongs_to.episode_from AND ep2.eid = belongs_to.episode_to AND "
					+ "belongs_to.lid = location.lid AND belongs_to.hid = ") + request.getParameter("hid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBesitz.add(new Gehört(rs.getInt("lid"), haus.get(0).getHid(), haus.get(0).getName(),
						rs.getInt("e1id"), rs.getString("etitle1"), rs.getInt("e2id"), rs.getString("etitle2"),
						rs.getString("lname")));
			}
			// Personen(Angehörige) laden
			sql = ("SELECT characters.name as cname, characters.cid as cid, ep1.title as etitle1, ep2.title as etitle2, "
					+ "ep1.eid as e1id, ep2.eid as e2id FROM characters, episodes as ep1, episodes as ep2, member_of "
					+ "WHERE ep1.eid = member_of.episode_from AND ep2.eid = member_of.episode_to AND "
					+ "member_of.pid = characters.cid AND member_of.hid = ") + request.getParameter("hid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listePersonen.add(new GehörtAn(rs.getInt("cid"), haus.get(0).getHid(), haus.get(0).getName(),
						rs.getInt("e1id"), rs.getString("etitle1"), rs.getInt("e2id"), rs.getString("etitle2"),
						rs.getString("cname")));
			}
			// Durchschnittsbewertung laden
			sql = ("SELECT DOUBLE(AVG(r.rating)) as average from rating r, rat_for_house rh WHERE rh.rid = r.rid and rh.hid = ")
					+ request.getParameter("hid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				bewertung.setAvgrating(rs.getDouble("average"));
			}
			// alle Bewertungen laden
			sql = ("SELECT users.name, rating, text FROM users, rat_for_house rh, rating r "
					+ "WHERE r.usid = users.usid and r.rid = rh.rid and rh.hid = ") + request.getParameter("hid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBewertung.add(new Bewertung(rs.getString("name"), rs.getInt("rating"), rs.getString("text")));
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

		// freemarker variablen setzen
		request.setAttribute("haus", haus);
		request.setAttribute("haussitz", sitz);
		request.setAttribute("hausbesitz", listeBesitz);
		request.setAttribute("hauspersonen", listePersonen);
		request.setAttribute("bewertung", bewertung);
		request.setAttribute("listeBewertung", listeBewertung);

		// Put the user list in request and let freemarker paint it.
		request.getRequestDispatcher("detail_haus.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Ratings
		Connection db2Conn = null;
		Boolean exists = false;
		if (request.getParameter("btn_bewerten") != null) {

			try {
				db2Conn = DBUtil.getConnection("got");

				db2Conn.setAutoCommit(false);
				// Checking if rating exists
				String sql = ("Select rating.usid as usidtest, rating.rid as rarid FROM rating, rat_for_house WHERE rating.usid = ?"
						+ " and rating.rid = rat_for_house.rid and rat_for_house.hid = " + request.getParameter("hid"));
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
					sql = "UPDATE rating SET rating = " + request.getParameter("select_bewertung")
							+ ", text = ?"
							+ " WHERE usid = ? and rating.rid = " + Integer.toString(ratingrid);
					//Deny SQL Injection
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
					sql = "Insert into rat_for_house (rid,hid) values (?,?)";
					ps = db2Conn.prepareStatement(sql);
					ps.setInt(1, rid);
					ps.setInt(2, Integer.parseInt(request.getParameter("hid")));
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
