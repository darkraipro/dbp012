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
import de.unidue.inf.is.domain.Gehört;
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Mitglied;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.Person;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailPersonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Variablen init
		Person person = null;
		Ort herkunft = null;
		List<Mitglied> listeHaeuser = new ArrayList<>();
		List<Beziehung> listeBeziehungen = new ArrayList<>();
		List<Figur> listeBesitzer = new ArrayList<>();
		List<Bewertung> listeBewertung = new ArrayList<>();
		Bewertung bewertung = new Bewertung();

		// SQL abfragen
		Connection db2Conn = null;
		try {
			db2Conn = DBUtil.getConnection("got");

			// Art laden und Auswählen ob Tier oder Person
			String sql = ("SELECT COUNT(animal.aid) as result " + "FROM animal WHERE animal.aid = "
					+ request.getParameter("cid"));
			PreparedStatement ps = db2Conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getInt("result") > 0) {
					// Weiterleiten falls Tier
					response.sendRedirect(("/detailtier?cid=" + request.getParameter("cid")));
				}
			}
			// Person laden
			sql = ("SELECT characters.cid, characters.birthplace, person.title, person.biografie, characters.name, location.name as lname FROM person, characters, location WHERE location.lid = characters.birthplace AND person.pid = characters.cid AND characters.cid = ")
					+ request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				person = new Person(rs.getInt("cid"), rs.getInt("birthplace"), rs.getString("title"),
						rs.getString("biografie"), rs.getString("name"));
				herkunft = new Ort(rs.getInt("birthplace"), rs.getString("lname"));
			}
			// Haeuser laden
			sql = ("SELECT houses.hid, houses.name, ep1.eid as eid1, ep2.eid as eid2, ep1.title as ename1, ep2.title as ename2 FROM houses, member_of, episodes as ep1, episodes as ep2 WHERE ep1.eid = member_of.episode_from AND ep2.eid = member_of.episode_to AND houses.hid = member_of.hid AND member_of.pid = ")
					+ request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeHaeuser.add(new Mitglied(Integer.parseInt(request.getParameter("cid")), rs.getInt("hid"),
						rs.getInt("eid1"), rs.getInt("eid2"), rs.getString("name"), rs.getString("ename1"),
						rs.getString("ename2")));
			}
			// Beziehungen laden
			sql = ("SELECT characters.name, characters.cid, person_relationship.rel_type FROM characters, person_relationship WHERE characters.cid = person_relationship.targetpid AND person_relationship.sourcepid = ")
					+ request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBeziehungen.add(new Beziehung(rs.getInt("cid"), rs.getString("name"), rs.getString("rel_type")));
			}
			// Tiere (Haustiere) laden
			sql = ("SELECT characters.name, characters.cid FROM characters, animal WHERE characters.cid = animal.aid AND animal.owner = ")
					+ request.getParameter("cid");
			ps = db2Conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listeBesitzer.add(new Figur(rs.getInt("cid"), rs.getString("name")));
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
		request.setAttribute("person", person);
		request.setAttribute("herkunft", herkunft);
		request.setAttribute("personhaeuser", listeHaeuser);
		request.setAttribute("personbeziehungen", listeBeziehungen);
		request.setAttribute("personbesitzer", listeBesitzer);
		request.setAttribute("listeBewertung", listeBewertung);
		request.setAttribute("bewertung", bewertung);

		// Put the user list in request and let freemarker paint it.
		request.getRequestDispatcher("detail_person.ftl").forward(request, response);
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
