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

import de.unidue.inf.is.domain.Episode;
import de.unidue.inf.is.domain.Figur;
import de.unidue.inf.is.domain.Gehört;
import de.unidue.inf.is.domain.Ort;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailOrtServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Ort ort = null;
		List<Gehört> listeGehört = new ArrayList<>();
		List<String> listeBurgen = new ArrayList<>();
		List<Figur> listeFiguren = new ArrayList<>();
		List<Episode> listeEpisoden = new ArrayList<>();

		// SQL abfragen
		try (Connection db2Conn = DBUtil.getConnection("got")) {

			// Ort laden
			String sql = ("SELECT * FROM location WHERE lid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("lid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						ort = new Ort(rs.getInt("lid"), rs.getString("name"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Gehört laden
			sql = ("SELECT houses.name, houses.hid, episodes1.eid as eid1, episodes1.title as ename1, episodes2.eid as eid2, episodes2.title as ename2 "
					+ "FROM houses, belongs_to, episodes as episodes1, episodes as episodes2 "
					+ "WHERE belongs_to.hid = houses.hid AND belongs_to.episode_from = episodes1.eid AND belongs_to.episode_to = episodes2.eid AND "
					+ " belongs_to.lid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("lid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeGehört.add(new Gehört(Integer.parseInt(request.getParameter("lid")), rs.getInt("hid"),
								rs.getString("name"), rs.getInt("eid1"), rs.getString("ename1"), rs.getInt("eid2"),
								rs.getString("ename2")));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Burgen laden
			sql = ("SELECT name " + "FROM castle " + "WHERE castle.location = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("lid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeBurgen.add(rs.getString("name"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Figuren (Herkunftsort) laden
			sql = ("SELECT cid, name " + "FROM characters " + "WHERE characters.birthplace = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("lid")));
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
			// Episoden (Handlungsort) laden
			sql = ("SELECT episodes.eid, episodes.title " + "FROM episodes, loc_for_epi "
					+ "WHERE loc_for_epi.eid = episodes.eid AND loc_for_epi.lid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("lid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						listeEpisoden.add(new Episode(rs.getInt("eid"), rs.getString("title")));
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

		request.setAttribute("ortname", ort.getName());
		request.setAttribute("orthaus", listeGehört);
		request.setAttribute("ortburg", listeBurgen);
		request.setAttribute("ortherkunftsort", listeFiguren);
		request.setAttribute("orthandlungsort", listeEpisoden);

		request.getRequestDispatcher("detail_ort.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
