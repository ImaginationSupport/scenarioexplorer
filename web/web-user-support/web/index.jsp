<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head minimalJS="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:maincontent title="Scenario Explorer User Support" cssClasses="narrow-main-content-holder">

	<imaginationsupport:maincontentsection>
		If you know your current password and want to change it, <a href="change-password.jsp">click here</a>.<br/>
		<br />
		If you do not know your current password and need to reset it, <a href="reset-password.jsp">click here</a>.<br/>
	</imaginationsupport:maincontentsection>

</imaginationsupport:maincontent>
</body>
</html>
