<%@ page 
	contentType="text/html; charset=utf8"
	language="java"
	import="java.util.*, edu.pitt.sis.paws.gboard.beans.*"
	pageEncoding="utf8"
%>
<%
	UserBean userBean = (UserBean) session.getAttribute("userBean");
	
	if (userBean == null) {
		response.sendRedirect(request.getContextPath()+"/login?action=LOGINREQD");
		return;
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>Groups Dashboard</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="Group Management Dashboard">
		<meta name="author" content="Artem Beaty-Avdonin">
		<link href="<%=request.getContextPath()%>/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
		<link href="<%=request.getContextPath()%>/css/dashboard.css" rel="stylesheet" type="text/css" />
		<link href="<%=request.getContextPath()%>/css/jquery.dataTables.css" rel="stylesheet" type="text/css" />
		<link href="<%=request.getContextPath()%>/css/bootstrap-select.min.css" rel="stylesheet" type="text/css" />
		<link href="<%=request.getContextPath()%>/css/jquery.ambiance.css" rel="stylesheet" type="text/css" />
		<link href="<%=request.getContextPath()%>/css/dataTables.tableTools.css" rel="stylesheet" type="text/css" />
		<link href="<%=request.getContextPath()%>/css/group.dashboard.css" rel="stylesheet" type="text/css" />
	</head>
	<body>

		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="<%=request.getContextPath()%>/dashboard">Groups Dashboard</a>
				</div>
				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav navbar-right">
						<li><a onclick="openNotificationCenter();" style="cursor: pointer;">Notifications</a></li>
						<li><p class="navbar-text">Signed in as <%= "<a onclick=\"openUserInfoModal('"+userBean.getLogin()+"', userInfoStaticMode);\">"+userBean.getLogin()+"</a>" %></p></li>
						<li><a href="LogoutService">logout</a></li>
					</ul>
					<form class="navbar-form navbar-right">
						<div class="form-group">
							<input type="text" class="form-control" placeholder="User Search..." id="searchField" onkeyup="lookup(this.value);" />
							<span id="searchclear" class="glyphicon glyphicon-remove-circle" onclick="clearTopUserSearchField();"></span>
		          			<div class="list-group" id="suggestions"></div>
	          			</div>
					</form>
				</div>
			</div>
		</nav>

		<div class="container-fluid">
			<div class="row">
				<div class="col-sm-3 col-md-2 sidebar">
					<button class="btn btn-default btn-lg btn-group-add" onclick="openAddGroupModal();" title="add new group"><span class="glyphicon glyphicon-plus-sign"></span></button>
					<h3 class="sub-header">Group</h3>
					<select id="groupSelector" data-live-search="true">
						<option value="-1">select group</option>
					</select><br/><br/>
					<button class="btn btn-default btn-lg btn-app-confirm hidden" onclick="updateGroupAppsOKBtnClick();" title="update group apps" id="updateGroupAppsOKBtn"><span class="glyphicon glyphicon-ok-sign"></span></button>
					<h4 class="sub-header">Apps</h4>
					<ul class="list-group" id="groupApps">
						No Selected Group
					</ul>
					<button class="btn btn-default btn-lg btn-course-add hidden" onclick="openAddCourseModal();" title="add course to the group" id="addGroupCoursePlusBtn"><span class="glyphicon glyphicon-plus-sign"></span></button>
					<h4 class="sub-header">Course</h4>
					<ul class="list-group" id="groupCourses">
						No Selected Group
					</ul>
					<a href="umrestart" target="_blank" style="position: absolute; bottom: 40px; color: rgba(0,0,0,0.5);">User Model Service Restart</a>
					<div style="position: absolute; bottom: 10px; color: rgba(0,0,0,0.5);">Artem Beaty-Avdonin <span class="glyphicon glyphicon-copyright-mark"></span> 2014</div>
				</div>
				<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
					<h1 class="no-group">No Selected Group</h1>
				</div>
			</div>
		</div>
    
    <!-- Add Group Modal -->
	<div class="modal fade" id="addGroupModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">Add New Group</h4>
	      </div>
	      <div class="modal-body">
			<div class="form-horizontal">
				<div id="alertMessageAddGroup"></div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Name</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" id="addGroupNameField" name="name" />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Mnemonic</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" id="addGroupMnemonicField" name="email" />
					</div>
				</div>
			</div>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" onclick="createNewGroup();">Create</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Add Course Modal -->
	<div class="modal fade" id="addCourseModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">Add Course To The Group</h4>
	      </div>
	      <div class="modal-body">
			<div class="form-horizontal">
				<div id="alertMessageAddCourse"></div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Course</label>
					<div class="col-sm-9">
						<select class="form-control" id="addCourseCourseField">
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Term</label>
					<div class="col-sm-9">
						<select class="form-control" id="addCourseTermField">
						  <option value="Fall">Fall</option>
						  <option value="Spring">Spring</option>
						  <option value="Summer">Summer</option>
						</select>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Year</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" id="addCourseYearField" />
					</div>
				</div>
			</div>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" onclick="addCourseToTheGroup();">Add</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Add Existing Users Modal-->
	<div class="modal fade" id="addExistingUsersModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg">
	    <div class="modal-content">
	      <div class="modal-header" id="addExistingUsersModalHeader">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">Add Existing Users To The Group</h4>
	      </div>
	      <div class="modal-body" id="addExistingUsersModalBody">
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- See Selected Users Modal-->
	<div class="modal fade" id="seeSelectedUsersModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title" id="seeSelectedUsersModalTitle">Selected Users</h4>
	      </div>
	      <div class="modal-body" id="seeSelectedUsersModalBody">
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" onclick="addSelectedUsersFromSelectedModal();">Add to the group</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Confirm Delete User Modal-->
	<div class="modal fade" id="confirmDeleteUserModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title" id="confirmDeleteUserModalTitle"></h4>
	      </div>
	      <div class="modal-body" id="seeSelectedUsersModalBody">
	      <div class="checkbox">
		    <label>
		      <input type="checkbox" onchange="toggleDeleteConfirmation();"> do not show me this again 
		    </label>
		  </div>
	      </div>
	      <div class="modal-footer">
	      	<button type="button" class="btn btn-default" id="confirmDeleteUserModalYes" onclick="">Yes</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Notification Center Modal-->
	<div class="modal fade" id="notificationCenterModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">Notification Center</h4>
	      </div>
	      <div class="modal-body" id="notificationCenterModalBody">
<%
	ArrayList<NotificationItem> notifications = userBean.getNotifications();
	if (notifications != null && notifications.size() > 0) {
		ListIterator<NotificationItem> iterator = notifications.listIterator(notifications.size());
		while (iterator.hasPrevious()) {
			NotificationItem i = iterator.previous();
			out.println("<div class=\"alert alert-"+i.getType()+"\"><span style=\"font-size: 0.8em; color: gray;\">"+i.getTime()+"</span> "+i.getText()+"</div>");
		}
	}
%>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- User Info Modal-->
	<div class="modal fade" id="userInfoModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title" id="userInfoModalTitle"></h4>
	      </div>
	      <div class="modal-body" id="userInfoModalModalBody">
			<div class="form-horizontal">
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Name</label>
			    <div class="col-sm-9">
			      <p class="form-control-static" id="userEditNameStatic"></p>
			      <input type="text" class="form-control" id="userEditName" name="name" onkeyup="editUserSaveBtnUpdate();" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Login</label>
			    <div class="col-sm-9">
			      <p class="form-control-static" id="userEditLoginStatic"></p>
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Email</label>
			    <div class="col-sm-9">
			      <p class="form-control-static" id="userEditEmailStatic"></p>
			      <input type="text" class="form-control" id="userEditEmail" name="email" onkeyup="editUserSaveBtnUpdate();" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Organization</label>
			    <div class="col-sm-9">
			      <p class="form-control-static" id="userEditOrganizationStatic"></p>
			      <input type="text" class="form-control" id="userEditOrganization" name="organization" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">City</label>
			    <div class="col-sm-9">
			      <p class="form-control-static" id="userEditCityStatic"></p>
			      <input type="text" class="form-control" id="userEditCity" name="city" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Country</label>
			    <div class="col-sm-9">
			      <p class="form-control-static" id="userEditCountryStatic"></p>
			      <input type="text" class="form-control" id="userEditCountry" name="country" />
			    </div>
			  </div>
<%
	if (userBean.getRole().equals("admin")) {
		out.println("<div class=\"form-group\"><div class=\"col-sm-offset-3 col-sm-9\"><div class=\"checkbox\"><label><input type=\"checkbox\" id=\"userEditTeacher\" value=\"true\"> Teacher</label></div></div></div>");
	}
%>
			</div>
	      </div>
	      <div class="modal-footer">
<%= userBean.getRole().equals("admin") ? "<button type=\"button\" class=\"btn btn-default pull-left\" onclick=\"showUpdateUserPassModal();\">Override Password</button>"+
	"<button type=\"button\" class=\"btn btn-default\" onclick=\"userInfoEditMode();\" id=\"userInfoEditButton\">Edit</button>"+
	"<button type=\"button\" class=\"btn btn-default\" onclick=\"saveUserEditUpdates();\" id=\"editUsersSaveBtn\">Save</button>" : "" %>
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Add New Single User Modal-->
	<div class="modal fade" id="addNewSingleUserModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">Add New User</h4>
	      </div>
	      <div class="modal-body">
			<div class="form-horizontal">
			  <div id="addNewSingleUserErrorBox"></div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Name<span style="color: red;"> *</span></label>
			    <div class="col-sm-9">
			      <input type="text" class="form-control" id="userAddName" name="name" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Email<span style="color: red;"> *</span></label>
			    <div class="col-sm-9">
			      <input type="text" class="form-control" id="userAddEmail" name="email" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Login<span style="color: red;"> *</span></label>
			    <div class="col-sm-9">
			      <input type="text" class="form-control" id="userAddLogin" name="name" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Password<span style="color: red;"> *</span></label>
			    <div class="col-sm-9">
			      <input type="password" class="form-control" id="userAddPass" name="name" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Organization</label>
			    <div class="col-sm-9">
			      <input type="text" class="form-control" id="userAddOrganization" name="organization" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">City</label>
			    <div class="col-sm-9">
			      <input type="text" class="form-control" id="userAddCity" name="city" />
			    </div>
			  </div>
			  <div class="form-group">
			    <label class="col-sm-3 control-label">Country</label>
			    <div class="col-sm-9">
			      <input type="text" class="form-control" id="userAddCountry" name="country" />
			    </div>
			  </div>
<%
	if (userBean.getRole().equals("admin")) {
		out.println("<div class=\"form-group\"><div class=\"col-sm-offset-3 col-sm-9\"><div class=\"checkbox\"><label><input type=\"checkbox\" id=\"userAddTeacher\" value=\"true\"> Teacher</label></div></div></div>");
	}
%>
			</div>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" onclick="addNewUser();">Add</button><!-- Not output for teacher ? -->
	        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Add Multiple New Users Modal-->
	<div class="modal fade" id="addMultipleNewUsersModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-lg">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">Add Multiple New Users To The Group</h4>
	      </div>
	      <div class="modal-body">
	      	<small><p>To add multiple users at once copy data from excel table or csv file and paste into the text area below, 
	      	or type in new users data, that includes comma separated <em>Name</em>, <em>Email</em>, <em>Login</em>, <em>Password</em>, 
	      	<em>Organization</em>, <em>City</em>, <em>Country</em> (<em>Name</em>, <em>Email</em>, <em>Login</em> and <em>Password</em> 
	      	are required. <em>Organization</em>, <em>City</em> and <em>Country</em> are optional). Then click on 'Generate Table' and you will see your data transferred into the table bellow, 
	      	where each cell is editable and surrounded with red border if the value in it is invalid. When you are satisfied with the result click on 'Add Users From Table' to add users to the group.</p></small>
			<textarea class="form-control" rows="6" id="multipleNewUsersTextArea"></textarea>
			<table class="table" id="multipleNewUsersTable"></table>
	      </div>
	      <div class="modal-footer">
	      	<button type="button" class="btn btn-default pull-left" onclick="clearAddMultipleNewUsers();">Clear</button>
	      	<button type="button" class="btn btn-default" onclick="generateInputFieldsForEachLines();">Generate Table</button>
	      	<button type="button" class="btn btn-default" onclick="addMultipleNewUsers();" disabled="disabled" id="addMultipleNewUsersSaveBtn">Add Users From Table</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Override User Password Modal-->
	<div class="modal fade" id="overrideUserPassModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog modal-sm">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title">Enter New Password</h4>
	      </div>
	      <div class="modal-body">
	      	<input type="password" class="form-control" onkeydown="updateUserPassSaveButton();" id="updateUserPassInputField" placeholder="new password">
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" onclick="updateUserPassword();" disabled="disabled" id="saveUserPassButton">Save</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	<!-- Group users context menu -->
	<ul class="list-group custom-menu">
	  <li class="list-group-item" data-action="delete">remove from group</li>
	  <li class="list-group-item" data-action="info">info</li>
<%
	if (userBean.getRole().equals("admin")) {
		out.println("<li class=\"list-group-item\" data-action=\"edit\">edit</li>");
	}
%>
	</ul>
	
	<!-- All users context menu -->
	<ul class="list-group custom-menu-user">
	  <li class="list-group-item" data-action="add">add to the group</li>
	</ul>

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/dataTables.tableTools.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/ie10-viewport-bug-workaround.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap-select.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.ambiance.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/ifvisible.js"></script>	
	<script type="text/javascript">
		var idling = false;
		// updateAutoCall = setInterval ("amIIdling()", 10000);
		// ifvisible.setIdleDuration(600);
		// ifvisible.idle(function(){
		// 	idling = true;
		// });
		// function amIIdling () {
		// 	if (idling) {
		// 		window.location.href = "LogoutService?timeout=true";
		// 	}
		// }
<%= "var showDeleteConfirmationDialogFlag = "+userBean.isDontShowAgain()+";" %>
	</script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/group.dashboard.js"></script>
	
  </body>
</html>