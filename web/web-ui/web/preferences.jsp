<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-preferences.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>
	<imaginationsupport:helpsidebarentry title="Date Format">
		Sets your personal preference on how to display dates in the application
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarentry title="Time Format">
		Sets your personal preference on how to display times in the application
	</imaginationsupport:helpsidebarentry>
</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Preferences" saveId="button-save" cancelId="button-cancel">
	<imaginationsupport:maincontentsection>
		<form id="preferences-form">
			<div class="form-group row">
				<label for="date-format" class="col-6 col-form-label">
					Date Format<imaginationsupport:helpbubble hoverPopup="The format to display dates" />
				</label>
				<div class="col-6">
					<select class="form-control" id="date-format">
						<option>Loading, please wait...</option>
					</select>
				</div>
			</div>

			<div class="form-group row">
				<label for="time-format" class="col-6 col-form-label">
					Time Format<imaginationsupport:helpbubble hoverPopup="The format to display times" />
				</label>
				<div class="col-6">
					<select class="form-control" id="time-format">
						<option>Loading, please wait...</option>
					</select>
				</div>
			</div>

			<!-- alert -->
			<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>
		</form>
	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>
</body>
</html>
