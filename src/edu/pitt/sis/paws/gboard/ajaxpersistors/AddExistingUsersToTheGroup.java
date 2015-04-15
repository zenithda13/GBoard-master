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
 * Servlet implementation class AddExistingUsersToTheGroup
 */
public class AddExistingUsersToTheGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddExistingUsersToTheGroup() {
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
		String user_login_temp = request.getParameter("u_l");
		
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
		DbPersistor persistorUM2 = new DbPersistor("UM2");
		ResultSet setUM2 = null;
		JSONObject jsonResponse = new JSONObject();
        try {
        	PrintWriter out = response.getWriter();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	
        	if( (group_mnemonic == null || group_mnemonic.length() == 0) || (user_login_temp == null || user_login_temp.length() == 0) ) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
        	
        	JSONArray userLogins = null;
        	try {
        		userLogins = new JSONArray(user_login_temp);        		
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
        	
			int found_group_id = 0;
			String groupName = null;
			// UM2
			int found_group_id2 = 0;
			// end of -- UM2
			
    		if(setPT2.next() && setUM2.next()) {
    			found_group_id = setPT2.getInt("UserID");
    			groupName = setPT2.getString("Name");
    			// UM2
    			found_group_id2 = setUM2.getInt("UserID");
    			// end of -- UM2
    		} else {
    			jsonResponse.put("error", "ERROR! Specified group is not found");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Specified group is not found", "danger");
    			return;
    		}
    		

    		JSONArray successResponce = new JSONArray();
    		JSONArray existResponce = new JSONArray();
    		JSONArray exceptionResponce = new JSONArray();
    		jsonResponse.put("success", successResponce);
    		jsonResponse.put("exist", existResponce);
    		jsonResponse.put("exception", exceptionResponce);
    		
    		// Process each user
    		for(int i=0; i<userLogins.length(); i++){
    	        JSONObject userObj  = userLogins.getJSONObject(i);
    	        String _user_login = userObj.getString("login");
    	        String userName = null;
    	        String userEmail = null;
    	        
    	        // Check whether user login exists
    	        int found_user_id = 0;;
    	        int found_user_id2 = 0;;
    	        attr = new HashMap<Integer, String>();
    	        attr.put(1, _user_login);
    	        
    	        setPT2 = persistorPT2.persistData(DbPersistor.GET_USER, attr);
    	        
    	        setUM2 = persistorUM2.persistData(DbPersistor.GET_USER, attr);
    	        
    	        if(setPT2.next() && setUM2.next()) {
    	        	found_user_id = setPT2.getInt("UserID");
    	        	userName = setPT2.getString("Name");
        	        userEmail = setPT2.getString("EMail");
        	        userName = (userName == null) ? "" : userName;
        	        userEmail = (userEmail == null) ? "" : userEmail;
    	        	// UM2
    	        	found_user_id2 = setUM2.getInt("UserID");
    	        	// end of -- UM2
    	        } else {
    	        	JSONObject tempObj  = new JSONObject();
    	        	tempObj.put("notfound", "ERROR! User with login("+_user_login+") was not found");
    	        	jsonResponse.getJSONArray("exception").put(tempObj);
    	        	userBean.addNotification("ERROR! User with login("+_user_login+") was not found", "danger");
    	        	continue;
    	        }
    	        
    	        // check membership
    	        attr = new HashMap<Integer, String>();
    	        attr.put(1, Integer.toString(found_group_id));
    	        attr.put(2, Integer.toString(found_user_id));
    	        
    	        setPT2 = persistorPT2.persistData(DbPersistor.IS_USER_GROUP_MEMBER, attr);
    	        
    	        if(setPT2.next()) {
    	        	JSONObject tempObj  = new JSONObject();
    	        	tempObj.put("notice", "<span style=\"font-weight: bold;\">"+userName+" ("+_user_login+")</span> is already a member of the <span style=\"font-weight: bold;\">"+groupName+"</span> group");
    	        	jsonResponse.getJSONArray("exist").put(tempObj);
    	        	userBean.addNotification("<span style=\"font-weight: bold;\">"+userName+" ("+_user_login+")</span> is already a member of the <span style=\"font-weight: bold;\">"+groupName+"</span> group", "warning");
    	        	continue;
    	        }
    	        
    	        //Add user to group
    	        boolean apdateFlag = false;
    	        attr = new HashMap<Integer, String>();
    	        attr.put(1, Integer.toString(found_group_id));
    	        attr.put(2, Integer.toString(found_user_id));
    	        if (!persistorPT2.persistUpdate(DbPersistor.ADD_USER_TO_GROUP_PT2, attr)) {
    	        	apdateFlag = true;
    	        }
    	        
    	        // UM2
    	        attr = new HashMap<Integer, String>();
    	        attr.put(1, Integer.toString(found_group_id2));
    	        attr.put(2, Integer.toString(found_user_id2));
    	        if (!persistorUM2.persistUpdate(DbPersistor.ADD_USER_TO_GROUP_UM2, attr)) {
    	        	apdateFlag = true;
    	        }
    	        // end of -- UM2
    	        
    	        if (!apdateFlag) {
    	        	JSONObject tempObj  = new JSONObject();
    	        	tempObj.put("success", "<span style=\"font-weight: bold;\">"+userName+" ("+_user_login+")</span> successfully added to the <span style=\"font-weight: bold;\">"+groupName+"</span> group");
    	        	tempObj.put("userID", found_user_id);
    	        	tempObj.put("userName", userName);
    	        	tempObj.put("userLogin", _user_login);
    	        	tempObj.put("userEmail", userEmail);
    	        	jsonResponse.getJSONArray("success").put(tempObj);
    	        	userBean.addNotification("<span style=\"font-weight: bold;\">"+userName+" ("+_user_login+")</span> successfully added to the <span style=\"font-weight: bold;\">"+groupName+"</span> group", "success");
    	        } else {
    	        	JSONObject tempObj  = new JSONObject();
    	        	tempObj.put("sql", "ERROR! SQL exception occurred while adding <span style=\"font-weight: bold;\">"+userName+" ("+_user_login+")</span> to the <span style=\"font-weight: bold;\">"+groupName+"</span> group");
    	        	jsonResponse.getJSONArray("exception").put(tempObj);
    	        	userBean.addNotification("ERROR! SQL exception occurred while adding <span style=\"font-weight: bold;\">"+userName+" ("+_user_login+")</span> to the <span style=\"font-weight: bold;\">"+groupName+"</span> group", "danger");
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
