package tesis.server.socialNetwork.utils;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class DefaultWrapperServlet extends HttpServlet{

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
	
		HttpServletRequest wrapped = new HttpServletRequestWrapper(req) {
			public String getServletPath() { 
				return ""; 
			}
	    };
	
	    rd.forward(wrapped, resp);
	}
}

