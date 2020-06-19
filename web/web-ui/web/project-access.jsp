<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-project-access.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>
	<imaginationsupport:helptip>
		Collaboration reduces surprise by providing more situational awareness.
	</imaginationsupport:helptip>

	<hr />

	<imaginationsupport:helpsidebarentry title="Project Access">
		Scenario Explorer allows for remote collaboration on challenging projects.
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarshowmore>

		<imaginationsupport:helpsidebarentry title="Owner">
			The Owner has full editorial control over the project, including adding and deleting members.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Member">
			Members can edit all features, timeline events, and create new views, but do not have authority to delete the project.
		</imaginationsupport:helpsidebarentry>

	</imaginationsupport:helpsidebarshowmore>

</imaginationsupport:sidebar>

<imaginationsupport:maincontent title="Project Access" saveId="button-save" cancelId="button-cancel">

	<imaginationsupport:maincontentsection>
		<table id="users" class="table table-striped w-100"></table>
	</imaginationsupport:maincontentsection>

</imaginationsupport:maincontent>
</body>
</html>
