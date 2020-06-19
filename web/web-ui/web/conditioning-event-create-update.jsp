<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-conditioning-event-create-update.js" datePicker="true" slider="true" plugins="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:breadcrumbs />
<imaginationsupport:sidebar>
	<div id="help-sidebar-conditioning-event" class="d-none">

		<imaginationsupport:helpsidebarentry title="Project Conditioning Events">
			A conditioning event (CE) is input by the user to introduce branching to the tree of futures. Scenario Explorer then places the CE in all available
			locations on tree and as a result, t context changes for every node after it in any trajectory. Analysts then can see how features change as an effect of the CE placement.
		</imaginationsupport:helpsidebarentry>

		<hr />
		<imaginationsupport:helpsidebarentry title="Name">
			Provide a short, clear name for the conditioning event.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Description">
			While optional, a description will help collaborators understand and remember the conditioning event's role in the project.
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Outcomes">
			Every conditioning event has multiple potential outcomes, each outcome has some impact on some set of features.
		</imaginationsupport:helpsidebarentry>
	</div>

	<div id="help-sidebar-precondition" class="d-none">
		<imaginationsupport:helpsidebarentry title="Depends on">
			The type of precondition to use
		</imaginationsupport:helpsidebarentry>

		<div id="help-sidebar-precondition-config"></div>
	</div>

	<div id="help-sidebar-outcome" class="d-none">
		<imaginationsupport:helpsidebarentry title="Name">
			The name of the outcome
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Description">
			The description of the outcome (optional)
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Likelihood">
			The probability that outcome could occur
		</imaginationsupport:helpsidebarentry>

		<imaginationsupport:helpsidebarentry title="Effects">
			The list of effects if this outcome happens
		</imaginationsupport:helpsidebarentry>
	</div>
</imaginationsupport:sidebar>
<imaginationsupport:maincontent title="Conditioning Events" saveId="button-save" cancelId="button-cancel">
	<imaginationsupport:maincontentwithleftsidebar workingEntriesHolderId="working-conditioning-events-holder" workingEntriesId="working-conditioning-events">
		<form>
			<div id="pane-conditioning-event">

				<!-- name -->
				<div class="form-group row">
					<label for="conditioning-event-name" class="col-3 col-form-label">
						Name<imaginationsupport:helpbubble hoverPopup="The name of the conditioning event" />
					</label>
					<div class="col-9">
						<input type="text" class="form-control" id="conditioning-event-name" placeholder="Conditioning Event Name">
					</div>
				</div>

				<!-- description -->
				<div class="form-group row">
					<label for="conditioning-event-description" class="col-3 col-form-label">
						Description<imaginationsupport:helpbubble hoverPopup="The description of the conditioning event (optional)" />
					</label>
					<div class="col-9">
						<textarea class="form-control" id="conditioning-event-description" rows="2" placeholder="Description of the conditioning event (optional)"></textarea>
					</div>
				</div>

				<!-- outcome sliders -->
				<div class="form-group row">
					<div class="col-3">
						Outcomes<imaginationsupport:helpbubble hoverPopup="The list of possible outcomes" />
					</div>
					<div class="col-9">
						<div id="conditioning-event-outcomes-summary" class="form-control" style="min-height:100px">Loading, please wait...</div>
					</div>
				</div>

				<!-- buttons -->
				<div class="text-right">
					<imaginationsupport:button id="conditioning-event-delete-button" text="Delete" fontAwesomeIconClassName="fa-trash" />
				</div>

			</div>

			<!-- #################################################################################################### -->

			<div id="pane-precondition" class="d-none">

				<!-- type -->
				<div class="form-group row">
					<label for="precondition-type" class="col-3 col-form-label">
						Depends on<imaginationsupport:helpbubble hoverPopup="The type of precondition" />
					</label>
					<div class="col-9">
						<select class="form-control" id="precondition-type"></select>
					</div>
				</div>

				<!-- config -->
				<div class="form-group row">
					<div class="col-9 offset-3">
						<div id="precondition-config" class="form-control plugin-config"></div>
					</div>
				</div>

				<!-- buttons -->
				<div class="text-right">
					<imaginationsupport:button id="precondition-delete-button" text="Delete" fontAwesomeIconClassName="fa-trash" />
				</div>

			</div>

			<!-- #################################################################################################### -->

			<div id="pane-outcome" class="d-none">

				<!-- name -->
				<div class="form-group row">
					<label for="outcome-name" class="col-3 col-form-label">
						Name<imaginationsupport:helpbubble hoverPopup="The name of the outcome" />
					</label>
					<div class="col-9">
						<input type="text" class="form-control" id="outcome-name" placeholder="Outcome Name">
					</div>
				</div>

				<!-- description -->
				<div class="form-group row">
					<label for="outcome-description" class="col-3 col-form-label">
						Description<imaginationsupport:helpbubble hoverPopup="The description of the outcome (optional)" />
					</label>
					<div class="col-9">
						<textarea class="form-control" id="outcome-description" rows="2" placeholder="Description of the outcome (optional)"></textarea>
					</div>
				</div>

				<!-- likelihood -->
				<div class="form-group row">
					<label for="outcome-likelihood-slider" class="col-3 col-form-label">
						Likelihood<imaginationsupport:helpbubble hoverPopup="The likelihood of this outcome happening" />
					</label>
					<div class="col-7 pt-1">
						<input type="text" id="outcome-likelihood-slider" />
					</div>
					<div class="col-2 text-right" id="outcome-likelihood-value"></div>
				</div>

				<!-- effects -->
				<div class="form-group row">
					<label for="outcome-effects" class="col-3 col-form-label">
						Effects<imaginationsupport:helpbubble hoverPopup="The list of effects when this outcome happens" />
					</label>
					<div class="col-9 pt-2">
						<div id="outcome-effects" class="form-control" style="min-height:100px"></div>
					</div>
				</div>

				<!-- add effect button -->
				<div class="text-right">
					<imaginationsupport:button id="outcome-pane-delete-button" text="Delete" fontAwesomeIconClassName="fa-trash" />
					<imaginationsupport:button id="outcome-pane-add-effect-button" text="Add Effect" fontAwesomeIconClassName="fa-plus" />
				</div>

			</div>

			<!-- #################################################################################################### -->

			<!-- alert -->
			<div class="alert alert-danger d-none mt-3" id="form-error-alert"></div>

		</form>
	</imaginationsupport:maincontentwithleftsidebar>
</imaginationsupport:maincontent>
</body>
</html>
