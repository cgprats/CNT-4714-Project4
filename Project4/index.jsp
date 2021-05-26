<!DOCTYPE html>
<!-- index.jsp -->
<!-- JSP front-end that processes a "get" request containing MySQL information. -->
<%-- start scriptlet --%>
<%
	String command = (String) session.getAttribute("command");
	String output = (String) session.getAttribute("output");
	if (command == null) command = "";
	if (output == null) output = "";
%>
<html lang="en">
   <!-- head section of document -->
   <head>
      <title>CNT 4714 Remote Database Manager</title>
	  <meta charset="utf-8">
	  <style type="text/css">
   	 <!--
   	 body { background-color: darkblue; color:white; font-family: verdana, arial, sans-serif; font-size: 1.4em; text-align: center; }
         h1 { color:white; font-size: 1.3em; }
	 h3 {font-size: 1.0em; }
         input[type="submit"] {background-color: yellow; font-weight:bold;}
         input[type="reset"] {background-color: yellow; font-weight:bold;}
         textarea {background-color:black; color:lime; width:50%; height: 150px;}
	 table, th, td { border: 1px solid black; margin: auto}
	 th {background-color: red; color: black}
	 td {background-color: lightgrey; color: black}
	 .errorboxed { background-color: darkred; margin: auto; width:50% }
	 .businesslogicboxed { background-color: darkgreen; margin: auto; width:50% }
	 .dbcolumns { background-color: red; color: black; margin: auto; width:50%; display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; grid-gap: 5px 1em;}
	 .dbdata { background-color: lightgrey; color: black; margin: auto; width:50%; display: grid; grid-template-columns: 1fr 1fr 1fr 1fr; grid-gap: 5px 1em;}
         span {color: red;}
         #servlet {color:purple;}
         #jsp {color:cyan;}
   	 -->
	 </style>
   </head>
   <!-- body section of document -->
   <body>
   	<h1>Welcome to the Fall 2020 Project 4 Enterprise Database System</h1>
	<h1>A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</h1>
	<hr>
   	<form action = "/Project4/MySQLServlet" method = "post">
	<small>You are connected to the Project 4 Enterprise System database.</small>
	<p><small>Please enter any valid SQL query or update command.</small></p>
	<p><textarea name = "command" autofocus = true required = true></textarea></p>
	<p><input type = "submit" value = "Execute Command"></input>
	   <input type = "reset" value = "Reset Form"></input>
	</p>
	<p>All execution results will appear below</p>
	</form>
      <hr>
      <h2>Database Results:</h2>
      <h3>
      	<%-- JSP expression to access the message sent from the servlet --%>
      	<%=output%>
      </h3>
   </body>
</html>  <!-- end HTML document -->
