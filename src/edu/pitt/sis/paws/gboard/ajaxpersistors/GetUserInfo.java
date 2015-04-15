package edu.pitt.sis.paws.gboard.ajaxpersistors;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import edu.pitt.sis.paws.gboard.beans.UserBean;
import edu.pitt.sis.paws.gboard.dbpersistors.DbPersistor;

/**
 * Servlet implementation class GetUserInfo
 */
public class GetUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUserInfo() {
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
		
		String user_login = request.getParameter("u_l");
		
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	
        	if(user_login == null || user_login.length() == 0) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
    		
    		attr = new HashMap<Integer, String>();
    		attr.put(1, user_login);
    		
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_USER, attr);
    		

        	if(setPT2.next()) {    			
    			jsonResponse.put("id", setPT2.getString("UserID"));
    			jsonResponse.put("login", user_login);
    			jsonResponse.put("name", setPT2.getString("Name"));
    			jsonResponse.put("email", setPT2.getString("EMail"));
    			jsonResponse.put("organization", setPT2.getString("Organization"));
    			jsonResponse.put("city", setPT2.getString("City"));
    			jsonResponse.put("country", setPT2.getString("Country"));
    			if (userBean.isAdmin()) {
    				jsonResponse.put("teacher", setPT2.getString("IsInstructor"));    				
    			}
    		} else {
    			jsonResponse.put("error", "ERROR! Specified user is not found");
    			userBean.addNotification("ERROR! Specified user is not found", "danger");
    		}
    		
			out.println(jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	PrintWriter out2 = response.getWriter();
            	JSONObject jsonResponse = new JSONObject();
            	jsonResponse.put("error", "SQL exception occurred while processing your request");
            	out2.println(jsonResponse.toString());
            	out2.close();
            	userBean.addNotification("SQL exception occurred while processing your request", "danger");
            } catch (Exception e2) {}
        } finally {
        	persistorPT2.close();
        }
	}

}
