package edu.pitt.sis.paws.gboard.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LogoutService
 */
public class LogoutService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutService() {
        super();
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.invalidate();
		String timeOut = request.getParameter("timeout");
		if (timeOut != null && timeOut.equals("true")) {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath()+"/login?action=EXPIRED"));
		} else {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath()+"/login?action=LOGGEDOUT"));  			
		}
	}

}
