<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="1.0"
				xmlns:lxslt="http://xml.apache.org/xslt"
				xmlns:stringutils="xalan://org.apache.tools.ant.util.StringUtils">
	<xsl:output method="html" indent="yes" encoding="US-ASCII" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" />
	<xsl:decimal-format decimal-separator="." grouping-separator="," />

	<xsl:param name="TITLE">Imagination Support JUnit Test Results</xsl:param>

	<xsl:template match="testsuites">
		<html>
			<head>
				<title>
					<xsl:value-of select="$TITLE" />
				</title>
				<style type="text/css">
					body
					{
						font:normal 68% verdana,arial,helvetica;
						color:#000000;
					}

					hr
					{
						width: 100%;
					}

					table
					{
						width: 100%;
					}

					table tr td, table tr th
					{
						font-size: 68%;
					}

					table.details tr th
					{
						font-weight: bold;
						background: #a6caf0;
					}

					table.details tr td
					{
						background: #eeeee0;
					}

					p
					{
						line-height:1.5em;
						margin-top:0.5em;
						margin-bottom:1.0em;
					}

					h1
					{
						margin: 0px 0px 5px;
						font: 165% verdana,arial,helvetica;
					}

					h2
					{
						margin-top: 1em;
						margin-bottom: 0.5em;
						font: bold 125% verdana,arial,helvetica;
					}

					h3
					{
						margin-bottom: 0.5em;
						font: bold 115% verdana,arial,helvetica;
					}

					h4
					{
						margin-bottom: 0.5em;
						font: bold 100% verdana,arial,helvetica;
					}

					h5
					{
						margin-bottom: 0.5em;
						font: bold 100% verdana,arial,helvetica;
					}

					h6
					{
						margin-bottom: 0.5em;
						font: bold 100% verdana,arial,helvetica;
					}

					th.column-test-suite-count
					{
						width: 80px;
					}

					th.column-test-name
					{
						width: 450px;
					}

					th.column-status
					{
						width: 120px;
					}

					th.column-elapsed
					{
						width: 100px;
					}
					
					.align-right
					{
						text-align: right;
					}

					.align-center
					{
						text-align: center;
					}

					.Error
					{
						font-weight:bold;
						color:red;
					}

					.Failure
					{
						font-weight:bold;
						color:red;
					}

				</style>
			</head>
			<body>
				<a name="top" />
				<xsl:call-template name="pageHeader" />

				<!-- Summary part -->
				<xsl:call-template name="summary" />
				<hr />

				<!-- Package List part -->
				<xsl:call-template name="packagelist" />
				<hr />

				<!-- For each package create its part -->
				<xsl:call-template name="packages" />
				<hr />

				<!-- For each class create the  part -->
				<xsl:call-template name="classes" />

			</body>
		</html>
	</xsl:template>


	<!-- ================================================================== -->
	<!-- Write a list of all packages with an hyperlink to the anchor of    -->
	<!-- of the package name.                                               -->
	<!-- ================================================================== -->
	<xsl:template name="packagelist">
		<h2>Packages</h2>
		Note: package statistics are not computed recursively, they only sum up all of its testsuites numbers.
		<table class="details" border="0" cellpadding="5" cellspacing="2">
			<xsl:call-template name="testsuite.test.header" />
			<!-- list all packages recursively -->
			<xsl:for-each select="./testsuite[not(./@package = preceding-sibling::testsuite/@package)]">
				<xsl:sort select="@package" />
				<xsl:variable name="testsuites-in-package" select="/testsuites/testsuite[./@package = current()/@package]" />
				<xsl:variable name="testCount" select="sum($testsuites-in-package/@tests)" />
				<xsl:variable name="errorCount" select="sum($testsuites-in-package/@errors)" />
				<xsl:variable name="failureCount" select="sum($testsuites-in-package/@failures)" />
				<xsl:variable name="timeCount" select="sum($testsuites-in-package/@time)" />

				<!-- write a summary for the package -->
				<tr valign="top">
					<!-- set a nice color depending if there is an error/failure -->
					<xsl:attribute name="class">
						<xsl:choose>
							<xsl:when test="$failureCount &gt; 0">Failure</xsl:when>
							<xsl:when test="$errorCount &gt; 0">Error</xsl:when>
						</xsl:choose>
					</xsl:attribute>
					<td>
						<a href="#{@package}">
							<xsl:value-of select="@package" />
						</a>
					</td>
					<td class="align-right">
						<xsl:value-of select="$testCount" />
					</td>
					<td class="align-right">
						<xsl:value-of select="$errorCount" />
					</td>
					<td class="align-right">
						<xsl:value-of select="$failureCount" />
					</td>
					<td>
						<xsl:call-template name="display-time">
							<xsl:with-param name="value" select="$timeCount" />
						</xsl:call-template>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>


	<!-- ================================================================== -->
	<!-- Write a package level report                                       -->
	<!-- It creates a table with values from the document:                  -->
	<!-- Name | Tests | Errors | Failures | Time                            -->
	<!-- ================================================================== -->
	<xsl:template name="packages">
		<!-- create an anchor to this package name -->
		<xsl:for-each select="/testsuites/testsuite[not(./@package = preceding-sibling::testsuite/@package)]">
			<xsl:sort select="@package" />
			<a name="{@package}" />
			<h3>Package
				<xsl:value-of select="@package" />
			</h3>

			<table class="details" border="0" cellpadding="5" cellspacing="2">
				<xsl:call-template name="testsuite.test.header" />

				<!-- match the testsuites of this package -->
				<xsl:apply-templates select="/testsuites/testsuite[./@package = current()/@package]" mode="print.test" />
			</table>
			<p />
			<p />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="classes">
		<xsl:for-each select="testsuite">
			<xsl:sort select="@name" />
			<!-- create an anchor to this class name -->
			<a name="{@name}" />
			<h3>TestCase
				<xsl:value-of select="@name" />
			</h3>

			<table class="details" border="0" cellpadding="5" cellspacing="2">
				<xsl:call-template name="testcase.test.header" />
				<!--
				test can even not be started at all (failure to load the class)
				so report the error directly
				-->
				<xsl:if test="./error">
					<tr class="Error">
						<td colspan="4">
							<xsl:apply-templates select="./error" />
						</td>
					</tr>
				</xsl:if>
				<xsl:apply-templates select="./testcase" mode="print.test" />
			</table>
			<p />

		</xsl:for-each>
	</xsl:template>

	<xsl:template name="summary">
		<h2>Summary</h2>
		<xsl:variable name="testCount" select="sum(testsuite/@tests)" />
		<xsl:variable name="errorCount" select="sum(testsuite/@errors)" />
		<xsl:variable name="failureCount" select="sum(testsuite/@failures)" />
		<xsl:variable name="timeCount" select="sum(testsuite/@time)" />
		<xsl:variable name="successRate" select="($testCount - $failureCount - $errorCount) div $testCount" />
		<table class="details" border="0" cellpadding="5" cellspacing="2">
			<tr valign="top">
				<th>Tests</th>
				<th>Failures</th>
				<th>Errors</th>
				<th>Success rate</th>
				<th>Time</th>
			</tr>
			<tr valign="top">
				<xsl:attribute name="class">
					<xsl:choose>
						<xsl:when test="$failureCount &gt; 0">Failure</xsl:when>
						<xsl:when test="$errorCount &gt; 0">Error</xsl:when>
					</xsl:choose>
				</xsl:attribute>
				<td class="align-right">
					<xsl:value-of select="$testCount" />
				</td>
				<td class="align-right">
					<xsl:value-of select="$failureCount" />
				</td>
				<td class="align-right">
					<xsl:value-of select="$errorCount" />
				</td>
				<td class="align-right">
					<xsl:call-template name="display-percent">
						<xsl:with-param name="value" select="$successRate" />
					</xsl:call-template>
				</td>
				<td class="align-right">
					<xsl:call-template name="display-time">
						<xsl:with-param name="value" select="$timeCount" />
					</xsl:call-template>
				</td>
			</tr>
		</table>
		<table border="0">
			<tr>
				<td style="text-align: justify;">
					Note: <i>failures</i> are anticipated and checked for with assertions while <i>errors</i> are unanticipated.
				</td>
			</tr>
		</table>
	</xsl:template>

	<!-- Page HEADER -->
	<xsl:template name="pageHeader">
		<h1>
			<xsl:value-of select="$TITLE" />
		</h1>
		<hr size="1" />
	</xsl:template>

	<xsl:template match="testsuite" mode="header">
		<tr valign="top">
			<th>Name</th>
			<th class="column-test-suite-count">Tests</th>
			<th class="column-test-suite-count">Errors</th>
			<th class="column-test-suite-count">Failures</th>
			<th nowrap="nowrap" class="column-elapsed">Elapsed</th>
		</tr>
	</xsl:template>

	<!-- class header -->
	<xsl:template name="testsuite.test.header">
		<tr valign="top">
			<th>Name</th>
			<th class="column-test-suite-count">Tests</th>
			<th class="column-test-suite-count">Errors</th>
			<th class="column-test-suite-count">Failures</th>
			<th nowrap="nowrap" class="column-elapsed">Elapsed</th>
		</tr>
	</xsl:template>

	<!-- method header -->
	<xsl:template name="testcase.test.header">
		<tr valign="top">
			<th class="column-test-name">Name</th>
			<th class="column-status">Status</th>
			<th>Details</th>
			<th nowrap="nowrap" class="column-elapsed">Elapsed</th>
		</tr>
	</xsl:template>

	<!-- class information -->
	<xsl:template match="testsuite" mode="print.test">
		<tr valign="top">
			<!-- set a nice color depending if there is an error/failure -->
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="@failures[.&gt; 0]">Failure</xsl:when>
					<xsl:when test="@errors[.&gt; 0]">Error</xsl:when>
				</xsl:choose>
			</xsl:attribute>

			<!-- print testsuite information -->
			<td>
				<a href="#{@name}">
					<xsl:value-of select="@name" />
				</a>
			</td>
			<td class="align-right">
				<xsl:value-of select="@tests" />
			</td>
			<td class="align-right">
				<xsl:value-of select="@errors" />
			</td>
			<td class="align-right">
				<xsl:value-of select="@failures" />
			</td>
			<td class="align-right">
				<xsl:call-template name="display-time">
					<xsl:with-param name="value" select="@time" />
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="testcase" mode="print.test">
		<tr valign="top">
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="failure | error">Error</xsl:when>
				</xsl:choose>
			</xsl:attribute>
			<td>
				<xsl:value-of select="@name" />
			</td>
			<xsl:choose>
				<xsl:when test="failure">
					<td>Failure</td>
					<td>
						<xsl:apply-templates select="failure" />
					</td>
				</xsl:when>
				<xsl:when test="error">
					<td>Error</td>
					<td>
						<xsl:apply-templates select="error" />
					</td>
				</xsl:when>
				<xsl:otherwise>
					<td>Success</td>
					<td />
				</xsl:otherwise>
			</xsl:choose>
			<td class="align-right">
				<xsl:call-template name="display-time">
					<xsl:with-param name="value" select="@time" />
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>


	<xsl:template match="failure">
		<xsl:call-template name="display-failures" />
	</xsl:template>

	<xsl:template match="error">
		<xsl:call-template name="display-failures" />
	</xsl:template>

	<!-- Style for the error and failure in the tescase template -->
	<xsl:template name="display-failures">
		<xsl:choose>
			<xsl:when test="not(@message)">N/A</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@message" />
			</xsl:otherwise>
		</xsl:choose>
		<!-- display the stacktrace -->
		<code>
			<br />
			<br />
			<xsl:call-template name="br-replace">
				<xsl:with-param name="word" select="." />
			</xsl:call-template>
		</code>
		<!-- the later is better but might be problematic for non-21" monitors... -->
		<!--pre><xsl:value-of select="."/></pre-->
	</xsl:template>

	<xsl:template name="JS-escape">
		<xsl:param name="string" />
		<xsl:param name="tmp1" select="stringutils:replace(string($string),'\','\\')" />
		<xsl:param name="tmp2" select="stringutils:replace(string($tmp1),&quot;'&quot;,&quot;\&apos;&quot;)" />
		<xsl:value-of select="$tmp2" />
	</xsl:template>

	<!--
		template that will convert a carriage return into a br tag
		@param word the text from which to convert CR to BR tag
	-->
	<xsl:template name="br-replace">
		<xsl:param name="word" />
		<xsl:value-of disable-output-escaping="yes" select='stringutils:replace(string($word),"&#xA;","&lt;br/>")' />
	</xsl:template>

	<xsl:template name="display-time">
		<xsl:param name="value" />
		<xsl:value-of select="format-number($value,'0.000')" />
	</xsl:template>

	<xsl:template name="display-percent">
		<xsl:param name="value" />
		<xsl:value-of select="format-number($value,'0.00%')" />
	</xsl:template>

</xsl:stylesheet>