const net = require("net");

const port = 3000;

const server = net.createServer();

server.on("connection", connection_handler);
function connection_handler(socket){
	console.log("New Client Connected");
}

server.listen(port);
console.log(`Server Listening on port ${port}`);
