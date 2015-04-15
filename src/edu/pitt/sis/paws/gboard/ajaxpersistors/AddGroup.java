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
 * Servlet implementation class AddGroup
 */
public class AddGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddGroup() {
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
		String group_name = request.getParameter("g_n");
		String group_mnemonic = request.getParameter("g_m");
		
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
		DbPersistor persistorUM2 = new DbPersistor("UM2");
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	
        	if( (group_mnemonic == null || group_mnemonic.length() == 0 || group_mnemonic.length() > 30) || (group_name == null || group_name.length() == 0 || group_name.length() > 60) ) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
        	
        	// Check whether login is taken
        	attr.put(1, group_mnemonic);
        	setPT2 = persistorPT2.persistData(DbPersistor.CHECK_LOGIN, attr);
        	
        	if(setPT2.next()) {
    			jsonResponse.put("error", "Such mnemonic already exist");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("Such mnemonic already exist", "danger");
    			return;
    		}
        	
        	// Add group to PT2 and UM2
        	attr = new HashMap<Integer, String>();
        	attr.put(1, group_mnemonic);
        	attr.put(2, group_name);
        	
        	Integer newGroupID = null;
        	boolean apdateFlag2 = false;
    		newGroupID = persistorPT2.persistUpdateGetID(DbPersistor.ADD_GROUP, attr);
    		
    		if (!persistorUM2.persistUpdate(DbPersistor.ADD_GROUP, attr)) {
    			apdateFlag2 = true;
    		}
    		
    		if (newGroupID != null && !apdateFlag2) {
    			if(userBean.isTeacher()) {
    				attr = new HashMap<Integer, String>();
    				attr.put(1, Integer.toString(userBean.getId()));
    	        	attr.put(2, Integer.toString(newGroupID));
    	        	persistorPT2.persistUpdate(DbPersistor.ASSIGN_GROUP_TO_TEACHER, attr);
    			}
    			jsonResponse.put("success", "Group with name("+group_name+") has been successfully added");   
    			userBean.addNotification("Group with name("+group_name+") has been successfully added", "success");
    		} else {
    			jsonResponse.put("error", "SQL exception occurred while processing your request");
    			userBean.addNotification("SQL exception occurred while processing your request", "danger");
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
            	out2.close();
            	userBean.addNotification("SQL exception occurred while processing your request", "danger");
            } catch (Exception e2) {}
        } finally {
        	persistorPT2.close();
        	persistorUM2.close();
        }
	}

}
