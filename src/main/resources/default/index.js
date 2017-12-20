function getMetadata(key)
{
	var metas = document.getElementsByTagName("meta"); 
	
	for (var i=0; i< metas.length; i++)
	{ 
		var meta = metas[i];
		if (meta.getAttribute("property") == key)
		{ 
			return meta.getAttribute("value"); 
		} 
	} 
}

var SocketMessageTypeInc = 0;
var SocketMessageType = {}
SocketMessageType.PING = SocketMessageTypeInc++;
SocketMessageType.AUTH = SocketMessageTypeInc++;
SocketMessageType.EVENT = SocketMessageTypeInc++;

var webSocketPort = null;
var webSocketUrl = null;
var webSocket = null;

var listeners = {};

function init()
{
	if (webSocket !== null)
	{
		error("already connected!");
	}
	// connect
	webSocketPort = getMetadata("ws-port");
	webSocketUrl = "ws://localhost:"+webSocketPort;
	if ('WebSocket' in window)
	{
		webSocket = new WebSocket(webSocketUrl);  
	}
	else if ('MozWebSocket' in window)
	{
		webSocket = new MozWebSocket(webSocketUrl);  
	}
	else
	{
	    alert("Your web browser does not appear to support required technology (WebSockets)");
		return;
	}
	// listener
	webSocket.addEventListener("open", function(event)
	{
		// auth
		sendAuth(getMetadata("ws-key"));
		// ping
		setInterval(sendPing, 50);
	});
	webSocket.addEventListener("message", function(event)
	{
		var json = JSON.parse(event.data)
		console.log("RECIEVE: type ", json.type, " data ", json.data);
		if (json.type == SocketMessageType.EVENT) // event
		{
			var eventData = json.data;
		    var listener = listeners[eventData.name];
		    console.log("	EVENT: type ", eventData.name, " data ", eventData.data);
		    if (listener == null)
		    	{
		    		console.log("unknown event: "+eventData.name);
		    	}
		    else
		    	{
		    		listener(eventData.data);
		    	}
		}
	});
}

// send types

function sendPing()
{
	sendRaw(SocketMessageType.PING, {});
}

function sendAuth(key)
{
	sendRaw(SocketMessageType.AUTH, {key: key});
}

function sendEvent(name, data)
{
	if (typeof data !== "object")
	{
		error("must be an object");
	}
	sendRaw(SocketMessageType.EVENT, {name: name, data: data});
}

// send
function sendRaw(type, data)
{
	if (webSocket.readyState == 2 || webSocket.readyState == 3)
	{
		window.close();
		return;
	}
	if (type != SocketMessageType.PING)
	{
		console.log("SEND: type ", type, " value ", data);
	}
	var object = {}
	object.type = type;
	object.data = data;
	webSocket.send(JSON.stringify(object));
}

// listener

function addListener(name, listener)
{
	listeners[name] = listener
}