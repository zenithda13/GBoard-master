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
 * Servlet implementation class UpdateGroupApps
 */
public class UpdateGroupApps extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateGroupApps() {
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
		String apps = request.getParameter("apps");
		
		DbPersistor persistorUM2 = new DbPersistor("UM2");
		ResultSet setUM2 = null;
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	if( (group_mnemonic == null || group_mnemonic.length() == 0) || (apps == null || apps.length() == 0) ) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
        		out.println(jsonResponse.toString());
        		userBean.addNotification("ERROR! Wrong parameters", "danger");
        		return;
        	}
        	
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	attr.put(1, group_mnemonic);
        	setUM2 = persistorUM2.persistData(DbPersistor.GET_GROUP, attr);
        	String groupID = "";
        	String groupName = "";
        	if (setUM2.next()) {
        		groupID = setUM2.getString("UserID");
        		groupName = setUM2.getString("Name");
        	} else {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
        		out.println(jsonResponse.toString());
        		userBean.addNotification("ERROR! Wrong parameters", "danger");
        		return;
        	}
        	
        	JSONArray appsData = null;
        	try {
        		appsData = new JSONArray(apps);        		
        	} catch (Exception jsonE) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
        	}
        	
        	boolean flag = false;
        	attr = new HashMap<Integer, String>();
        	attr.put(1, groupID);
        	if (persistorUM2.persistUpdate(DbPersistor.CLEAN_UP_GROUP_APPS, attr)) {
        		for(int i=0; i<appsData.length(); i++){
        			JSONObject appObj  = appsData.getJSONObject(i);
        			String id = appObj.getString("id");
        			attr = new HashMap<Integer, String>();
        			attr.put(1, id);
        			attr.put(2, groupID);
        			if (!persistorUM2.persistUpdate(DbPersistor.ADD_APP_CONNECTION, attr)) {
        				flag = true;
        			}
        		}        		
        	} else {
        		flag = true;
        	}

    		if (!flag) {
    			jsonResponse.put("success", "<span style=\"font-weight: bold;\">"+groupName+"</span> group apps have been updated");    			
    			userBean.addNotification("<span style=\"font-weight: bold;\">"+groupName+"</span> group apps have been updated", "success");
    		} else {
    			jsonResponse.put("error", "ERROR! SQL exception occurred while updating <span style=\"font-weight: bold;\">"+groupName+"</span> group apps");    			
    			userBean.addNotification("ERROR! SQL exception occurred while updating <span style=\"font-weight: bold;\">"+groupName+"</span> group apps", "danger");
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
        	persistorUM2.close();
        }
	}

}
