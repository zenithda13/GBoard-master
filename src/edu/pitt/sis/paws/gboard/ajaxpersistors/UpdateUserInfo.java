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
 * Servlet implementation class UpdateUserInfo
 */
public class UpdateUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateUserInfo() {
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
		
		String name = request.getParameter("u_n");
		String email = request.getParameter("u_e");
		String login = request.getParameter("u_l");
		String organization = request.getParameter("u_o");
		organization = organization == null ? "" : organization;
		String city = request.getParameter("u_c");
		city = city == null ? "" : city;
		String country = request.getParameter("u_country");
		country = country == null ? "" : country;
		String teacher = request.getParameter("u_t");
		
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
		DbPersistor persistorUM2 = new DbPersistor("UM2");
		ResultSet setUM2 = null;
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	
        	if(!userBean.isAdmin() || name == null || name.length() == 0 || email == null || email.length() == 0 || 
        			login == null || login.length() == 0 || teacher == null || (!teacher.equals("0") && !teacher.equals("1"))) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
    		
    		// Check whether user login or email exists
    		attr = new HashMap<Integer, String>();
    		attr.put(1, login);
    		
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_USER, attr);

        	setUM2 = persistorUM2.persistData(DbPersistor.GET_USER, attr);
    		
    		if(setPT2.next() && setUM2.next()) {
    			//Add new user
    			attr = new HashMap<Integer, String>();
    			attr.put(1, name);
    			attr.put(2, email);
    			attr.put(3, organization);
    			attr.put(4, city);
    			attr.put(5, country);
    			attr.put(6, teacher);
    			attr.put(7, login);
    			boolean update1 = persistorPT2.persistUpdate(DbPersistor.UPDATE_USER_INFO, attr);
    			// UM2
    			attr = new HashMap<Integer, String>();
    			attr.put(1, name);
    			attr.put(2, email);
    			attr.put(3, organization);
    			attr.put(4, city);
    			attr.put(5, country);
    			attr.put(6, login);
    			boolean update2 = persistorUM2.persistUpdate(DbPersistor.UPDATE_USER_INFO_UM2, attr);
    			// end of -- UM2
    			if (update1 && update2) {
    				attr = new HashMap<Integer, String>();
    	    		attr.put(1, login);
    	    		setPT2 = persistorPT2.persistData(DbPersistor.GET_USER, attr);
    				JSONObject userJson = new JSONObject();
    				if (setPT2.next()) {
    					userJson.put("id", setPT2.getString("UserID"));
    					userJson.put("login", setPT2.getString("Login"));
    					userJson.put("name", setPT2.getString("Name"));
    					userJson.put("email", setPT2.getString("EMail"));
    					jsonResponse.put("success", "<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> info has been successfully updated");    	
    					jsonResponse.put("user", userJson);
    					userBean.addNotification("<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> info has been successfully updated", "success");    					
    				} else {
    					jsonResponse.put("error", "ERROR! SQL exception occurred while retrieving updated user info for <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>");
        				userBean.addNotification("ERROR! SQL exception occurred while retrieving updated user info for <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>", "danger");
    				}
    			} else {
    				jsonResponse.put("error", "ERROR! SQL exception occurred while updating user info for <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>");
    				userBean.addNotification("ERROR! SQL exception occurred while updating user info for <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>", "danger");
    			}    			
    		} else {
    			jsonResponse.put("error", "Specified user wasn't found");
    			userBean.addNotification("Specified user wasn't found", "danger");
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
        	persistorUM2.close();
        }
	}
}
