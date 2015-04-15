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
 * Servlet implementation class AddNewUserToTheGroup
 */
public class AddNewUserToTheGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewUserToTheGroup() {
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
		
		String group_mnemonic = request.getParameter("g_m");//this is group title
		String name = request.getParameter("u_n");
		String email = request.getParameter("u_e");
		String login = request.getParameter("u_l");
		String password = request.getParameter("u_p");
		String organization = request.getParameter("u_o");
		organization = organization == null ? "" : organization;
		String city = request.getParameter("u_c");
		city = city == null ? "" : city;
		String country = request.getParameter("u_country");
		country = country == null ? "" : country;
		String teacher = "0";
		String role = request.getParameter("u_r");
		
		
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
		DbPersistor persistorUM2 = new DbPersistor("UM2");
		ResultSet setUM2 = null;
		DbPersistor persistorAgg = new DbPersistor("aggregate");
		ResultSet setAgg = null;
		
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	
        	if( group_mnemonic == null || group_mnemonic.length() == 0 || name == null || name.length() == 0 || email == null || email.length() == 0 || 
        			login == null || login.length() == 0 || password == null || password.length() == 0 ) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
        	
        	// Check for existence of the specified group
        	attr.put(1, group_mnemonic);
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_GROUP, attr);
        	setUM2 = persistorUM2.persistData(DbPersistor.GET_GROUP, attr);
        	
			if(!setPT2.next() || !setUM2.next()) {
				jsonResponse.put("error", "ERROR! Specified group is not found");
				out.println(jsonResponse.toString());
				userBean.addNotification("ERROR! Specified group is not found", "danger");
				return;
    		}
    		
    		// Check whether user login or email exists
    		attr = new HashMap<Integer, String>();
    		attr.put(1, login);
    		attr.put(2, email);
    		
    		
    		
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_USER_BY_LOGIN_OR_EMAIL, attr);

        	setUM2 = persistorUM2.persistData(DbPersistor.GET_USER_BY_LOGIN_OR_EMAIL, attr);
        	
        	attr = new HashMap<Integer, String>();
        	attr.put(1, group_mnemonic);
        	attr.put(2, login);
        	
        	setAgg = persistorAgg.persistData(DbPersistor.GET_ROLE_OF_USER, attr);
    		
    		if(setPT2.next() && setUM2.next()&&setAgg.next()) {
    			JSONObject userJson = new JSONObject();
    			userJson.put("id", setPT2.getString("UserID"));
    			userJson.put("login", setPT2.getString("Login"));
    			userJson.put("name", setPT2.getString("Name"));
    			userJson.put("email", setPT2.getString("EMail"));
    			jsonResponse.put("warning", "<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> is already in the system");
    			jsonResponse.put("user", userJson);
    			out.println(jsonResponse.toString());
    			userBean.addNotification("<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> is already in the system", "warning");
    			return;    			
    		} else {
    			//Add new user
    			attr = new HashMap<Integer, String>();
    			attr.put(1, login);
        		attr.put(2, name);
        		attr.put(3, password);
        		attr.put(4, email);
        		attr.put(5, organization);
        		attr.put(6, city);
        		attr.put(7, country);
    			// UM2
    			Integer insertID2 = persistorUM2.persistUpdateGetID(DbPersistor.ADD_NEW_USER_UM2, attr);
    			// end of -- UM2
    			attr.put(8, teacher);
    			Integer insertID1 = persistorPT2.persistUpdateGetID(DbPersistor.ADD_NEW_USER, attr);
    			
    			//should also add data to aggregate.ent_non_student
    			attr = new HashMap<Integer, String>();
    			attr.put(1, login);
    			attr.put(2, group_mnemonic);
    			attr.put(3, role);
    			Integer insertID3 = persistorAgg.persistUpdateGetID(DbPersistor.ADD_USER_TO_NON_STUDENT, attr);
    			
    			
    			
    			if (insertID1 != null && insertID2 != null&&insertID3!=null) {
    				JSONObject userJson = new JSONObject();
        			userJson.put("id", insertID1.toString());
        			userJson.put("login", login);
        			userJson.put("name", name);
        			userJson.put("email", email);
        			userJson.put("role", role);
    				jsonResponse.put("success", "<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> successfully added to the system");    	
    				jsonResponse.put("user", userJson);
    				userBean.addNotification("<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> successfully added to the system", "success");
    			} else {
    				jsonResponse.put("error", "ERROR! SQL exception occurred while adding new user <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>");
    				userBean.addNotification("ERROR! SQL exception occurred while adding new user <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>", "danger");
    			}    			
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
        	persistorAgg.close();
        }
	}

}
