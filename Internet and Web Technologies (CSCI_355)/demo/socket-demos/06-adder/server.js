const fs = require("fs");
const net = require("net");
const CloseEmitter = require("./modules/CloseEmitter.js");

const port = 3000;
let connections = [];

const server = net.createServer();
server.on("connection", connection_handler);
function connection_handler(socket){
	console.log("New Client Connected");
	connections.push(socket);
	let sum = 0;
	
	socket.on("data", data_handler);
	function data_handler(chunk){
		// Assumption: only one chunk and it is <= 65,536 Bytes
		let integer = Number.parseInt(chunk.toString(), 10);
		console.log(`Received ${integer}`);
		sum += integer;
		socket.write(sum.toString());
	}
	
	socket.on("error", error_handler);
    function error_handler(err){
		fs.appendFile("log/error.txt", `${new Date()}: ${err}\r\n`, ()=>{});
		console.log("Connection Lost");
		connections.splice(connections.indexOf(socket), 1);
    	socket.destroy();
	}
	
	socket.on("end", end_handler);
    function end_handler(){
		console.log("Client Disconnected");
		connections.splice(connections.indexOf(socket), 1);
	}
}

server.listen(port);
console.log(`Server Listening on port ${port}`);

const close_emitter = new CloseEmitter();
close_emitter.on("close", terminate_all_connections);
function terminate_all_connections() {
	let sockets_closed = 0;
	if(connections.length <= 0) {
		process.exit();
	}
	connections.forEach(function(socket){
		close_socket(socket, terminate_after_all_close);
	});
	function close_socket(socket, callback){
		socket.end(callback);
	}
	function terminate_after_all_close(){
		sockets_closed++;
		if(sockets_closed >= connections.length){
			process.exit();
		}
	}
}
