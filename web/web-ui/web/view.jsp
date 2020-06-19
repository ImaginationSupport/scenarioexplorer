<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-view.js" datatables="true" slider="true" plugins="true" viewCanvas="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<div class="container" id="main-content-holder">
	<div id="view-canvas-holder" class="d-none">
		<canvas></canvas>
	</div>
	<div id="view-canvas-side-bar" class="d-none">

		<div class="pl-2">
			<imaginationsupport:helptip>
				Welcome to your view!
				<imaginationsupport:helpsidebarshowmore>
					Your initial scenario in Futures Building will show one line connecting the root state to the end state.
					Timeline events are at the bottom of the view. To introduce branching, add a conditioning event by clicking on the tab below.
				</imaginationsupport:helpsidebarshowmore>
			</imaginationsupport:helptip>
		</div>

		<hr />

		<ul class="nav nav-tabs" id="side-bar-tabs" role="tablist">
			<li class="nav-item">
				<a class="nav-link active"
				   id="states-tab"
				   data-toggle="tab"
				   href="#states-tab-body"
				   role="tab"
				   aria-controls="states-tab-body" aria-selected="true">States</a>
			</li>
			<li class="nav-item">
				<a class="nav-link"
				   id="conditioning-events-tab"
				   data-toggle="tab"
				   href="#conditioning-events-tab-body"
				   role="tab"
				   aria-controls="conditioning-events-tab-body"
				   aria-selected="false">Conditioning Events</a>
			</li>
			<li class="nav-item">
				<a class="nav-link"
				   id="timeline-events-tab"
				   data-toggle="tab"
				   href="#timeline-events-tab-body"
				   role="tab"
				   aria-controls="timeline-events-tab-body"
				   aria-selected="false">Timeline Events</a>
			</li>
		</ul>
		<div class="tab-content">

			<%-- ###### States ###################################################################################### --%>

			<div class="tab-pane fade show active p-2" id="states-tab-body" role="tabpanel" aria-labelledby="states-tab">
				<div id="states-list">
					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<label class="input-group-text" for="states-search-box">Search</label>
						</div>
						<input type="text" class="form-control" id="states-search-box">
					</div>

					<table class="table table-striped table-bordered table-hover">
						<thead>
						<tr>
							<th class="bg-secondary text-light" scope="col">Name</th>
							<th class="bg-secondary text-light" scope="col">Start</th>
							<th class="bg-secondary text-light" scope="col">End</th>
						</tr>
						</thead>
						<tbody id="states-tbody">
					</table>
				</div>
				<div id="state-details" class="d-none">
					<table class="table table-striped table-bordered">
						<thead>
						<tr>
							<th class="bg-secondary text-light" scope="col">Feature</th>
							<th class="bg-secondary text-light" scope="col">Value</th>
						</tr>
						</thead>
						<tbody id="state-details-tbody">
						</tbody>
					</table>
					<div>
						<imaginationsupport:button text="Back to list" id="state-details-back-to-list" fontAwesomeIconClassName="fa-chevron-circle-left" />
					</div>
				</div>
			</div>

			<%-- ###### Conditioning Events ######################################################################### --%>

			<div class="tab-pane fade p-2" id="conditioning-events-tab-body" role="tabpanel" aria-labelledby="conditioning-events-tab">
				<div id="conditioning-events-list">
					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<label class="input-group-text" for="conditioning-events-search-box">Search</label>
						</div>
						<input type="text" class="form-control" id="conditioning-events-search-box">
					</div>

					<table class="table table-striped table-bordered table-hover">
						<thead>
						<tr>
							<th class="bg-secondary text-light" scope="col">Name</th>
							<th class="bg-secondary text-light" scope="col">View</th>
						</tr>
						</thead>
						<tbody id="conditioning-events-tbody">
					</table>

					<div class="text-right">
						<imaginationsupport:button text="New Conditioning Event" id="conditioning-event-new-button" fontAwesomeIconClassName="fa-plus" />
					</div>
				</div>
				<div id="conditioning-event-details" class="d-none">
					<table class="table table-striped table-bordered">
						<tbody id="conditioning-event-details-tbody">
						</tbody>
					</table>
					<div>
						<imaginationsupport:button text="Back to list" id="conditioning-event-details-back-to-list" fontAwesomeIconClassName="fa-chevron-circle-left" />
						<div class="float-right">
							<imaginationsupport:button text="Assign" id="conditioning-event-details-assign" fontAwesomeIconClassName="fa-plus" />
							<imaginationsupport:button text="Delete" id="conditioning-event-details-delete" fontAwesomeIconClassName="fa-trash" />
							<imaginationsupport:button text="Edit" id="conditioning-event-details-edit" fontAwesomeIconClassName="fa-edit" />
						</div>
					</div>
				</div>
			</div>

			<%-- ###### Timeline Events ############################################################################# --%>

			<div class="tab-pane fade p-2" id="timeline-events-tab-body" role="tabpanel" aria-labelledby="timeline-events-tab">
				<div id="timeline-events-list">
					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<label class="input-group-text" for="timeline-events-search-box">Search</label>
						</div>
						<input type="text" class="form-control" id="timeline-events-search-box">
					</div>

					<table class="table table-striped table-bordered table-hover">
						<thead>
						<tr>
							<th class="bg-secondary text-light" scope="col">Name</th>
							<th class="bg-secondary text-light" scope="col">Start</th>
							<th class="bg-secondary text-light" scope="col">End</th>
						</tr>
						</thead>
						<tbody id="timeline-events-tbody">
					</table>

					<div class="text-right">
						<imaginationsupport:button text="New Timeline Event" id="timeline-event-new-button" fontAwesomeIconClassName="fa-plus" />
					</div>
				</div>
				<div id="timeline-event-details" class="d-none">
					<table class="table table-striped table-bordered">
						<tbody id="timeline-event-details-tbody">
						</tbody>
					</table>
					<div>
						<imaginationsupport:button text="Back to list" id="timeline-event-details-back-to-list" fontAwesomeIconClassName="fa-chevron-circle-left" />
						<div class="float-right">
							<imaginationsupport:button text="Edit" id="timeline-event-details-edit" fontAwesomeIconClassName="fa-edit" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="stats" class="d-none"></div>
</div>
<div class="container d-none" id="error-display">
	<div class="card mt-2 border-danger">
		<div class="card-header bg-danger text-light h4">
			<span class="align-middle">Error!</span>
		</div>
		<div class="card-body">
			<div id="error-message"></div>
			<pre id="error-exception-stacktrace" class="border rounded border-danger p-2 mt-3"></pre>
		</div>
	</div>
</div>
</body>
</html>
