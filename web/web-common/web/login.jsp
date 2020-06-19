<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<html lang="en">
<!--
	App:   {{app.name}} {{app.version}}
	Built: {{build.time}}({{build.number}})
	Src:   {{git.branch}}:{{git.commit.id}}
-->
<imaginationsupport:head minimalJS="true" />
<body>
<imaginationsupport:pageheader />
<div class="container">
	<div class="text-center">
		<img class="my-5 img-fluid" src="img/logo-large.png" alt="{{app.name}} logo">
	</div>
	<div class="row">
		<div class="col-4 offset-4 text-center">
			<form method="POST" action="j_security_check" accept-charset="UTF-8">
				<h1 class="h3 mb-4 font-weight-normal">Welcome!&nbsp;&nbsp;Please sign in:</h1>

				<div class="form-group">
					<label for="j_username" class="sr-only">Email address</label>
					<input type="text" name="j_username" id="j_username" class="form-control" placeholder="Username" required>
				</div>

				<div class="form-group">
					<label for="j_password" class="sr-only">Password</label>
					<input type="password" id="j_password" name="j_password" class="form-control" placeholder="Password" required>
				</div>

				<div class="form-group">
					<button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
				</div>

				<div class="mt-5">
					<i class="fas fa-info-circle fa-2x float-left mt-2 text-primary"></i>
					<div class="ml-3">If you have an account and need to reset your password, <a href="{{web-user-support.deploy.path}}">click here</a>.</div>
				</div>
			</form>
		</div>
	</div>
</div>
</body>
</html>