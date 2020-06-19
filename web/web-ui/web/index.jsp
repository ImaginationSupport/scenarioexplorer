<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-index.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:maincontent title="Scenario Explorer Dashboard" newId="new-project" newLabel="New Project">

	<imaginationsupport:maincontentsection id="loading-dashboard">
		<p>Loading your dashboard, please wait...</p>
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection id="dashboard" cssClasses="d-none">
		<p>Loading, please wait...</p>
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection id="no-projects" cssClasses="d-none">
		<p>Create your first project</p>
	</imaginationsupport:maincontentsection>

</imaginationsupport:maincontent>
</body>
</html>
