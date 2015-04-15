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
 * Servlet implementation class OverridePassword
 */
public class OverridePassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OverridePassword() {
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
		String user_pass = request.getParameter("u_p");
		
		int found_user_id = 0;
		String userName = null;
		// UM2
		int found_user_id2 = 0;
		// end of -- UM2
		
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
		DbPersistor persistorUM2 = new DbPersistor("UM2");
		ResultSet setUM2 = null;
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	
        	if (!userBean.getRole().equals("admin")) {
        		jsonResponse.put("error", "ERROR! You don't have sufficient rights for this service");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! You don't have sufficient rights for this service", "danger");
    			return;
        	}
        	
        	if( (user_pass == null || user_pass.length() == 0) || (user_login == null || user_login.length() == 0) ) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
    		
    		// check user
    		attr.put(1, user_login);
    		
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_USER, attr);

        	setUM2 = persistorUM2.persistData(DbPersistor.GET_USER, attr);
    		
    		if(setPT2.next() && setUM2.next()) {
    			found_user_id = setPT2.getInt("UserID");
    			userName = setPT2.getString("Name");
    	        userName = (userName == null) ? "" : userName;
    			// UM2
    			found_user_id2 = setUM2.getInt("UserID");
    			// end of -- UM2
    		} else {
    			jsonResponse.put("error", "ERROR! Specified user is not found");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Specified user is not found", "danger");
    			return;
    		}
    		
    		// Update User Password
    		attr = new HashMap<Integer, String>();
    		attr.put(1, user_pass);
    		attr.put(2, Integer.toString(found_user_id));
    		
    		persistorPT2.persistUpdate(DbPersistor.UPDATE_USER_PASSWORD, attr);
    		
    		// UM2
    		attr = new HashMap<Integer, String>();
    		attr.put(1, user_pass);
    		attr.put(2, Integer.toString(found_user_id2));
    		if (persistorUM2.persistUpdate(DbPersistor.UPDATE_USER_PASSWORD, attr)) {
    			jsonResponse.put("success", "<span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> password has been successfully changed");    			
    			userBean.addNotification("<span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> password has been successfully changed", "success");
    		} else {
    			jsonResponse.put("error", "ERROR! SQL exception occurred while updating <span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> password");    			
    			userBean.addNotification("ERROR! SQL exception occurred while updating <span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> password", "danger");
    		}
    		
			out.println(jsonResponse.toString());
        	out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	PrintWriter out2 = response.getWriter();
            	JSONObject jsonResponse = new JSONObject();
            	jsonResponse.put("error", "SQL exception occurred while processing your request");
            	out2.println(jsonResponse.toString());
            	userBean.addNotification("SQL exception occurred while processing your request", "danger");
            	out2.close();
            } catch (Exception e2) {}
        } finally {
        	persistorPT2.close();
        	persistorUM2.close();
        }
	}

}
