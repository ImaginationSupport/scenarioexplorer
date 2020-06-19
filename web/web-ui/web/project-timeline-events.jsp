<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-project-timeline-events.js" datePicker="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>

	<imaginationsupport:helptip>
		You can also upload a set of timeline events.
		Use <a href="timeline-events-upload-sample.csv" target="_blank">this sample</a> as a template.
	</imaginationsupport:helptip>

	<hr />

	<imaginationsupport:helpsidebarentry title="Project Timeline Events">
		Timeline events provide a linear time series of contextual situations to anchor the consistent parameters of the scenario.
		Timeline events may include seasons, elections, planned initiatives, or other known events that are not dependent on the actions or events in the project.
		<p></p>
		<b>Note:</b> There are two ways to add timeline events. You may either enter them individually or drag and drop a .csv file that contains multiple timeline events.
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarshowmore>

		<imaginationsupport:helpsidebarentry title="Name">
			Provide a short, clear name for the timeline event.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Description">
			While optional, a description will help collaborators understand the timeline event's role in the project.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Start Date">
			Identify when the event begins.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="End Date">
			Identify when the event ends.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="URL">
			Provide a URL with additional information about this event (optional).
			Collaborators will be able to access the URL.
		</imaginationsupport:helpsidebarentry>

	</imaginationsupport:helpsidebarshowmore>

</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Timeline Events" saveId="button-save" cancelId="button-cancel">
	<imaginationsupport:maincontentwithleftsidebar workingEntriesHolderId="working-timeline-events-holder" workingEntriesId="working-timeline-events">
		<form id="timeline-event-form">

			<!-- name -->
			<div class="form-group row">
				<label for="timeline-event-name" class="col-3 col-form-label">
					Name<imaginationsupport:helpbubble hoverPopup="The name of the timeline event" />
				</label>
				<div class="col-9">
					<input type="text" class="form-control" id="timeline-event-name" placeholder="Timeline Event Name">
				</div>
			</div>

			<!-- description -->
			<div class="form-group row">
				<label for="timeline-event-description" class="col-3 col-form-label">
					Description<imaginationsupport:helpbubble hoverPopup="The description of the timeline event (optional)" />
				</label>
				<div class="col-9">
					<textarea class="form-control" id="timeline-event-description" rows="2" placeholder="Description of the timeline event"></textarea>
				</div>
			</div>

			<!-- start -->
			<div class="form-group row">
				<label for="timeline-event-start-date" class="col-3 col-form-label">
					Start Date<imaginationsupport:helpbubble hoverPopup="The start date of the timeline event" />
				</label>
				<div class="col-9">
					<imaginationsupport:datepicker id="timeline-event-start-date" />
				</div>
			</div>

			<!-- end -->
			<div class="form-group row">
				<label for="timeline-event-end-date" class="col-3 col-form-label">
					End Date<imaginationsupport:helpbubble hoverPopup="The end date of the timeline event" />
				</label>
				<div class="col-9">
					<imaginationsupport:datepicker id="timeline-event-end-date" />
				</div>
			</div>

			<!-- url -->
			<div class="form-group row">
				<label for="timeline-event-url" class="col-3 col-form-label">
					URL<imaginationsupport:helpbubble hoverPopup="A URL for additional information (optional)" />
				</label>
				<div class="col-9">
					<input type="text" class="form-control" id="timeline-event-url" placeholder="URL">
				</div>
			</div>

			<!-- alert -->
			<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>

			<!-- buttons -->
			<div class="form-group row">
				<div class="col-9 offset-3">
					<imaginationsupport:button id="button-add-update" text="Add" />
					<imaginationsupport:button id="button-delete" text="Delete" />
				</div>
			</div>

		</form>

		<hr id="divider-line" />

		<div class="row border rounded py-2 mr-0" id="import-holder">
			<div class="col-3">
				<i class="fas fa-upload fa-10x text-dark"></i>
			</div>
			<div class="col-9 d-flex">
				<span class="align-self-center">
				<b>Drag and drop</b> a CSV file into this area to import a list of timeline events.<br />
				<br />
				For the proper layout, here is a <a href="timeline-events-upload-sample.csv" target="_blank">sample</a>.
				</span>
			</div>
		</div>

	</imaginationsupport:maincontentwithleftsidebar>
</imaginationsupport:maincontent>

</body>
</html>
