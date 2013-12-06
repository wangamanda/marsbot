<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<div id="nav">
<ul id="navigation">
        <li><a href="WebClientXMLServlet?status=0" target="_self">Current Simulation</a></li>
        <li><a href="WebClientXMLServlet?status=1" target="_self">Connection Status</a></li>
        <li><a href="WebClientTournamentServlet" target="_self">Results</a></li>
<!--
       <li><a href="results.html" target="_self">Results</a></li>
-->
</ul>
</div> 
</xsl:template>

</xsl:stylesheet> 
