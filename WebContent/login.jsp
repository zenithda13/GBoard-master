<%@ page language="java"%>
<%@ page import="edu.pitt.sis.paws.gboard.beans.UserBean" %>

<%
	UserBean userBean = (UserBean) session.getAttribute("userBean");
	
	if (userBean != null) {
		response.sendRedirect("dashboard");
		return;
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
    	<meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1">
    	<title>Groups Dashboard - Login</title>
    	<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    	<link href="css/login.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
  
	    <div class="site-wrapper">
	      <div class="site-wrapper-inner">
	        <div class="cover-container">
	
	          <div class="inner cover">
	            <h1 class="cover-heading">PAWS Groups Dashboard</h1>
	            
	            <form class="form-horizontal" role="form" name="LoginForm" id="LoginForm" method="post" action="LoginService">
					<div id="alertMessage"></div>
					<div class="form-group">
						<label class="col-sm-3 control-label">Login / Email</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" id="email" name="email" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">Password</label>
						<div class="col-sm-9">
							<input type="password" class="form-control" id="password" name="password" />
						</div>
					</div>
		            <p class="lead">
				      <input type="submit" name="Submit" value="Login" id="loginBtn" class="btn btn-default"/>
		            </p>
				</form>
	            
	          </div>
	
	          <div class="mastfoot">
	            <div class="inner">
	              <p><a href="http://adapt2.sis.pitt.edu/wiki/Main_Page">PAWS</a> Lab, iSchool, University of Pittsburgh.</p>
	            </div>
	          </div>
	
	        </div>
	      </div>
	    </div>
	
	    <script src="js/jquery-1.9.1.js"></script>
	    <script src="js/bootstrap.min.js"></script>
    	<script language="JavaScript">
			$(document).ready(function() {
				$('#email').focus();
				
				var query = window.location.search;
				if (query.length > 0) {
					var action = query.substring(query.indexOf("=") + 1, query.length);
					if(action == "LOGINFAILED") {
						alertMessage("Incorrect Login or Password. Please try again", "danger");
					}
					else if (action == "LOGINREQD") {
						alertMessage("You have to be logged in to access this web application", "danger");
					}
					else if (action == "LOGGEDOUT") {
						alertMessage("You have successfully logged out", "success");
					}
					else if (action == "EXPIRED") {
						alertMessage("Your session has expired. Please Login again", "info");
					}
				}
				
				$("#password").keypress(function(event) {
				    if (event.which == 13) {
				        event.preventDefault();
				        $("form").submit();
				    }
				});
			});
			
			function alertMessage(text, type) {
				$("#alertMessage").hide().html('<div class="alert alert-'+type+'" role="alert">'+text+'</div>').fadeIn('slow');
			}
		</script>
	</body>
</html>