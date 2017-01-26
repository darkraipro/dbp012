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
		<h1>Liste aller Figuren:</h1><br />
		<table class="datatable">
		<tr>
        <th>Name</th>  <th>Geburtsort</th>	<th>CharacterID</th>
    	</tr>
    <#list figuren as char>
    <tr>
        <td><a href="${char.art}?name=${char.name}">${char.name}</td>	<td><center>${char.birthplace}</center></td>		<td><center>${char.cid}</center></td>
    </tr>
    </#list>
    </table>
	</div>
	<div><a href="start">Startseite</a></div>
</body>
</html>
