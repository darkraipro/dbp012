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
		<h1>Person:</h1><br />
		<p>Name: ${person.name}</p><br />
		<p>Titel : ${person.title}</p><br />
		Herkuntsort: <a href="detailort?lid=${herkunft.lid}">${herkunft.name}</a><br /><br />
		<p>Biographie: ${person.bio}</p><br />
		<p>Haeuser: </p>
			<#list personhaeuser as ph><a href="detailhaus?hid=${ph.hid}">${ph.housename}</a> von <a href="detailepisode?eid=${ph.epfromid}">${ph.epfromtitle}</a> bis <a href="detailepisode?eid=${ph.eptoid}">${ph.eptotitle}</a> <br /></#list><br />
		<p>Beziehungen: </p>
			<#list personbeziehungen as pb><a href="detailperson?cid=${pb.target}">${pb.name}</a> Art: ${pb.art}<br /></#list><br />
		<p>Besitzer von: </p>
			<#list personbesitzer as pb2><a href="detailperson?cid=${pb2.cid}">${pb2.name}</a><br /></#list><br />
		
		<br /><br /><br />
		<hr>
		<p>Bewertung abgeben:</p> 
		<form method="post">
		<select size="1" name="select_bewertung">
			<option>0</option>
			<option>1</option>
			<option>2</option>
			<option>3</option>
			<option>4</option>
			<option>5</option>
		</select>
		<br />
		<textarea rows="10" cols="50" name="textarea_bewertung"></textarea>
		<br />
		<button type="submit" name="btn_bewerten">Bewerten</button>
        </form>
		<br /><br />
		<hr>
		
		<h3>Alle Bewertungen:</h3><br />
		<table class="datatable">
    <tr>
        <th>Name</th>  <th>Bewertung</th>	<th>Kommentar</th>
    </tr>
    <#list listeBewertung as lb>
    <tr>
        <td>${lb.username}</td> <td>  <center> ${lb.rating}</center></td>	<td><center>${lb.text}</center></td>
    </tr>
    </#list>
  </table>
		<p>Durchschnittsbewertung: ${bewertung.avgrating}</p>
		<br />
	</div>
	<div>
	<a href="start">Startseite</a>
	</div>
</body>
</html>
