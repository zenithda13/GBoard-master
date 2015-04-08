package edu.pitt.sis.paws.gboard.ajaxpersistors;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.pitt.sis.paws.gboard.beans.UserBean;
import edu.pitt.sis.paws.gboard.dbpersistors.DbPersistor;

/**
 * Servlet implementation class GetGroupApps
 */
public class GetGroupApps extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetGroupApps() {
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
		
		String groupID = request.getParameter("g_id");
		DbPersistor persistorPT2 = new DbPersistor("PortalTest2");
		ResultSet setPT2 = null;
		DbPersistor persistorUM2 = new DbPersistor("UM2");
		ResultSet setUM2 = null;
        try {
        	PrintWriter out = response.getWriter();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	attr.put(1, groupID);
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_GROUP_BY_ID, attr);
        	setPT2.next();
        	String login = setPT2.getString("u.Login");
        	
        	HashSet<Integer> appsConnected = new HashSet<Integer>();
        	attr = new HashMap<Integer, String>();
        	attr.put(1, login);
        	setUM2 = persistorUM2.persistData(DbPersistor.GET_GROUP_CONNECTED_APPS, attr);
        	while(setUM2.next()) {
        		appsConnected.add(setUM2.getInt("AppID"));
        	}
        	
        	setUM2 = persistorUM2.persistData(DbPersistor.GET_ALL_APPS, null);
        	JSONArray allApps = new JSONArray();
        	while (setUM2.next()) {
        		JSONObject app = new JSONObject();
        		app.put("id", setUM2.getString("AppID"));
        		app.put("name", setUM2.getString("Description"));
        		app.put("checked", (appsConnected.contains(setUM2.getInt("AppID")) ? "1" : "0" ));
        		allApps.put(app);
        	}
        	
			out.println(allApps.toString());
        	
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	PrintWriter out2 = response.getWriter();
            	out2.println("false");
            	out2.close();
            } catch (Exception e2) {}
        } finally {
        	persistorPT2.close();
        	persistorUM2.close();
        }
	}

}
