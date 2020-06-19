<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head minimalJS="true" pageJavaScriptPath="js/jsp-change-password.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:maincontent title="Change Password" cssClasses="narrow-main-content-holder">
	<imaginationsupport:maincontentsection>

		<form id="reset-password-form" action="backend" method="post" accept-charset="UTF-8">
			<input type="hidden" name="action" value="change" />

			<!-- user name -->
			<div class="form-group row">
				<label for="form-username" class="col-4 col-form-label">Username</label>
				<div class="col-8">
					<input type="text" class="form-control" id="form-username" name="username">
				</div>
			</div>

			<!-- current password -->
			<div class="form-group row">
				<label for="form-current-password" class="col-4 col-form-label">Current Password</label>
				<div class="col-8">
					<input type="password" class="form-control" id="form-current-password" name="current">
				</div>
			</div>

			<!-- new password -->
			<div class="form-group row">
				<label for="form-new-password" class="col-4 col-form-label">New Password</label>
				<div class="col-8">
					<input type="password" class="form-control" id="form-new-password" name="new">
				</div>
			</div>

			<!-- new password again -->
			<div class="form-group row">
				<label for="form-new-password-again" class="col-4 col-form-label">New Password again</label>
				<div class="col-8">
					<input type="password" class="form-control" id="form-new-password-again" name="again">
				</div>
			</div>

			<!-- alert -->
			<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>

			<!-- button bar -->
			<div class="form-group row">
				<div class="col-8 offset-4">
					<imaginationsupport:button id="form-submit" text="Change Password" fontAwesomeIconClassName="fa-wrench" />
					<imaginationsupport:button id="form-cancel" text="Cancel" fontAwesomeIconClassName="fa-times" />
				</div>
			</div>
		</form>

	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>
</body>
</html>
