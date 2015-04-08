package edu.pitt.sis.paws.gboard.ajaxpersistors;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.pitt.sis.paws.gboard.beans.UserBean;
import edu.pitt.sis.paws.gboard.dbpersistors.*;

/**
 * Servlet implementation class SearchUser
 */
public class SearchUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchUser() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean userBean = (UserBean) request.getSession().getAttribute("userBean");
		if (userBean == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You have to be logged in to access this service"); 
			return;
		}
		
		String searchQuery = request.getParameter("queryString");
		String showAdd = request.getParameter("showAdd");
		boolean addaddButton = false;
		if (searchQuery == null)
			return;
		searchQuery = searchQuery.trim();
		if (searchQuery.length()<2)
			return;
		
		addaddButton = (showAdd != null && showAdd.equals("true")) ? true : false;
		
		PrintWriter out = response.getWriter();
		DbPersistor persistor = new DbPersistor("PortalTest2");
        try {
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	attr.put(1, "%"+searchQuery+"%");
        	attr.put(2, "%"+searchQuery+"%");
        	ResultSet set = persistor.persistData(DbPersistor.SEARCH_USER, attr);
			if (set == null)
				return;
			
        	set.last();
			int size = set.getRow();
			if (size < 1) {
				out.println("<a disabled class=\"list-group-item\"><p class=\"list-group-item-text\">No Results</p></a>");
			} else {
				set.beforeFirst();
				while (set.next()) {
					String uID = set.getString("UserID");
					String uName = set.getString("Name");
					String uLogin = set.getString("Login");
					String uEmail = set.getString("EMail");
					out.println("<a class=\"list-group-item\" uid=\""+uID+"\" uname=\""+uName+"\" ulogin=\""+uLogin+"\" uemail=\""+uEmail+"\">"
							+(addaddButton ? "<span class=\"glyphicon glyphicon-plus-sign pull-right\" onclick=\"addUserTopSearch(this);\" title=\"add to the group\"></span>" : "")+
							"<p class=\"list-group-item-text\" style=\"margin-right: 17px;\" onclick=\"openUserInfoModal('"+uLogin+"', userInfoStaticMode);\">"+uName+" ("+uLogin+")</p></a>");
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	persistor.close();
        }
	}

}
