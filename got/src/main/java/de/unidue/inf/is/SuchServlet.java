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
import de.unidue.inf.is.domain.Season;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class SuchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		List<Figur> figuren = new ArrayList<>();
		List<Haus> haeuser = new ArrayList<>();
		List<Season> staffeln = new ArrayList<>();


		// SQL abfragen
		try (Connection db2Conn = DBUtil.getConnection("got")) {
			String sql = "";
			// FIGUR
			if (!request.getParameter("suchef").isEmpty()) {
				sql = "SELECT DISTINCT c.cid, c.name, p.pid, a.aid FROM characters c "
						+ "LEFT JOIN person p ON c.cid = p.pid " + "LEFT JOIN animal a ON c.cid = a.aid "
						+ "LEFT JOIN member_of mo ON c.cid = mo.pid " + "LEFT JOIN houses h ON h.hid = mo.hid "
						+ "WHERE UPPER(c.name) LIKE ? " + "OR UPPER (p.title) LIKE ? " + "OR UPPER (h.name) LIKE ? ";
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setString(1, "%" + request.getParameter("suchef").toUpperCase() + "%");
					ps.setString(2, "%" + request.getParameter("suchef").toUpperCase() + "%");
					ps.setString(3, "%" + request.getParameter("suchef").toUpperCase() + "%");
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							figuren.add(new Figur(rs.getInt("cid"), rs.getString("name")));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// HAUS
			else if (!request.getParameter("sucheh").isEmpty()) {
				sql = ("SELECT houses.name, houses.hid FROM houses " + "WHERE UPPER(houses.name) LIKE ?");
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setString(1, "%" + request.getParameter("sucheh").toUpperCase() + "%");
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							haeuser.add(new Haus(rs.getInt("hid"), rs.getString("name")));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			// STAFFEL
			else if (!request.getParameter("suches").isEmpty()) {
				sql = ("SELECT season.number, season.sid FROM season " + "WHERE season.number = ?");
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setString(1, "%" + request.getParameter("suches") + "%");
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							staffeln.add(new Season(rs.getInt("sid"), rs.getInt("number")));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		request.setAttribute("figuren", figuren);
		request.setAttribute("haeuser", haeuser);
		request.setAttribute("staffeln", staffeln);

		request.getRequestDispatcher("suchergebnisse.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
