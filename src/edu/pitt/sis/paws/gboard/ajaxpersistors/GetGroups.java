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
import edu.pitt.sis.paws.gboard.dbpersistors.*;

/**
 * Servlet implementation class GerGroups
 */
public class GetGroups extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetGroups() {
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
		
		DbPersistor persistor = new DbPersistor("PortalTest2");
		ResultSet set = null;
        try {
        	PrintWriter out = response.getWriter();
        	if (userBean.isAdmin()) {
        		set = persistor.persistData(DbPersistor.GET_ALL_GROUPS, null);        		
        	} else {
        		HashMap<Integer, String> attr = new HashMap<Integer, String>();
        		attr.put(1, Integer.toString(userBean.getId()));
        		set = persistor.persistData(DbPersistor.GET_ALL_GROUPS_FOR_TEACHER, attr);
        	}
        	
        	JSONArray allGroups = new JSONArray();
        	while (set.next()) {
        		JSONObject group = new JSONObject();
        		group.put("id", set.getString("u.UserID"));
        		group.put("login", set.getString("u.Login"));
        		group.put("name", set.getString("u.Name"));
        		allGroups.put(group);
        	}
			out.println(allGroups.toString());
        	
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	PrintWriter out2 = response.getWriter();
            	out2.println("false");
            	out2.close();
            } catch (Exception e2) {}
        } finally {
        	persistor.close();
        }
	}

}
