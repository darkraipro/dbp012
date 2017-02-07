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
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailPersonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Person person = null;
		Ort herkunft = null;
		List<Mitglied> listeHaeuser = new ArrayList<>();
		List<Beziehung> listeBeziehungen = new ArrayList<>();
		List<Figur> listeBesitzer = new ArrayList<>();
		List<Bewertung> listeBewertung = new ArrayList<>();
		Bewertung bewertung = new Bewertung();

		// SQL abfragen
		try (Connection db2Conn = DBUtil.getConnection("got")) {

			// Art laden und Auswählen ob Tier oder Person
			String sql = ("SELECT COUNT(animal.aid) as result " + "FROM animal WHERE animal.aid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("cid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						if (rs.getInt("result") > 0) {
							// Weiterleiten falls Tier
							response.sendRedirect(("/detailtier?cid=" + request.getParameter("cid")));
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Person laden
			sql = ("SELECT characters.cid, characters.birthplace, person.title, person.biografie, characters.name, location.name as lname FROM person, characters, location WHERE location.lid = characters.birthplace AND person.pid = characters.cid AND characters.cid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("cid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						person = new Person(rs.getInt("cid"), rs.getInt("birthplace"), rs.getString("title"),
								rs.getString("biografie"), rs.getString("name"));
						herkunft = new Ort(rs.getInt("birthplace"), rs.getString("lname"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Haeuser laden
			sql = ("SELECT houses.hid, houses.name, ep1.eid as eid1, ep2.eid as eid2, ep1.title as ename1, ep2.title as ename2 FROM houses, member_of, episodes as ep1, episodes as ep2 WHERE ep1.eid = member_of.episode_from AND ep2.eid = member_of.episode_to AND houses.hid = member_of.hid AND member_of.pid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("cid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeHaeuser.add(new Mitglied(Integer.parseInt(request.getParameter("cid")), rs.getInt("hid"),
								rs.getInt("eid1"), rs.getInt("eid2"), rs.getString("name"), rs.getString("ename1"),
								rs.getString("ename2")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Beziehungen laden
			sql = ("SELECT characters.name, characters.cid, person_relationship.rel_type FROM characters, person_relationship WHERE characters.cid = person_relationship.targetpid AND person_relationship.sourcepid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("cid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeBeziehungen
								.add(new Beziehung(rs.getInt("cid"), rs.getString("name"), rs.getString("rel_type")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Tiere (Haustiere) laden
			sql = ("SELECT characters.name, characters.cid FROM characters, animal WHERE characters.cid = animal.aid AND animal.owner = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("cid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeBesitzer.add(new Figur(rs.getInt("cid"), rs.getString("name")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Durchschnittsbewertung laden
			sql = ("SELECT DOUBLE(AVG(r.rating)) as average from rating r, rat_for_char rc WHERE rc.rid = r.rid and rc.cid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("cid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						bewertung.setAvgrating(rs.getDouble("average"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// alle Bewertungen laden
			sql = ("SELECT users.name, rating, text FROM users, rat_for_char rc, rating r "
					+ "WHERE r.usid = users.usid and r.rid = rc.rid and rc.cid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("cid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeBewertung
								.add(new Bewertung(rs.getString("name"), rs.getInt("rating"), rs.getString("text")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		request.setAttribute("person", person);
		request.setAttribute("herkunft", herkunft);
		request.setAttribute("personhaeuser", listeHaeuser);
		request.setAttribute("personbeziehungen", listeBeziehungen);
		request.setAttribute("personbesitzer", listeBesitzer);
		request.setAttribute("listeBewertung", listeBewertung);
		request.setAttribute("bewertung", bewertung);

		request.getRequestDispatcher("detail_person.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Boolean exists = false;
		if (request.getParameter("btn_bewerten") != null) {
			int rid = -1;
			try (Connection db2Conn = DBUtil.getConnection("got")) {
				db2Conn.setAutoCommit(false);
				int ratingrid = -1;
				// Checking if rating exists
				String sql = ("Select rating.usid as usidtest, rating.rid as rarid FROM rating, rat_for_char WHERE rating.usid = ?"
						+ " and rating.rid = rat_for_char.rid and rat_for_char.cid = ?");
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setInt(1, 21);
					ps.setInt(2, Integer.parseInt(request.getParameter("cid")));
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							if (rs.getInt("usidtest") == 21) {
								System.out.println("Userrating already exists. Now proceed to Update");
								exists = true;
								ratingrid = rs.getInt("rarid");
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (exists) {
					sql = "UPDATE rating SET rating = ?" + ", text = ?" + " WHERE usid = ? and rating.rid = ?";

					try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
						ps.setInt(1, Integer.parseInt(request.getParameter("select_bewertung")));
						ps.setString(2, request.getParameter("textarea_bewertung"));
						ps.setInt(3, 21);
						ps.setInt(4, ratingrid);
						ps.executeUpdate();
						db2Conn.commit();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					System.out.println("Bewertung update!");
				} else {
					sql = "Insert into Rating (usid, rating, text) values (?,?,?)";
					try (PreparedStatement ps = db2Conn.prepareStatement(sql, new String[] { "rid" })) {
						ps.setInt(1, 21);
						ps.setInt(2, Integer.parseInt(request.getParameter("select_bewertung")));
						ps.setString(3, request.getParameter("textarea_bewertung"));
						ps.executeUpdate();
						db2Conn.commit();

						try (ResultSet rs2 = ps.getGeneratedKeys()) {
							while (rs2.next()) {
								rid = rs2.getShort(1);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						System.out.println("New RID: " + rid);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					sql = "Insert into rat_for_char (rid,cid) values (?,?)";
					try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
						ps.setInt(1, rid);
						ps.setInt(2, Integer.parseInt(request.getParameter("cid")));
						ps.executeUpdate();

						System.out.println("Bewertung eingefügt!");
						db2Conn.commit();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			doGet(request, response);
		} else {
			doGet(request, response);
		}
	}
}
