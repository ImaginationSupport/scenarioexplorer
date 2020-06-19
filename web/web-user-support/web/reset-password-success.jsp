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
<imaginationsupport:maincontent title="Change Password" cssClasses="narrow-main-content-holder">
	<imaginationsupport:maincontentsection>

		Success!&nbsp;&nbsp;A new password has been emailed to you.<br/>
		<br />
		<a href="/scenarioexplorer">Return to Scenario Explorer</a>

	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>
</body>
</html>
