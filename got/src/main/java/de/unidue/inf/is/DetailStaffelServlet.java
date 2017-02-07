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
import de.unidue.inf.is.utils.DBUtil;

/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailStaffelServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private List<Episode> listEpisoden = new ArrayList<>();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Episode ep;
		int nummer = 0;
		String num = "";
		listEpisoden.clear();

		List<Bewertung> listeBewertung = new ArrayList<>();
		Bewertung bewertung = new Bewertung();
		try (Connection db2Conn = DBUtil.getConnection("got")) {
			StringBuffer outb = new StringBuffer();
			outb.append("SELECT * FROM Episodes, Season WHERE Episodes.sid = Season.sid and Season.sid = ?");
			String sql1 = outb.toString();
			outb.delete(0, outb.length());
			outb.append("SELECT number FROM Season WHERE Season.sid = ?");
			String sql2 = outb.toString();
			try (PreparedStatement ps = db2Conn.prepareStatement(sql1)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("sid")));
				try (ResultSet rs = ps.executeQuery()) {
					int i = 0;
					while (rs.next() && i < 5) {
						int number = rs.getInt("eid");
						int numberofe = rs.getInt("number");
						int sid = rs.getInt("sid");
						String summary = rs.getString("summary");
						String title = rs.getString("title");
						Date date = rs.getDate("startdate");
						ep = new Episode(number, numberofe, title, summary, date, sid);
						listEpisoden.add(ep);

						i++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try (PreparedStatement ps = db2Conn.prepareStatement(sql2)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("sid")));
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						nummer = rs.getInt("number");
						System.out.println("Nummer: " + Integer.toString(nummer));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Durchschnittsbewertung laden
			String sql = ("SELECT DOUBLE(AVG(r.rating)) as average from rating r, rat_for_sea rs WHERE rs.rid = r.rid and rs.sid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("sid")));
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
			sql = ("SELECT users.name, rating, text FROM users, rat_for_sea rs, rating r "
					+ "WHERE r.usid = users.usid and r.rid = rs.rid and rs.sid = ?");
			try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
				ps.setInt(1, Integer.parseInt(request.getParameter("sid")));
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

		num = Integer.toString(nummer);
		request.setAttribute("staffelepisoden", listEpisoden);
		request.setAttribute("staffelnummer", request.getParameter("sid"));
		request.setAttribute("listeBewertung", listeBewertung);
		request.setAttribute("bewertung", bewertung);

		request.getRequestDispatcher("detail_staffel.ftl").forward(request, response);
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
				String sql = ("Select rating.usid as usidtest, rating.rid as rarid FROM rating, rat_for_sea WHERE rating.usid = ?"
						+ " and rating.rid = rat_for_sea.rid and rat_for_sea.sid = ?");
				try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
					ps.setInt(1, 21);
					ps.setInt(2, Integer.parseInt(request.getParameter("sid")));
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
					sql = "Insert into rat_for_sea (rid,sid) values (?,?)";
					try (PreparedStatement ps = db2Conn.prepareStatement(sql)) {
						ps.setInt(1, rid);
						ps.setInt(2, Integer.parseInt(request.getParameter("sid")));
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
