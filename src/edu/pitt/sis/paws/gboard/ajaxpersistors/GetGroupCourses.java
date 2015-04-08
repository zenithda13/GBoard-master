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
 * Servlet implementation class GetGroupCourses
 */
public class GetGroupCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetGroupCourses() {
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
		DbPersistor persistorAggregate = new DbPersistor("Aggregate");
		ResultSet setAggregate = null;
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	attr.put(1, groupID);
        	setPT2 = persistorPT2.persistData(DbPersistor.GET_GROUP_BY_ID, attr);
        	setPT2.next();
        	String login = setPT2.getString("u.Login");
        	
        	attr = new HashMap<Integer, String>();
        	attr.put(1, login);
        	setAggregate = persistorAggregate.persistData(DbPersistor.GET_GROUP_COURSES, attr);
        	setAggregate.last();
			int size = setAggregate.getRow();
			if (size > 0) {
				setAggregate.beforeFirst();
				JSONArray courses = new JSONArray();
				while(setAggregate.next()) {
					JSONObject course = new JSONObject();
					course.put("id", setAggregate.getString("c.course_id"));
					course.put("title", setAggregate.getString("c.course_name")+" ("+setAggregate.getString("g.term")+" "+setAggregate.getString("g.year")+")");
	        		courses.put(course);
				}
				jsonResponse.putOpt("groupcourses", courses);
			}
        	
			setAggregate = persistorAggregate.persistData(DbPersistor.GET_ALL_COURSES, null);
			JSONArray courses = new JSONArray();
			while(setAggregate.next()) {
				JSONObject course = new JSONObject();
				course.put("id", setAggregate.getString("course_id"));
				course.put("name", setAggregate.getString("course_name"));
        		courses.put(course);
			}
			jsonResponse.putOpt("courses", courses);
        	
			out.println(jsonResponse.toString());
        	
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	PrintWriter out2 = response.getWriter();
            	JSONObject jsonResponse = new JSONObject();
            	jsonResponse.put("error", "SQL exception occurred while retrieving group courses");
            	out2.println(jsonResponse.toString());
            	out2.close();
            } catch (Exception e2) {}
        } finally {
        	persistorPT2.close();
        	persistorAggregate.close();
        }
	}

}
