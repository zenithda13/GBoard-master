package edu.pitt.sis.paws.gboard.ajaxpersistors;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import edu.pitt.sis.paws.gboard.beans.UserBean;
import edu.pitt.sis.paws.gboard.dbpersistors.DbPersistor;

/**
 * Servlet implementation class AddCourseToTheGroup
 */
public class AddCourseToTheGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddCourseToTheGroup() {
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
		
		String groupMnemonic = request.getParameter("g_m");
		String groupName = request.getParameter("g_n");
		String term = request.getParameter("term");
		String year = request.getParameter("year");
		String courseID = request.getParameter("c_id");

		DbPersistor persistorAggregate = new DbPersistor("Aggregate");
        try {
        	PrintWriter out = response.getWriter();
        	JSONObject jsonResponse = new JSONObject();
        	if (groupMnemonic == null || groupMnemonic.length() < 1 || groupName == null || groupName.length() < 1 || term == null || term.length() < 1 || 
        			year == null || year.length() < 1 || courseID == null || courseID.length() < 1) {
        		jsonResponse.put("error", "ERROR! Wrong parameters");
    			out.println(jsonResponse.toString());
    			userBean.addNotification("ERROR! Wrong parameters", "danger");
    			return;
    		}
        	
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	attr.put(1, groupMnemonic);
        	attr.put(2, groupName);
        	attr.put(3, courseID);
        	attr.put(4, term);
        	attr.put(5, year);
        	
        	if (persistorAggregate.persistUpdate(DbPersistor.ADD_COURSE_TO_THE_GROUP, attr)) {
        		jsonResponse.put("success", "<span style=\"font-weight: bold;\">"+groupName+"</span> group course has been updated");    			
    			userBean.addNotification("<span style=\"font-weight: bold;\">"+groupName+"</span> group course has been updated", "success");
    		} else {
    			jsonResponse.put("error", "ERROR! SQL exception occurred while updating <span style=\"font-weight: bold;\">"+groupName+"</span> group course");    			
    			userBean.addNotification("ERROR! SQL exception occurred while updating <span style=\"font-weight: bold;\">"+groupName+"</span> group course", "danger");
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
        	persistorAggregate.close();
        }
	}

}
