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

import de.unidue.inf.is.domain.Season;
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class ListeStaffelnServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static List<Season> seasonList = new ArrayList<Season>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Season season;
		try (Connection db2Conn = DBUtil.getConnection("got")) {
			final String sql1 = "SELECT number, numberofe, startdate, sid FROM season";
			try (PreparedStatement ps = db2Conn.prepareStatement(sql1)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						int number = rs.getInt("number");
						int numberofe = rs.getInt("numberofe");
						Date date = rs.getDate("startdate");
						int sid = rs.getInt("sid");
						season = new Season(number, numberofe, date, sid);
						seasonList.add(season);
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
		request.setAttribute("season", seasonList);
		request.getRequestDispatcher("liste_staffeln.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
