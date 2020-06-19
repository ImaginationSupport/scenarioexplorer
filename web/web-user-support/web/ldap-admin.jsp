<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head pageJavaScriptPath="js/jsp-ldap-admin.js" minimalJS="true" />
<body>
<imaginationsupport:pageheader />
<imaginationsupport:maincontent title="LDAP Admin">
	<imaginationsupport:maincontentwithleftsidebar workingEntriesHolderId="working-users-holder" workingEntriesId="working-users">
		<form>
			<!-- username -->
			<div class="form-group row">
				<label for="username" class="col-3 col-form-label">Username</label>
				<div class="col-9">
					<input type="text" class="form-control" id="username" placeholder="Username">
				</div>
			</div>

			<!-- real name -->
			<div class="form-group row">
				<label for="realname" class="col-3 col-form-label">Real name</label>
				<div class="col-9">
					<input type="text" class="form-control" id="realname" placeholder="First Last">
				</div>
			</div>

			<!-- email address -->
			<div class="form-group row">
				<label for="email" class="col-3 col-form-label">Email address</label>
				<div class="col-9">
					<input type="text" class="form-control" id="email" placeholder="email@company.com">
				</div>
			</div>

			<!-- is site admin -->
			<div class="form-group row">
				<label for="is-site-admin" class="col-3 col-form-label">Site Admin</label>
				<div class="col-9">
					<div id="is-site-admin" data-toggle="buttons" class="btn-group btn-group-toggle">
						<label class="btn" id="is-site-admin-true-holder">
							<input type="radio" name="admin-bar" id="is-site-admin-true" autocomplete="off" />
							<span>Yes</span>
						</label>
						<label class="btn" id="is-site-admin-false-holder">
							<input type="radio" name="admin-bar" id="is-site-admin-false" autocomplete="off" />
							<span>No</span>
						</label>
					</div>
				</div>
			</div>

			<!-- email address -->
			<div class="form-group row">
				<label for="password" class="col-3 col-form-label">Password</label>
				<div class="col-9">
					<input type="password" class="form-control" id="password">
				</div>
			</div>

			<!-- user missing -->
			<div class="info alert-warning p-2 mb-3 border border-warning rounded d-none" id="form-user-missing">Warning: User is in LDAP, but not in Scenario Explorer.</div>

			<!-- alert -->
			<div class="alert alert-danger border border-danger d-none" id="form-alert"></div>

			<!-- buttons -->
			<div class="col-9 offset-3 pl-2 pr-0">
				<imaginationsupport:button id="button-add-update" text="Add" fontAwesomeIconClassName="fa-plus" />
				<imaginationsupport:button id="button-delete" text="Delete" fontAwesomeIconClassName="fa-trash" />

				<div class="float-right">
					<imaginationsupport:button id="button-add-scenario-explorer-user" text="Create Scenario Explorer User" fontAwesomeIconClassName="fa-user-circle" />
				</div>
			</div>

			<div class="col-9 offset-3 pl-2 pr-0 mt-5">
				<imaginationsupport:button id="button-export-ldif" text="Export LDIF" fontAwesomeIconClassName="fa-file-export" />
				<imaginationsupport:button id="button-export-csv" text="Export CSV" fontAwesomeIconClassName="fa-file-export" />
			</div>

		</form>
	</imaginationsupport:maincontentwithleftsidebar>
</imaginationsupport:maincontent>
</body>
</html>
