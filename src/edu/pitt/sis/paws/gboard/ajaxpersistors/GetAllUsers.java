package edu.pitt.sis.paws.gboard.ajaxpersistors;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.pitt.sis.paws.gboard.beans.UserBean;
import edu.pitt.sis.paws.gboard.dbpersistors.DbPersistor;

/**
 * Servlet implementation class GetAllUsers
 */
public class GetAllUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllUsers() {
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
		
		DbPersistor persistor = new DbPersistor("PortalTest2");
		ResultSet set = null;
        try {
        	PrintWriter out = response.getWriter();
//        	UserBean userBean = (UserBean) this.session.getAttribute("userBean");
        	set = persistor.persistData(DbPersistor.GET_ALL_USERS, null);
        	
        	JSONArray users = new JSONArray();
        	while (set.next()) {
        		JSONObject user = new JSONObject();
        		user.put("id", set.getString("u.UserID"));
        		user.put("login", set.getString("u.Login"));
        		user.put("name", set.getString("u.Name"));
        		user.put("email", set.getString("u.EMail"));
        		users.put(user);
        	}
			out.println(users.toString());
        	
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	PrintWriter out2 = response.getWriter();
            	out2.println("false");
            	out2.close();
            } catch (Exception e2) {}
        } finally {
        	persistor.close();
        }
	}

}
