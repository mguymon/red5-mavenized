<script type="text/javascript" src="/sample/player/swfobject.js"></script>
 
<div id="player">This text will be replaced</div>
 
<script type="text/javascript">
var so = new SWFObject('/sample/player/player.swf','mpl','470','290','9');
so.addParam('allowscriptaccess','always');
so.addParam('allowfullscreen','true');
so.addParam('flashvars','&file=IronMan.flv&streamer=rtmp://localhost/sample');
so.write('player');
</script>