<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-project-template.js" datePicker="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>
	<imaginationsupport:helpsidebarentry title="Name">
		The name of the project template
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarentry title="Description">
		A description of the project template (optional)
	</imaginationsupport:helpsidebarentry>
</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Project Template" saveId="button-save" cancelId="button-cancel" deleteId="button-delete">

	<imaginationsupport:maincontentsection>
		<form id="project-template-form">
			<!-- project name -->
			<div class="form-group row">
				<label for="project-template-name" class="col-2 col-form-label">
					Name<imaginationsupport:helpbubble hoverPopup="The name of the project template" />
				</label>
				<div class="col-10">
					<input type="text" class="form-control" id="project-template-name" placeholder="My Project Template Name">
				</div>
			</div>

			<!-- project description -->
			<div class="form-group row">
				<label for="project-template-description" class="col-2 col-form-label">
					Description<imaginationsupport:helpbubble hoverPopup="A description of the project template (optional)" />
				</label>
				<div class="col-10">
					<textarea class="form-control" id="project-template-description" rows="2" placeholder="Description of the project template"></textarea>
				</div>
			</div>
		</form>
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection cssClasses="py-0">
		<!-- alert -->
		<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>
	</imaginationsupport:maincontentsection>

</imaginationsupport:maincontent>

</body>
</html>
