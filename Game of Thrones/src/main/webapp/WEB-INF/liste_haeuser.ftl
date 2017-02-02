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
		<h1>Liste aller Häuser:</h1><br />
		<table class="datatable">
    <tr>
        <th>Name</th>  <th>Motto</th>	<th>Sitz</th>
    </tr>
    <#list haeuser as haus>
    <tr>
        <td><a href="detailhaus?hid=${haus.hid}">${haus.name}</td> <td>  <center> ${haus.words}</center></td>	<td><center><a href="detailort?lid=${haus.seat}">${haus.ort}</center></td>
    </tr>
    </#list>
  </table>
	</div>
	<div>
	<a href="start">Startseite</a>
	</div>
</body>
</html>
