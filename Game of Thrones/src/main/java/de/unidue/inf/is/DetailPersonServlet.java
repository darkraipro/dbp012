package de.unidue.inf.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.unidue.inf.is.domain.Beziehung;
import de.unidue.inf.is.domain.User;



/**
 * Einfaches Beispiel, das die Vewendung der Template-Engine zeigt.
 */
public final class DetailPersonServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Variablen init
        String name, herkunft, biographie, haus;
        name=herkunft=biographie=haus="";
        List<Beziehung> listeBeziehungen = new ArrayList<>();
        List<String> listeBesitzer = new ArrayList<>();
        
        //SQL abfragen
        name = request.getParameter("name");
        //haus = sql where name=name
        //burg = sql where name=name
        
        //TEST
        herkunft="alpen";
        biographie="tod";
        haus="simpsons";
        listeBeziehungen.add(new Beziehung("mit mama","liebe"));
        listeBeziehungen.add(new Beziehung("mit papa","im krieg"));
        listeBesitzer.add("struppi");
        listeBesitzer.add("tim");
        
        
        //freemarker variablen setzen
        request.setAttribute("personname", name);
        request.setAttribute("personherkunft", herkunft);
        request.setAttribute("personhaus", haus);
        request.setAttribute("personbeziehungen", listeBeziehungen);
        request.setAttribute("personbesitzer", listeBesitzer);
        request.setAttribute("personbiographie", biographie);
    	
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_person.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
