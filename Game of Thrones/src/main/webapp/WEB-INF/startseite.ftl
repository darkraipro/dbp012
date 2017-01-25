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
		<p>
			Die Datenbank "${db2name}" ist ${db2exists}
		</p>
		</div>
		<!-- <a href="/detailperson">lol</a> -->
		<h1>Figuren:</h1><br />
		<button type="button" name="btn_figuren" onclick="location.href='/listefiguren'">Alle Figuren</button><br />
		<input type="text" name="txt_suchfigur" />
		<button type="button" name="btn_suchfigur">Suchen</button>
		<br /><br />
		
		<h1>Häuser:</h1><br />
		<button type="button" name="btn_haeuser" onclick="location.href='/listehaeuser'">Alle Häuser</button><br />
		<input type="text" name="txt_suchhaus" />
		<button type="button" name="btn_suchhaus">Suchen</button>
		<br /><br />
		
		<h1>Staffeln:</h1><br />
		<button type="button" name="btn_staffeln" onclick="location.href='/listestaffeln'">Alle Staffeln</button><br />
		<input type="text" name="txt_suchstaffel" />
		<button type="button" name="btn_suchstaffel">Suchen</button>
		<br /><br />
		
		<h1>Playlists:</h1><br />
		
		<br /><br />
		
		<h3>Playlist erstellen:</h3><br />
		<p>Name:</p><input type="text" name="txt_playlist"></input>
		<button type="button" name="btn_playlist">Playlist erstellen </button>
	</div>
	
</body>
</html>
