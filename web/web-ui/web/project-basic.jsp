<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-project-basic.js" datePicker="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>

	<imaginationsupport:helpsidebarentry title="Project Information">
		Enter a name, description, and timeframe for your analysis.
		These details provide a clear textual context for the project.
		A well-defined context will help you (and potential team members) stay focused on the goals of the project.
	</imaginationsupport:helpsidebarentry>

	<imaginationsupport:helpsidebarshowmore>

		<imaginationsupport:helpsidebarentry title="Project Name">
			The name identifies the specific scenario or research question that you and your team will be analyzing.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Description">
			A description of the project is useful for providing mission-specific context to collaborating team members.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Start Date">
			The start date creates the root node of the tree, also known as the now state.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="End Date">
			The end date maps the scenario to specific length of time, for example, 5 months or 5 years from the start date.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Increment">
			The increment provides a time resolution for the Scenario Explorer to anticipate changes (days, months, or years).
		</imaginationsupport:helpsidebarentry>

	</imaginationsupport:helpsidebarshowmore>

	<hr />

	<imaginationsupport:helptip>
		Select a dates well before and after the duration of your scenario.
	</imaginationsupport:helptip>
</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Create Project" saveId="button-save" cancelId="button-cancel" deleteId="button-delete">
	<imaginationsupport:maincontentsection id="create-project-holder">
		<form id="create-project-form">
			<%-- project name --%>
			<div class="form-group row">
				<label for="create-project-name" class="col-2 col-form-label">
					Name<imaginationsupport:helpbubble hoverPopup="Enter a name for the project" />
				</label>
				<div class="col-10">
					<input type="text" class="form-control" id="create-project-name" placeholder="My Project Name (must be unique)">
				</div>
			</div>

			<%-- project description --%>
			<div class="form-group row">
				<label for="create-project-description" class="col-2 col-form-label">
					Description<imaginationsupport:helpbubble hoverPopup="Provide context for the project" />
				</label>
				<div class="col-10">
					<textarea class="form-control" id="create-project-description" rows="2" placeholder="Description of the Project (optional)"></textarea>
				</div>
			</div>

			<imaginationsupport:accordionholder id="create-project-accordion">
				<imaginationsupport:accordion idSuffix="empty" title="Start with an Empty Project" parentId="create-project-accordion" expanded="true">
					<%-- start date --%>
					<div class="form-group row">
						<label for="create-project-start-date" class="col-2 col-form-label">
							Start Date<imaginationsupport:helpbubble hoverPopup="The starting date of the project" />
						</label>
						<div class="col-10">
							<imaginationsupport:datepicker id="create-project-start-date" />
						</div>
					</div>

					<%-- end date --%>
					<div class="form-group row">
						<label for="create-project-end-date" class="col-2 col-form-label">
							End Date<imaginationsupport:helpbubble hoverPopup="The ending date of the project" />
						</label>
						<div class="col-10">
							<imaginationsupport:datepicker id="create-project-end-date" />
						</div>
					</div>

					<%-- increment --%>
					<div class="form-group row">
						<label for="create-project-increment" class="col-2 col-form-label">
							Increment<imaginationsupport:helpbubble hoverPopup="The time resolution for the system to anticipate changes" />
						</label>
						<div class="col-10">
							<select class="form-control" id="create-project-increment" style="width:12rem">
								<option value="1">Day</option>
								<option value="7">Week</option>
								<option value="30" selected="selected">Month</option>
								<option value="365">Year</option>
							</select>
						</div>
					</div>
				</imaginationsupport:accordion>
				<imaginationsupport:accordion idSuffix="import" title="Or Import from a Save File (.json)" parentId="create-project-accordion">
					<div id="import-drag-drop-holder"></div>
				</imaginationsupport:accordion>
				<imaginationsupport:accordion idSuffix="from-template" title="Or Start from a Template" parentId="create-project-accordion">
					<table class="table table-striped table-bordered">
						<thead>
						<tr>
							<th class="bg-secondary text-light" scope="col"></th>
							<th class="bg-secondary text-light" scope="col">Name</th>
							<th class="bg-secondary text-light" scope="col">Created</th>
							<th class="bg-secondary text-light" scope="col">Creator</th>
							<th class="bg-secondary text-light" scope="col">Description</th>
						</tr>
						</thead>
						<tbody id="project-template">
						<tr>
							<td colspan="4">Loading, please wait...</td>
						</tr>
						</tbody>
					</table>
				</imaginationsupport:accordion>
			</imaginationsupport:accordionholder>
		</form>
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection id="edit-project-holder">
		<form id="edit-project-form">
			<%-- project name --%>
			<div class="form-group row">
				<label for="edit-project-name" class="col-2 col-form-label">
					Name<imaginationsupport:helpbubble hoverPopup="The name of the project (must be unique)" />
				</label>
				<div class="col-10">
					<input type="text" class="form-control" id="edit-project-name" placeholder="My Project Name">
				</div>
			</div>

			<%-- project description --%>
			<div class="form-group row">
				<label for="edit-project-description" class="col-2 col-form-label">
					Description<imaginationsupport:helpbubble hoverPopup="A description of the project (optional)" />
				</label>
				<div class="col-10">
					<textarea class="form-control" id="edit-project-description" rows="2" placeholder="Description of the project"></textarea>
				</div>
			</div>

			<%-- start date --%>
			<div class="form-group row">
				<label for="edit-project-start-date" class="col-2 col-form-label">
					Start Date<imaginationsupport:helpbubble hoverPopup="Select a date well before the time frame of your analysis" />
				</label>
				<div class="col-10">
					<imaginationsupport:datepicker id="edit-project-start-date" />
				</div>
			</div>

			<%-- end date --%>
			<div class="form-group row">
				<label for="edit-project-end-date" class="col-2 col-form-label">
					End Date<imaginationsupport:helpbubble hoverPopup="Select an end date well after the time frame of your analysis" />
				</label>
				<div class="col-10">
					<imaginationsupport:datepicker id="edit-project-end-date" />
				</div>
			</div>

			<%-- increment --%>
			<div class="form-group row">
				<label for="edit-project-increment" class="col-2 col-form-label">
					Increment<imaginationsupport:helpbubble hoverPopup="Select the level of timeline detail" />
				</label>
				<div class="col-10">
					<select class="form-control" id="edit-project-increment" style="width:12rem">
						<option value="1">Day</option>
						<option value="7">Week</option>
						<option value="30" selected="selected">Month</option>
						<option value="365">Year</option>
					</select>
				</div>
			</div>
		</form>

	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection id="delete-project-confirm">
		<p>Are you sure you want to delete this project? This action cannot be undone.</p>
		<imaginationsupport:button id="delete-project-confirm-delete" text="Delete" fontAwesomeIconClassName="fa-trash" />
		<imaginationsupport:button id="delete-project-confirm-cancel" text="Cancel" fontAwesomeIconClassName="fa-times" />
	</imaginationsupport:maincontentsection>

	<imaginationsupport:maincontentsection cssClasses="py-0">
		<%-- alert --%>
		<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>
	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>

</body>
</html>
