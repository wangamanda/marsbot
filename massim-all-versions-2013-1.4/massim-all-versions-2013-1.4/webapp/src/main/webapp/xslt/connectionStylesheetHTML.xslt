<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/transitional.dtd"/>

<xsl:template match="/">
<html>
<head>
<title>Connection Status</title>
<meta http-equiv="pragma" content="no-cache" />
<link rel="stylesheet" type="text/css" href="css/style.css" />

<script type="text/javascript" src="jquery.min.js"></script>
<script>
var auto_refresh = setInterval(
function() {
	$('#content').wrap('<div id="ajax"/>');
	$('#ajax').load("/massim/WebClientXMLServlet?status=1 #content", function(response, status, xhr) {
		if (status == "error") {
			location.reload();
		}
	});
	if ($('#content').size() == 0) {
		location.reload();
	}
}, 5000);
</script>
</head>

<body>
<xsl:import href="menu.xslt" />
<xsl:apply-imports/>
<xsl:apply-templates />
</body>
</html>
</xsl:template>

<xsl:template match="status">

<xsl:variable name="teams">
	<xsl:value-of select="count(client)"/>
</xsl:variable>

<xsl:variable name="pagebreak">
	<xsl:text disable-output-escaping="yes">
		<![CDATA[</table><table class="center left" border="1px">]]>
	</xsl:text>
</xsl:variable>

<xsl:variable name="agents">
	<xsl:for-each select="client[last()]">
		<xsl:value-of select="translate(@user, translate(@user, '0123456789', ''), '')"/>
	</xsl:for-each>
</xsl:variable>

<xsl:variable name="team1">
	<xsl:for-each select="client[1]">
		<xsl:value-of select="translate(@user,'0123456789','')"/>
	</xsl:for-each>
</xsl:variable>

<div id="content">
	<h1 class="center">Connection Status</h1>
	<table class="center left" border="1px">
	<xsl:for-each select="client">
		<xsl:if test="1=translate(@user, translate(@user, '0123456789', ''), '')">
			<th colspan="3">
				<xsl:value-of select="translate(@user,'0123456789','')"/>
			</th>
			<tr>
				<th>Agent</th>
				<th>IP</th>
				<th>Port</th>
			</tr>
		</xsl:if>
		<tr>
			<td><xsl:value-of select="@user"/></td>
			<td><xsl:value-of select="@ip"/></td>
			<td><xsl:value-of select="@port"/></td>
		</tr>
		<xsl:if test="28=translate(@user, translate(@user, '0123456789', ''), '')">
			<tr colspan="3"/>
			<xsl:value-of select="$pagebreak" disable-output-escaping="yes"/>
		</xsl:if>
		</xsl:for-each>
	</table>
</div>

<!-- div id="content1">
	<h1 class="center">Connection Status</h1>
	<table class="center left" border="1px">
		<tr><th colspan="3">
			<xsl:value-of select="$team1"/>
		</th></tr>
	<tr>
		<th>Agent</th>
		<th>IP</th>
		<th>Port</th>
	</tr>
		<xsl:for-each select="client">
		<xsl:if test="$team1=translate(@user,'0123456789','')">
		<tr>
			<td><xsl:value-of select="@user"/></td>
			<td><xsl:value-of select="@ip"/></td>
			<td><xsl:value-of select="@port"/></td>
		</tr>
		</xsl:if>
	</xsl:for-each>
	
	</table>
	<table class="center right" border="1px">
                <tr><th colspan="3">
		<xsl:for-each select="client[last()]">
			<xsl:value-of select="translate(@user,'0123456789','')"/>
		</xsl:for-each>
		</th></tr>

	<tr>
		<th>Agent</th>
		<th>IP</th>
		<th>Port</th>
	</tr>
	<xsl:for-each select="client">
		<xsl:if test="$team1!=translate(@user,'0123456789','')">
		<tr>
			<td><xsl:value-of select="@user"/></td>
			<td><xsl:value-of select="@ip"/></td>
			<td><xsl:value-of select="@port"/></td>
		</tr>
		</xsl:if>		
	</xsl:for-each>
	
	</table>

</div-->
<xsl:import href="footer.xslt" />
<xsl:apply-imports/>
</xsl:template>
</xsl:stylesheet>
