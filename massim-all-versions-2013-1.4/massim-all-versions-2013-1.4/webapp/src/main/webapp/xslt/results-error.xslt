<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/transitional.dtd"/>

<xsl:template match="/">

<html>
<head>
<title>Results</title>
<script type="text/javascript" src="jquery.min.js"></script>
<script>
var auto_refresh = setInterval(
function(){
$.ajax({
    type: 'HEAD',
    url: 'index.html',
    success: function() {
             window.location.href='index.html';
    },  
    error: function() {
             location.reload();
    }
});
},5000);
</script>

<link rel="stylesheet" type="text/css" href="css/style.css" />
</head>

<body>
<xsl:import href="menu.xslt" />
<xsl:apply-imports/>
<xsl:apply-templates />
</body>
</html>
</xsl:template>

<xsl:template match="error">
<div id="content-error">
	<h1 class="center">Results</h1>
	<p class="center">This page automatically reloads every 5 seconds!</p>
	<p class="center"><xsl:value-of select="@message"/></p>
</div>
<xsl:import href="footer.xslt" />
<xsl:apply-imports/>
</xsl:template>
</xsl:stylesheet>
