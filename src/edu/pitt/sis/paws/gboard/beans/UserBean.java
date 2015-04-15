package edu.pitt.sis.paws.gboard.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserBean implements Cloneable, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;	
	private String login = null;
	private String name = null;
	private String email = null;
	private String role = null;
	private ArrayList<NotificationItem> notifications = null;
	private boolean dontShowAgain = true;
	SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");


	/**
	 * @return clone of this object
	 */
	public Object clone() {
		try {
			return(super.clone());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean isAdmin() {
		if (role != null && role.equals("admin")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isTeacher() {
		if (role != null && role.equals("teacher")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the notifications
	 */
	public ArrayList<NotificationItem> getNotifications() {
		return notifications;
	}

	/**
	 * @param notifications the notifications to set
	 */
	public void setNotifications(ArrayList<NotificationItem> notifications) {
		this.notifications = notifications;
	}
	
	/**
	 * @param notification the notification to add
	 */
	public void addNotification(String text, String type) {
		if (notifications == null)
			notifications = new ArrayList<NotificationItem>();
		NotificationItem notification = new NotificationItem();
		notification.setTime(dateFormat.format(new Date()));
		notification.setText(text);
		notification.setType(type);
		notifications.add(notification);
	}

	/**
	 * @return the dontShowAgain
	 */
	public String isDontShowAgain() {
		return String.valueOf(dontShowAgain);
	}

	/**
	 * @param dontShowAgain the dontShowAgain to set
	 */
	public void setDontShowAgain(boolean dontShowAgain) {
		this.dontShowAgain = dontShowAgain;
	}
}
