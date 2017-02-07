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
import de.unidue.inf.is.domain.Playlist;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailPlaylistServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Playlist playlist = null;
		List<Episode> listeEpisoden = new ArrayList<>();

		// SQL abfragen
		try (Connection db2Conn = DBUtil.getConnection("got")) {

			// Playlist laden
			String sql = ("SELECT playlist.name FROM playlist WHERE plid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("plid")));
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						playlist = new Playlist(Integer.parseInt(request.getParameter("plid")), rs.getString("name"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Episoden laden
			sql = ("SELECT episodes.title, episodes.eid FROM episodes, playlist_contains_episode "
					+ "WHERE episodes.eid = playlist_contains_episode.eid AND playlist_contains_episode.plid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("plid")));
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

		request.setAttribute("playlist", playlist);
		if (listeEpisoden.size() == 0)
			listeEpisoden.add(new Episode(0, " "));
		request.setAttribute("playlistepisoden", listeEpisoden);

		request.getRequestDispatcher("detail_playlist.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);

	}
}
