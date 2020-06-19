<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head minimalJS="true" pageJavaScriptPath="js/jsp-reset-password.js" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:maincontent title="Reset Password" cssClasses="narrow-main-content-holder">
	<imaginationsupport:maincontentsection>
		<form id="reset-password-form" action="backend" method="post" accept-charset="UTF-8">
			<input type="hidden" name="action" value="reset" />

			<!-- user name -->
			<div class="form-group row">
				<label for="form-username" class="col-3 col-form-label">Username</label>
				<div class="col-9">
					<input type="text" class="form-control" id="form-username" name="username">
				</div>
			</div>

			<div class="form-group row">
				<div class="col-9 offset-3">or</div>
			</div>

			<!-- email -->
			<div class="form-group row ">
				<label for="form-email" class="col-3 col-form-label">Email</label>
				<div class="col-9">
					<input type="text" class="form-control" id="form-email" name="email">
				</div>
			</div>

			<!-- alert -->
			<div class="alert alert-danger border border-danger d-none" id="form-error-alert"></div>

			<!-- button bar -->
			<div class="form-group row">
				<div class="col-9 offset-3">
					<imaginationsupport:button id="form-submit" text="Reset Password" fontAwesomeIconClassName="fa-wrench" />
					<imaginationsupport:button id="form-cancel" text="Cancel" fontAwesomeIconClassName="fa-times" />
				</div>
			</div>
		</form>
	</imaginationsupport:maincontentsection>
</imaginationsupport:maincontent>
</body>
</html>
