<html>
<head><title>GoT Datenbank</title>
<style type="text/css">
* {
   margin:0;
   padding:0;
}

body{
   text-align:center;
   background: #efe4bf none repeat scroll 0 0;
}

#wrapper{
   width:960px;
   margin:0 auto;
   text-align:left;
   background-color: #fff;
   border-radius: 0 0 10px 10px;
   padding: 20px;
   box-shadow: 1px -2px 14px rgba(0, 0, 0, 0.4);
}

#site{
    background-color: #fff;
    padding: 20px 0px 0px 0px;
}
.centerBlock{
	margin:0 auto;
}
</style>

<body>
	<div id="wrapper">
	    <div id="logo">
			<img width="100%" src="images/header.jpg" class="centerBlock" />
		</div>
		<div id="site">
		
		</div>
		<h1>Liste aller Suchergebnisse:</h1><br />
    <#list figuren as fig>
    <a href="detailperson?cid=${fig.cid}">${fig.name}</a><br />
    </#list>
    <#list haeuser as hae>
    <a href="detailhaus?hid=${hae.hid}">${hae.name}</a><br />
    </#list>
    <#list staffeln as sta>
    <a href="detailstaffel?sid=${sta.sid}">Staffel ${sta.number}</a><br />
    </#list>
	</div>
	<div><a href="start">Startseite</a></div>
</body>
</html>
