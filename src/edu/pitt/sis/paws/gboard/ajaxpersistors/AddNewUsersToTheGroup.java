package edu.pitt.sis.paws.gboard.ajaxpersistors;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.pitt.sis.paws.gboard.beans.UserBean;
import edu.pitt.sis.paws.gboard.dbpersistors.DbPersistor;

/**
 * Servlet implementation class AddNewUsersToTheGroup
 */
public class AddNewUsersToTheGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddNewUsersToTheGroup() {
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
		String group_mnemonic = request.getParameter("g_m");
		String new_users_data = request.getParameter("u_d");
		
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
		DbPersistor persistorUM2 = new DbPersistor("UM2");
		ResultSet setUM2 = null;
		JSONObject jsonResponse = new JSONObject();
        try {
        	PrintWriter out = response.getWriter();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	
        	if( (group_mnemonic == null || group_mnemonic.length() == 0) || (new_users_data == null || new_users_data.length() == 0) ) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
        	
        	JSONArray usersData = null;
        	try {
        		usersData = new JSONArray(new_users_data);        		
        	} catch (Exception jsonE) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
        	}
        	
        	// Check for existence of the specified group
        	attr.put(1, group_mnemonic);
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_GROUP, attr);
        	setUM2 = persistorUM2.persistData(DbPersistor.GET_GROUP, attr);
			
    		if(!setPT2.next() && !setUM2.next()) {
    			jsonResponse.put("error", "ERROR! Specified group is not found");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Specified group is not found", "danger");
    			return;
    		}

    		JSONArray successResponce = new JSONArray();
    		JSONArray existResponce = new JSONArray();
    		JSONArray exceptionResponce = new JSONArray();
    		JSONArray usersToAddResponce = new JSONArray();
    		jsonResponse.put("success", successResponce);
    		jsonResponse.put("exist", existResponce);
    		jsonResponse.put("exception", exceptionResponce);
    		jsonResponse.put("users", usersToAddResponce);
    		
    		// Process each user
    		for(int i=0; i<usersData.length(); i++){
    	        JSONObject userObj  = usersData.getJSONObject(i);
    	        String name = userObj.getString("name");
    			String email = userObj.getString("email");
    			String login = userObj.getString("login");
    			String password = userObj.getString("password");
    			String organization = userObj.getString("organization");
    			String city = userObj.getString("city");
    			String country = userObj.getString("country");
    	        
    			// Check whether user login or email exists
        		attr = new HashMap<Integer, String>();
        		attr.put(1, login);
        		attr.put(2, email);
        		
            	setPT2 = persistorPT2.persistData(DbPersistor.GET_USER_BY_LOGIN_OR_EMAIL, attr);

            	setUM2 = persistorUM2.persistData(DbPersistor.GET_USER_BY_LOGIN_OR_EMAIL, attr);
    	        
            	if(setPT2.next() && setUM2.next()) {
        			JSONObject tempObj  = new JSONObject();
        			tempObj.put("warning", "<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> is already in the system");
    	        	jsonResponse.getJSONArray("exist").put(tempObj);
        			userBean.addNotification("<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> is already in the system", "warning");
        			tempObj  = new JSONObject();
        			tempObj.put("login", ""+setPT2.getString("Login")+"");
        			jsonResponse.getJSONArray("users").put(tempObj);  			
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
        			attr.put(8, "0");
        			Integer insertID1 = persistorPT2.persistUpdateGetID(DbPersistor.ADD_NEW_USER, attr);
        			if (insertID1 != null && insertID2 != null) {
        				JSONObject tempObj  = new JSONObject();
            			tempObj.put("success", "<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> successfully added to the system");
        	        	jsonResponse.getJSONArray("success").put(tempObj);
        	        	userBean.addNotification("<span style=\"font-weight: bold;\">"+name+" ("+login+")</span> successfully added to the system", "success");
        	        	tempObj  = new JSONObject();
            			tempObj.put("login", ""+login+"");
            			jsonResponse.getJSONArray("users").put(tempObj);
        			} else {
        				JSONObject tempObj  = new JSONObject();
            			tempObj.put("exception", "ERROR! SQL exception occurred while adding new user <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>");
        	        	jsonResponse.getJSONArray("exception").put(tempObj);
        	        	userBean.addNotification("ERROR! SQL exception occurred while adding new user <span style=\"font-weight: bold;\">"+name+" ("+login+")</span>", "danger");
        			}    			
        		}
    	    }
    		out.println(jsonResponse.toString());
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	PrintWriter out2 = response.getWriter();
            	out2.println(jsonResponse.toString());
            	out2.close();
            } catch (Exception e2) {}
        } finally {
        	persistorPT2.close();
        	persistorUM2.close();
        }
	}
}
