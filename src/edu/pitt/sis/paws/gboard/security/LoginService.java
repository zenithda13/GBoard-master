package edu.pitt.sis.paws.gboard.security;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.pitt.sis.paws.gboard.beans.UserBean;
import edu.pitt.sis.paws.gboard.dbpersistors.DbPersistor;

/**
 * Servlet implementation class LoginService
 */
public class LoginService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginService() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (checkLogin(request)) {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath()+"/dashboard"));
		} else {
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath()+"/login?action=LOGINFAILED"));						
		}
	}
	
	private boolean checkLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserBean uBean = new UserBean();

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        if(email == null || email.length()<1 || password == null || password.length()<1) {
        	return false;
        }
       
        DbPersistor persistor = new DbPersistor("PortalTest2");
        ResultSet set = null;
        try {
        	HashMap<Integer, String> attr = new HashMap<Integer, String>();
        	attr.put(1, email);
        	attr.put(2, email);
        	set = persistor.persistData(DbPersistor.GET_USER_FOR_SECURITY_CHECK, attr);
			
        	set.last();
			int size = set.getRow();
			if (size != 1) {
	        	return false;
			}
			set.first();
			
			if (checkPasswordMD5(password, set.getString("Pass"))) {
				Integer uID = set.getInt("UserID");
				String uLogin = set.getString("Login");
				String uName = set.getString("Name");
				String uEmail = set.getString("EMail");
				String uTeacher = set.getString("IsInstructor");
				
				attr = new HashMap<Integer, String>();
	        	attr.put(1, Integer.toString(uID));
	        	set = persistor.persistData(DbPersistor.CHECK_USER_IS_ADMIN_BY_ID, attr);
				if (set.next()) {
					uBean.setRole("admin");					
				} else {
					if (uTeacher != null && uTeacher.equals("1")) {
						uBean.setRole("teacher");	
					} else {
			        	return false;
					}
				}
				
				uBean.setId(uID);
				uBean.setLogin(uLogin);
				uBean.setName(uName);
				uBean.setEmail(uEmail);
			} else {
	        	return false;
			}
			session.setAttribute("userBean", uBean);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        	persistor.close();
        }
        return true;
	}
	
	private boolean checkPasswordMD5(String clearPassword, String dbPassword) {
		if ( dbPassword != null && dbPassword.length() > 0) {
			return md5(clearPassword).equals(dbPassword);			
		} else {
			return false;
		}
	}
	
	private String md5( String source ) {
		try {
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			byte[] bytes = md.digest( source.getBytes("UTF-8") );
			return getBase16String( bytes );
		} catch( Exception e )	{
			e.printStackTrace();
			return null;
		}
	}
	
	private String getBase16String( byte[] bytes ) {
	  StringBuffer sb = new StringBuffer();
	  for( int i=0; i<bytes.length; i++ ) {
	     byte b = bytes[ i ];
	     String hex = Integer.toHexString((int) 0x00FF & b);
	     if (hex.length() == 1) {
	        sb.append("0");
	     }
	     sb.append( hex );
	  }
	  return sb.toString();
	}

}