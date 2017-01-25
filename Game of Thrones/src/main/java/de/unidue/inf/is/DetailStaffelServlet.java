package de.unidue.inf.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.User;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailStaffelServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
    	int nummer=0;
        List<String> listeEpisoden = new ArrayList<>();
        
        //SQL abfragen
        nummer = Integer.parseInt(request.getParameter("nummer"));
        //haus = sql where name=name
        //burg = sql where name=name
        
        //TEST
        listeEpisoden.add("ep1");
        listeEpisoden.add("ep2 mag ich nicht");
        
        
        //freemarker variablen setzen
        request.setAttribute("staffelepisoden", listeEpisoden);
        request.setAttribute("staffelnummer", nummer);
    	
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_staffel.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
