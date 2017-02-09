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
import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.domain.Playlist;
import de.unidue.inf.is.domain.Season;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class StartseiteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		List<Figur> listeFiguren = new ArrayList();
		List<Haus> listeHaeuser = new ArrayList();
		List<Season> listeStaffeln = new ArrayList();
		List<Playlist> listePlaylist = new ArrayList();

		// SQL abfragen
		String sql = "";
		int anzahlTestFiguren = 5;
		int anzahlTestHaeuser = 5;
		int anzahlTestStaffeln = 5;
		try (Connection db2Conn = DBUtil.getConnection("got")) {
			// Anzahl Figuren prüfen
			sql = ("SELECT COUNT(characters.cid) as result " + "FROM characters");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						if (rs.getInt("result") < 5)
							anzahlTestFiguren = rs.getInt("result");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Anzahl Haeuser prüfen
			sql = ("SELECT COUNT(houses.hid) as result " + "FROM houses");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						if (rs.getInt("result") < 5)
							anzahlTestHaeuser = rs.getInt("result");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Anzahl Staffeln prüfen
			sql = ("SELECT COUNT(season.sid) as result " + "FROM season");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						if (rs.getInt("result") < 5)
							anzahlTestStaffeln = rs.getInt("result");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Figuren laden
			for (int i = 1; i <= anzahlTestFiguren; i++) {
				sql = ("SELECT characters.name, characters.cid FROM characters " + "WHERE characters.cid = " + i);
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							listeFiguren.add(new Figur(rs.getInt("cid"), rs.getString("name")));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// Haeuser laden
			for (int i = 1; i <= anzahlTestHaeuser; i++) {
				sql = ("SELECT houses.name, houses.hid FROM houses " + "WHERE houses.hid = " + i);
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							listeHaeuser.add(new Haus(rs.getInt("hid"), rs.getString("name")));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// Staffeln laden
			for (int i = 1; i <= anzahlTestStaffeln; i++) {
				sql = ("SELECT season.number, season.sid FROM season " + "WHERE season.sid = " + i);
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							listeStaffeln.add(new Season(rs.getInt("sid"), rs.getInt("number")));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			// Alle Playlisten laden
			sql = ("SELECT plid, name FROM playlist WHERE usid=1");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listePlaylist.add(new Playlist(rs.getInt("plid"), rs.getString("name")));
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

		request.setAttribute("vorschaufiguren", listeFiguren);
		request.setAttribute("vorschauhaeuser", listeHaeuser);
		request.setAttribute("vorschaustaffeln", listeStaffeln);
		request.setAttribute("vorschauplaylist", listePlaylist);

		request.getRequestDispatcher("startseite.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getParameter("btn_suchfigur") != null) {
			String textfeld = request.getParameter("txt_suchfigur");
			response.sendRedirect("/suche?suchef=" + textfeld);
		}
		if (request.getParameter("btn_suchhaus") != null) {
			String textfeld = request.getParameter("txt_suchhaus");
			response.sendRedirect("/suche?suchef=&sucheh=" + textfeld);
		}
		if (request.getParameter("btn_suchstaffel") != null) {
			String textfeld = request.getParameter("txt_suchstaffel");
			response.sendRedirect("/suche?suchef=&sucheh=&suches=" + textfeld);
		}
		if (request.getParameter("btn_playlist") != null) {
			String textfeld = request.getParameter("txt_playlist");
			try (Connection db2Conn = DBUtil.getConnection("got")) {
				db2Conn.setAutoCommit(false);
				List<Playlist> playlists = new ArrayList<>();

				// Playlists laden
				String sql = ("SELECT * FROM playlist WHERE EXISTS (SELECT * FROM playlist WHERE name = ?)");				
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setString(1, textfeld);
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							System.out.println("DOPPELTER EINTRAG");
							response.sendRedirect("/start");
						}
						// Playlistnamen überprüfen
//						for (Playlist p : playlists) {
//							System.out.println("Neue: -"+ textfeld +"-, Alte: -"+ p.getName()+"-");
//							String wert = p.getName();
//							if (textfeld.equals(wert)) {
//								
//							}
//						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				sql = ("INSERT INTO playlist(name,usid) VALUES(?, 1)");
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setString(1, textfeld);
					ps.executeUpdate();
					db2Conn.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				response.sendRedirect("/start");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
