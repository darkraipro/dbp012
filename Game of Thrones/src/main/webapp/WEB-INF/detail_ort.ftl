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
		<h1>Ort:</h1><br />
		<p>Name: ${ortname}</p><br />
		Haus(Besitzer):<a href="detailhaus?name=${orthaus}">${orthaus}</a><br /><br />
		<p>Burg: ${ortburg}</p><br />
		<p>Herkunftsort von:</p>
			<#list ortherkunftsort as oh><a href="detailperson?name=${oh}">${oh}</a><br /></#list><br />
		<p>Handlungsort in:</p>
			<#list orthandlungsort as oh2><a href="detailepisode?name=${oh2}">${oh2}</a><br /></#list><br />
		
		<br /><br /><br />
		<hr>
		<p>Bewertung abgeben:</p> 
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
		<button type="button" name="btn_bewerten">Bewerten</button>
		<br /><br />
		<hr>
		<h3>Alle Bewertungen:</h3><br />
		<p>Durchschnittsbewertung: </p>
		<br />
	</div>
	<div>
	<a href="start">Startseite</a>
	</div>
</body>
</html>
