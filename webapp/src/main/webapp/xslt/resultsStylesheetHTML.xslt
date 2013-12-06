<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/transitional.dtd"/>

<xsl:template match="/">
<html>
<head>
<title>Results</title>
<meta http-equiv="pragma" content="no-cache" />
<link rel="stylesheet" type="text/css" href="css/style.css" />

<script type="text/javascript" src="jquery.min.js"></script>
<script>
var auto_refresh = setInterval(
function() {
	$('#content').wrap('<div id="ajax"/>');
	$('#ajax').load("/massim/WebClientTournamentServlet #content", function(response, status, xhr) {
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

<xsl:template match="tournament">
<div id="content">
	<h1 class="center">Results</h1>
	<br />
	
	<xsl:for-each select="ranking">
	<table class="center" border="1">
	<tr>
		<th>Pos.</th>
		<th>Teamname</th>
		<th>Score</th>
		<th>Difference</th>
		<th>Matches</th>
		<th>Simulations</th>
		<th>Points</th>
	</tr>
		<xsl:for-each select="rank">
			<xsl:sort select="@position" />
	        <xsl:sort select="@diffGold" />
		
			<tr>
				<td style="background-color:#e6e6e6;"><xsl:value-of select="@position"/></td>
				<td style="background-color:#e6e6e6;"><xsl:value-of select="@teamname"/></td>
				<xsl:variable name="teamname" select="@teamname"/>
				<td><xsl:value-of select='format-number(@ownGold, "#0")'/> : <xsl:value-of select='format-number(@enemyGold, "#0")'/></td>
				<td><xsl:value-of select='format-number(@diffGold, "#0")'/></td>
				<td>
					<!--xsl:value-of select="count(/*/match[@blue=$teamname])"/>+<xsl:value-of select="count(/*/match[@red=$teamname])"/-->
					<xsl:value-of select="count(/*/match[@blue=$teamname])+count(/*/match[@red=$teamname])"/>
				</td>
				<td>
					<xsl:value-of select="count(/*/match[@blue=$teamname]/simulation)+count(/*/match[@red=$teamname]/simulation)"/>
				</td>
				<td style="background-color:#e6e6e6;"><xsl:value-of select="@ownPoints"/><!--: <xsl:value-of select="@enemyPoints"/>--></td>
			</tr>
		</xsl:for-each>	
	</table>
	</xsl:for-each>	
	<table class="center" border="1" width="100%">
	
	<xsl:for-each select="match">
	  <xsl:variable name="scene" select="@simulation"/>
	  <xsl:variable name="blue" select="@blue"/>
	  <xsl:variable name="red" select="@red"/>
	<br/>	
		<tr style="background-color:#e6e6e6;">
		<th></th>
		<th class="blue"><xsl:value-of select="@blue"/></th>
		<th class="red"><xsl:value-of select="@red"/></th>
		
	</tr>

	<xsl:for-each select="simulation">
		<tr>
			<td>
               	<xsl:element name="a">
                	<xsl:attribute name="href">
				<xsl:for-each select="result">
					<xsl:variable name="output"><xsl:value-of select="@output"/></xsl:variable>  
					<xsl:value-of select="substring-after($output, '/home/massim/www/webapps/massim/')"/>
				</xsl:for-each>
                	</xsl:attribute>
		  			<xsl:attribute name="target">_blank</xsl:attribute>
					<xsl:text><xsl:value-of select="@name"/></xsl:text>
				</xsl:element>
             		</td>
			
			<xsl:for-each select="result">
				<td class="blue"><xsl:value-of select='format-number(@*[name()=$blue], "#0")'/></td>
				<td class="red"><xsl:value-of select='format-number(@*[name()=$red], "#0")'/></td>
			</xsl:for-each>
<!--SVG missing-->
		</tr>
	</xsl:for-each>
	<tr colspan="3" style="border:0;height:20px;"></tr>
	</xsl:for-each>
	</table>
</div>
<xsl:import href="footer.xslt" />
<xsl:apply-imports/>
</xsl:template >
</xsl:stylesheet>
