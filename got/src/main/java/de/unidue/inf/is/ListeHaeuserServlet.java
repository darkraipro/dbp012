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

import de.unidue.inf.is.domain.Haus;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class ListeHaeuserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static List<Haus> houseList = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Haus haus;
		try (Connection db2Conn = DBUtil.getConnection("got")) {
			houseList.clear();
			final String sql1 = "SELECT houses.name, words, seat, houses.hid, location.name as ort FROM houses, location WHERE houses.seat = location.lid";
			try (PreparedStatement ps = db2Conn.prepareStatement(sql1)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						String name = rs.getString("name");
						String words = rs.getString("words");
						String ort = rs.getString("ort");
						int seat = rs.getInt("seat");
						int hid = rs.getInt("hid");
						haus = new Haus(hid, name, words, ort, seat);
						houseList.add(haus);

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
		request.setAttribute("haeuser", houseList);
		request.getRequestDispatcher("liste_haeuser.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
