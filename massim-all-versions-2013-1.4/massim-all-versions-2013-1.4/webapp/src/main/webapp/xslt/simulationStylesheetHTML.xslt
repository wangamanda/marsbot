<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/transitional.dtd"/>

<xsl:template match="/">
<html>
<head>
<title>Current Simulation</title>
<meta http-equiv="pragma" content="no-cache" />

<link rel="stylesheet" type="text/css" href="css/style.css" />

<xsl:element name="link">
	<xsl:attribute name="rel">stylesheet</xsl:attribute>
	<xsl:attribute name="type">text/css</xsl:attribute>
	<xsl:attribute name="href">
		<xsl:for-each select="simulationstate">
			<xsl:value-of select="concat(substring-before(substring-after(@output2, '/home/massim/www/webapps/massim/'),'masSim-0.svg'),'style.css')"/>
		</xsl:for-each>
	</xsl:attribute>

</xsl:element>

<script type="text/javascript" src="jquery.min.js"></script>

<script>
var auto_refresh = setInterval(
function() {
	$('#image').wrap('<div id="ajax"/>');
	$('#ajax').load("/massim/WebClientXMLServlet?status=0 #image", function(response, status, xhr) {
		if (status == "error") {
			location.reload();
		}
	});
	if ($('#image').children().length == 0) {
		location.reload();
	};
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

<xsl:template match="simulationstate">
<div id="content">
	<xsl:element name="svg">
		<xsl:attribute name="id">image</xsl:attribute>
		<xsl:choose>
			<xsl:when test="contains(@output,'masSim')">
			        <xsl:choose>
					<xsl:when test="document(concat('../',substring-after(@output, '/home/massim/www/webapps/massim/')))">
						<xsl:copy-of select="document(concat('../',substring-after(@output, '/home/massim/www/webapps/massim/')))"/>
				        </xsl:when>
			        	<xsl:otherwise/>
			        </xsl:choose>
			</xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:element>

	<xsl:element name="object">
		<xsl:attribute name="data">
			<xsl:value-of select="substring-after(@output2, '/home/massim/www/webapps/massim/')"/>
		</xsl:attribute>
		<xsl:attribute name="type">image/svg+xml</xsl:attribute>
		<xsl:attribute name="id">background</xsl:attribute>
	</xsl:element>
</div>
<xsl:import href="footer.xslt" />
<xsl:apply-imports/>
</xsl:template>
</xsl:stylesheet>
