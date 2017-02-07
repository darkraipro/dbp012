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
import de.unidue.inf.is.utils.DBUtil;

public final class ListeFigurenServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static List<Figur> characterList = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Figur character;
		try (Connection db2Conn = DBUtil.getConnection("got")) {
			characterList.clear();

			final String sql1 = "SELECT characters.name, location.name as geburtsort, cid FROM characters, person, location WHERE pid = cid and birthplace = location.lid";
			final String sql2 = "SELECT characters.name, location.name as geburtsort, cid FROM characters, animal, location WHERE aid = cid and birthplace = location.lid";
			try (PreparedStatement ps = db2Conn.prepareStatement(sql1)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						String name = rs.getString("name");
						String birthplace = rs.getString("geburtsort");
						int cid = rs.getInt("cid");
						String art = "detailperson";
						character = new Figur(name, birthplace, cid, art);
						characterList.add(character);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try (PreparedStatement ps = db2Conn.prepareStatement(sql2)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						String name = rs.getString("name");
						String birthplace = rs.getString("geburtsort");
						int cid = rs.getInt("cid");
						String art = "detailtier";
						character = new Figur(name, birthplace, cid, art);
						characterList.add(character);
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
		request.setAttribute("figuren", characterList);
		request.getRequestDispatcher("liste_figuren.ftl").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
