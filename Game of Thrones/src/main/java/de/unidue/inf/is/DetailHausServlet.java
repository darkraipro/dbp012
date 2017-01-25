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
public final class DetailHausServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
        String name, sitz;
        name=sitz="";
        List<String> listeBesitz = new ArrayList<>();
        
      //TEST
        name="Kein Name";
        sitz="Kein Sitz";
        
        //SQL abfragen
        name = request.getParameter("name");
        //haus = sql where name=name
        //burg = sql where name=name
        
        
        
        
        //freemarker variablen setzen
        request.setAttribute("hausname", name);
        request.setAttribute("haussitz", sitz);
        request.setAttribute("hausbesitz", listeBesitz);
        
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_haus.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
