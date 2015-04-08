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
 * Servlet implementation class RemoveUserFromGroup
 */
public class RemoveUserFromGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveUserFromGroup() {
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
		String showWarning = request.getParameter("w_m");
		if (showWarning != null) {
			boolean showWarningBool = showWarning.equals("true") ? true : false;
			userBean.setDontShowAgain(showWarningBool);
			return;
		}
		
		String group_mnemonic = request.getParameter("g_m");
		String user_login = request.getParameter("u_l");
		
		int found_group_id = 0;
		int found_user_id = 0;
		String found_group_mnemonic = "";
		String groupName = null;
		String userName = null;
		// UM2
		int found_group_id2 = 0;
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
        	
        	if( (group_mnemonic == null || group_mnemonic.length() == 0) || (user_login == null || user_login.length() == 0) ) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
        	
        	attr.put(1, group_mnemonic);
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_GROUP, attr);

        	setUM2 = persistorUM2.persistData(DbPersistor.GET_GROUP, attr);

    		if(setPT2.next() && setUM2.next()) {
    			found_group_id = setPT2.getInt("UserID");
    			found_group_mnemonic = setPT2.getString("Login");
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
    		
    		if(found_group_mnemonic.equals("world")) {
    			jsonResponse.put("error", "ERROR! Users cannot be deleted from group 'World'");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Users cannot be deleted from group 'World'", "danger");
    			return;
    		}
    		
    		// check user
    		attr = new HashMap<Integer, String>();
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
    		
    		// check membership
    		attr = new HashMap<Integer, String>();
    		attr.put(1, Integer.toString(found_group_id));
    		attr.put(2, Integer.toString(found_user_id));
    		
    		setPT2 = persistorPT2.persistData(DbPersistor.IS_USER_GROUP_MEMBER, attr);
    		
    		if(!setPT2.next()) {
    			jsonResponse.put("error", "ERROR! <span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> is not a member of the <span style=\"font-weight: bold;\">"+groupName+"</span> group");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! <span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> is not a member of the <span style=\"font-weight: bold;\">"+groupName+"</span> group", "danger");
    			return;
    		}
    		
    		persistorPT2.persistUpdate(DbPersistor.DELETE_USER_FROM_GROUP_PT2, attr);
    		
    		// UM2
    		attr = new HashMap<Integer, String>();
    		attr.put(1, Integer.toString(found_group_id2));
    		attr.put(2, Integer.toString(found_user_id2));
    		if (persistorUM2.persistUpdate(DbPersistor.DELETE_USER_FROM_GROUP_UM2, attr)) {
    			jsonResponse.put("success", "<span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> successfully removed from the <span style=\"font-weight: bold;\">"+groupName+"</span> group");    			
    			userBean.addNotification("<span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> successfully removed from the <span style=\"font-weight: bold;\">"+groupName+"</span> group", "success");
    		} else {
    			jsonResponse.put("error", "ERROR! SQL exception occurred while adding <span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> to the <span style=\"font-weight: bold;\">"+groupName+"</span> group");    			
    			userBean.addNotification("ERROR! SQL exception occurred while adding <span style=\"font-weight: bold;\">"+userName+" ("+user_login+")</span> to the <span style=\"font-weight: bold;\">"+groupName+"</span> group", "danger");
    		}
    		
			out.println(jsonResponse.toString());
        	
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
