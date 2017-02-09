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
import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailEpisodeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Variablen init
		Episode episode = new Episode();
		List<Figur> listeFiguren = new ArrayList<>();
		List<Ort> listeOrte = new ArrayList<>();
		List<Bewertung> listeBewertung = new ArrayList<>();
		Bewertung bewertung = new Bewertung();

		// SQL abfragen
		try (Connection db2Conn = DBUtil.getConnection("got")) {

			// Episode laden
			String sql = ("SELECT * FROM episodes WHERE eid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("eid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						episode = new Episode(rs.getInt("eid"), rs.getInt("number"), rs.getString("title"),
								rs.getString("summary"), rs.getDate("releasedate"), rs.getInt("sid"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Figuren laden
			sql = ("SELECT characters.cid, characters.name, characters.birthplace FROM characters, char_for_epi WHERE characters.cid = char_for_epi.cid AND "
					+ " char_for_epi.eid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("eid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeFiguren.add(new Figur(rs.getString("name"), 0, rs.getInt("cid")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Orte laden
			sql = ("SELECT location.lid, location.name FROM location, loc_for_epi WHERE location.lid = loc_for_epi.lid AND "
					+ " loc_for_epi.eid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("eid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeOrte.add(new Ort(rs.getInt("lid"), rs.getString("name")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Durchschnittsbewertung laden
			sql = ("SELECT DOUBLE(AVG(r.rating)) as average from rating r, rat_for_epi re WHERE re.rid = r.rid and re.eid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("eid")));
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
			sql = ("SELECT users.name, rating, text FROM users, rat_for_epi re, rating r "
					+ "WHERE r.usid = users.usid and r.rid = re.rid and re.eid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("eid")));
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

		// freemarker variablen setzen
		request.setAttribute("episodeid", episode.getEid());
		request.setAttribute("episodetitel", episode.getTitle());
		request.setAttribute("episodenummer", episode.getNumber());
		request.setAttribute("episodestaffel", episode.getSid());
		request.setAttribute("episodehandlung", episode.getSummary());
		request.setAttribute("episodefiguren", listeFiguren);
		request.setAttribute("episodeorte", listeOrte);
		request.setAttribute("bewertung", bewertung);
		request.setAttribute("listeBewertung", listeBewertung);

		// Put the user list in request and let freemarker paint it.
		request.getRequestDispatcher("detail_episode.ftl").forward(request, response);
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
				String sql = ("Select rating.usid as usidtest, rating.rid as rarid FROM rating, rat_for_epi WHERE rating.usid = ?"
						+ " and rating.rid = rat_for_epi.rid and rat_for_epi.eid = ?");
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setInt(1, 21);
					ps.setInt(2, Integer.parseInt(request.getParameter("eid")));
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
					sql = "Insert into rat_for_char (rid,eid) values (?,?)";
					try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
						ps.setInt(1, rid);
						ps.setInt(2, Integer.parseInt(request.getParameter("eid")));
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
