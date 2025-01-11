const fs = require("fs");
const net = require("net");
const CloseEmitter = require("./modules/CloseEmitter.js");
const port = 3000;
let connections = [];

const output_path = "./output/small.dummy";
const server = net.createServer();

server.on("connection", connection_handler);
function connection_handler(socket){
	console.log("New Client Connected");
	connections.push(socket);
	
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
	
	socket.once("data", receive_length);
	function receive_length(chunk){
		// Assume 1 Chunk for length first
		let total_length = Number.parseInt(chunk.toString(), 10);
		console.log(`Total ${total_length}`)
		socket.on("data", function(chunk){
			receive_data({chunk, total_length});
		});
		socket.write("Ready");
	}
	
	const output_file = fs.createWriteStream(output_path);
	let transferred = 0;
	function receive_data({chunk, total_length}){
		transferred += chunk.length;
		console.log(`New Chunk: ${chunk.length.toString().padEnd(5,' ')} | ${Math.floor(transferred / total_length * 100)} %`);
		let written = transferred;
		output_file.write(chunk, function(){
			finished_writing({written, total_length})
		});
	}
	
	function finished_writing({written, total_length}){
		if(written >= total_length){
			output_file.end(function(){
				console.log("File Recieved");
				socket.end("File Recieved");
			});
		}
	}
}

server.listen(port);
console.log(`Now listening on port ${port}`);

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