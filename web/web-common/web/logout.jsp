<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="imaginationsupport" uri="/WEB-INF/imagination-support-taglib.tld" %>
<!DOCTYPE html>
<%
	session.invalidate();

	response.setStatus( response.SC_TEMPORARY_REDIRECT );

	response.setHeader( "Location", "index.jsp" );
%>

