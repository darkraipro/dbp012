package de.unidue.inf.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.User;
import de.unidue.inf.is.utils.DBUtil;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class StartseiteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Put the user list in request and let freemarker paint it.
    	final String databaseToCheck = "dbp012";
		boolean databaseExists = DBUtil.checkDatabaseExists(databaseToCheck);

		request.setAttribute("db2name", databaseToCheck);

		if (databaseExists) {
			request.setAttribute("db2exists", "vorhanden! Supi!");
		}
		else {
			request.setAttribute("db2exists", "nicht vorhanden :-(");
		}
        request.getRequestDispatcher("startseite.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
