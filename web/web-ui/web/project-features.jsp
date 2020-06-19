<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-project-features.js" datePicker="true" plugins="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>

	<imaginationsupport:helpsidebarentry title="Project Features">
		Features are the key factors that analysts track as they interact with possible futures.
		Features have values that change over time and in response to the context (similar in concept to algebraic variables or logical fluents).
		While it is useful to have 5 features to start, you may add or change features at any time and Scenario Explorer will automatically update the project to include them.
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarshowmore>

		<imaginationsupport:helpsidebarentry title="Feature Name">
			Provide a feature name that clearly describes the variable, preferably a name that is independently recognizeable from the specific project.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Feature Description">
			While optional, a description of the feature will help collaborators understand its role in the project.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Feature Type">
			Currently, Scenario Explorer supports Boolean, multiple choice, integer, decimal, probability, and text feature types.
		</imaginationsupport:helpsidebarentry>

		<div id="sidebar-feature-type-config-help-holder"></div>

		<imaginationsupport:helpsidebarentry title="Projector">
			A projector is predictive model that forecasts future values of a feature. Example: A compound rate projector will tell the value based on an amortization schedule.
		</imaginationsupport:helpsidebarentry>

		<div id="sidebar-projector-config-help-holder"></div>
	</imaginationsupport:helpsidebarshowmore>

	<hr />

	<imaginationsupport:helptip>
		Part of the challenge of collaborating on a project is deciding on a common set of features that balance the relevant information without including extra or useless features. This can be an iterative process.
	</imaginationsupport:helptip>
</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Features" saveId="button-save" cancelId="button-cancel">

	<imaginationsupport:maincontentwithleftsidebar workingEntriesHolderId="working-features-holder" workingEntriesId="working-features">
		<form>
			<!-- feature name -->
			<div class="form-group row">
				<label for="feature-name" class="col-3 col-form-label">
					Name<imaginationsupport:helpbubble hoverPopup="The name of the feature" />
				</label>
				<div class="col-9">
					<input type="text" class="form-control" id="feature-name" placeholder="My Feature Name">
				</div>
			</div>

			<!-- feature description -->
			<div class="form-group row">
				<label for="feature-description" class="col-3 col-form-label">
					Description<imaginationsupport:helpbubble hoverPopup="A description of the feature (optional)" />
				</label>
				<div class="col-9">
					<textarea class="form-control" id="feature-description" rows="2" placeholder="Description of the Feature"></textarea>
				</div>
			</div>

			<!-- feature type -->
			<div class="form-group row">
				<label for="feature-type" class="col-3 col-form-label">
					Type<imaginationsupport:helpbubble hoverPopup="Select from Boolean, multiple choice, integer, probability, decimal, and text feature types" />
				</label>
				<div class="col-9">
					<select class="form-control" id="feature-type"></select>
				</div>
			</div>

			<!-- feature configuration -->
			<div class="form-group row">
				<div class="col-9 offset-3">
					<div id="feature-config" class="form-control plugin-config"></div>
				</div>
			</div>

			<!-- projector -->
			<div class="form-group row">
				<label for="projector-type" class="col-3 col-form-label">
					Projector<imaginationsupport:helpbubble hoverPopup="A projector will automatically change the feature's value over time" />
				</label>
				<div class="col-9">
					<select class="form-control" id="projector-type"></select>
				</div>
			</div>

			<!-- projector configuration -->
			<div class="form-group row">
				<div class="col-9 offset-3">
					<div id="projector-config" class="form-control plugin-config"></div>
				</div>
			</div>

			<!-- alert -->
			<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>

			<!-- buttons -->
			<div class="form-group row">
				<div class="col-9 offset-3">
					<imaginationsupport:button id="button-add-update-feature" text="Add" fontAwesomeIconClassName="fa-plus" />
					<imaginationsupport:button id="button-delete-feature" text="Delete" fontAwesomeIconClassName="fa-trash" />
				</div>
			</div>

		</form>
	</imaginationsupport:maincontentwithleftsidebar>
</imaginationsupport:maincontent>

</body>
</html>
