<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form method="POST" action="DemoServlet"  enctype="multipart/form-data">
<fieldset>
<legend>
Report parameters
</legend>
Enter authors list file : <input type="file" name="file"/>
Enter the report year : <input type="text" name="year"><br/>
<input type=submit value="Submit"/>
</fieldset>

</form>
</body>
</html>