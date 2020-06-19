<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-project-details.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar notifications="true">

	<imaginationsupport:helptip>
		You can expand or collapse a section by clicking on the corresponding header (Project Details, Views, Features, etc.).
	</imaginationsupport:helptip>

	<hr />

	<imaginationsupport:helpsidebarentry title="Welcome to your Project Dashboard">
		This page gives you access to all of the components of analysis. Start by adding <b>features</b> (important variables for the scenario) and <b>timeline
		events</b> to constrain your project. Then, see how your scenario plays out by creating a Futures Building <b>analytic view.</b>
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarshowmore>

		<imaginationsupport:helpsidebarentry title="Project Details">
			This section provides the overall context for the project
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Analytic Views">
			After inputting features and timeline events, start exploring the scenario using one of our analytic techniques (i.e. Futures Building or Smart Query).
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Features">
			Features are the key variables that analysts track as they interact with possible futures. Scenario Explorer currently supports Boolean, integer, multiple choice, probability, and text features.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Timeline Events">
			Timeline events provide consistent context since they occur regardless of which future we are in.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Access">
			The project owner may set permissions for collaborators to view and edit the project.
		</imaginationsupport:helpsidebarentry>

	</imaginationsupport:helpsidebarshowmore>

</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Project" exportId="export-button" exportLabel="Export">

	<imaginationsupport:maincontentsection id="loading-project">
		<p>Loading project, please wait...</p>
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection id="project-details" cssClasses="d-none">
		<imaginationsupport:collapsiblesection
			title="Project Details"
			bodyId="project-basic-details"
			initiallyExpanded="true"
			headerCssClasses="h3"
			bodyCssClasses="bg-light border border-secondary rounded p-3 mb-3">
			Loading, please wait...
		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Analytic Views"
			bodyId="project-views"
			initiallyExpanded="true"
			headerCssClasses="h3"
			bodyCssClasses="bg-light border border-secondary rounded p-3 mb-3">
			Loading, please wait...
		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Features"
			bodyId="project-features"
			initiallyExpanded="false"
			headerCssClasses="h3"
			bodyCssClasses="bg-light border border-secondary rounded p-3 mb-3">
			Loading, please wait...
		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Timeline Events"
			bodyId="project-timeline-events"
			initiallyExpanded="false"
			headerCssClasses="h3"
			bodyCssClasses="bg-light border border-secondary rounded p-3 mb-3">
			Loading, please wait...
		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Access"
			bodyId="project-access"
			initiallyExpanded="false"
			headerCssClasses="h3"
			bodyCssClasses="bg-light border border-secondary rounded p-3 mb-3">
			Loading, please wait...
		</imaginationsupport:collapsiblesection>

		<imaginationsupport:collapsiblesection
			title="Templates"
			bodyId="project-templates"
			initiallyExpanded="false"
			headerCssClasses="h3"
			bodyCssClasses="bg-light border border-secondary rounded p-3 mb-3">
			Loading, please wait...
		</imaginationsupport:collapsiblesection>
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection id="clone-project-holder" cssClasses="d-none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Cloning project...</span>
		</div>
		<span class="m-2">Cloning project, please wait...</span>
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection id="delete-confirm" cssClasses="d-none">
		<p>Are you sure you want to delete <span id="delete-confirm-name"></span>?&nbsp;&nbsp;This action cannot be undone.</p>
		<imaginationsupport:button id="delete-confirm-delete" text="Delete" fontAwesomeIconClassName="fa-trash" />
		<imaginationsupport:button id="delete-confirm-cancel" text="Cancel" fontAwesomeIconClassName="fa-times" />
	</imaginationsupport:maincontentsection>

</imaginationsupport:maincontent>

</body>
</html>
