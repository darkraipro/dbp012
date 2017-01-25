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
public final class DetailEpisodeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;




    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	
    	//Variablen init
    	int episode, nummer, staffel;
    	episode=nummer=staffel=0;
        String titel, handlung;
        titel=handlung="";
        List<String> listeFiguren = new ArrayList<>();
        List<String> listeOrte = new ArrayList<>();
        
        //SQL abfragen
        titel = request.getParameter("titel");
        //haus = sql where name=name
        //burg = sql where name=name
        
        //TEST
        episode=1;
        nummer=123;
        staffel=222;
        handlung="alsd";
        listeFiguren.add("snow");
        listeFiguren.add("snow2");
        listeOrte.add("ort");
        listeOrte.add("ort2");
        
        
        //freemarker variablen setzen
        request.setAttribute("episodeid", episode);
        request.setAttribute("episodetitel", titel);
        request.setAttribute("episodenummer", nummer);
        request.setAttribute("episodestaffel", staffel);
        request.setAttribute("episodehandlung", handlung);
        request.setAttribute("episodefiguren", listeFiguren);
        request.setAttribute("episodeorte", listeOrte);
        
    	// Put the user list in request and let freemarker paint it.
        request.getRequestDispatcher("detail_episode.ftl").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                    IOException {
        doGet(request, response);
    }
}
