<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
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
<imaginationsupport:breadcrumbs />
<imaginationsupport:maincontent title="About">
	<imaginationsupport:maincontentsection>

		<p>Scenario Explorer - &copy; 2016-2019 <a href="http://www.ara.com" target="_blank">Applied Research Associates, Inc.</a></p>
		<p>Version: {{app.version}} (Build {{build.number}})<br />Built: {{build.time}}</p>

	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>
</body>
</html>
