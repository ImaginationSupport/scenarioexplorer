<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-view-create-update.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>

	<imaginationsupport:helpsidebarentry title="Analytic Views">
		Views allow you to visualize the scenario given a specific type of analysis.
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarentry title="View: Futures Building">
		<b>Futures Building</b> allows you to systematically think through potential conditioning events and their outcomes using forward chaining.
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarentry title="View: Smart Query">
		<b>Smart Query</b> allows you to identify key indicators and warnings for different classes of futures based on specific features.
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarshowmore>

		<imaginationsupport:helpsidebarentry title="Name">
			Name the view based on the specific focus of your analysis.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Description">
			Describe the goals of the analysis.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="View Type">
			Currently, Scenario Explorer supports Futures Building and Smart Query view types.
		</imaginationsupport:helpsidebarentry>

		<div id="view-config-help-smart-query" class="d-none">
			<imaginationsupport:helpsidebarentry title="Feature">
				The Feature to evaluate for the Smart Query - The system will look at all other features to determine which conditioning events affect the outcome the most.
			</imaginationsupport:helpsidebarentry>
		</div>

	</imaginationsupport:helpsidebarshowmore>
</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Create View" saveId="button-save" cancelId="button-cancel" deleteId="button-delete">

	<imaginationsupport:maincontentsection>
		<form id="view-form">
			<!-- view name -->
			<div class="form-group row">
				<label for="view-name" class="col-2 col-form-label">
					Name<imaginationsupport:helpbubble hoverPopup="Name the view based on the specific focus of your analysis" />
				</label>
				<div class="col-10">
					<input type="text" class="form-control" id="view-name" placeholder="My View Name">
				</div>
			</div>

			<!-- view description -->
			<div class="form-group row">
				<label for="view-description" class="col-2 col-form-label">
					Description<imaginationsupport:helpbubble hoverPopup="Describe the goals of the analysis" />
				</label>
				<div class="col-10">
					<textarea class="form-control" id="view-description" rows="2" placeholder="Description of the View (optional)"></textarea>
				</div>
			</div>

			<!-- view type -->
			<div class="form-group row">
				<label for="view-type" class="col-2 col-form-label">
					Type<imaginationsupport:helpbubble hoverPopup="Select the type of view" />
				</label>
				<div class="col-10">
					<select class="form-control" id="view-type" style="width:16rem"></select>

					<div class="d-none mt-2" id="view-config-smart-query">
						<div class="form-group row">
							<label for="smart-query-feature" class="col-2 col-form-label">
								Feature<imaginationsupport:helpbubble hoverPopup="The feature to evaluate for this smart query" />
							</label>
							<select class="form-control col-6" id="smart-query-feature"></select>
							<div class="col-4 col-form-label" id="smart-query-feature-mode"></div>
						</div>
					</div>
				</div>
			</div>

			<!-- alert -->
			<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>
		</form>
	</imaginationsupport:maincontentsection>

</imaginationsupport:maincontent>

</body>
</html>
