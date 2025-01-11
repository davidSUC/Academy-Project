const fs = require("fs");
const net = require("net");
const readline = require('readline');

const CloseEmitter = require("./modules/CloseEmitter.js");
const port = 3000;
let connections = [];

const output_path = "./output/";
const server = net.createServer();

server.on("connection", connection_handler);
function connection_handler(socket){
	console.clear();
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
	
	socket.once("data", receive_file_info);
	function receive_file_info(chunk){
		// Assume 1 Chunk for Header Info (Length and Filename)
		let [length_str, filename] = chunk.toString().split("|");
		
		let total_length = Number.parseInt(length_str, 10);
		const output_stream = fs.createWriteStream(`${output_path}${filename}`);
		socket.on("data", function(chunk){
			receive_data({chunk, total_length, output_stream});
		});
		socket.write("Ready");
	}
	
	let transferred = 0;
	let progress = 0;
	let previous = 0;
	function receive_data({chunk, total_length, output_stream}){
		transferred += chunk.length;
		readline.cursorTo(process.stdout, 0, 0);
		previous = progress;
		progress = Math.floor(transferred / total_length * 100);
		console.log(`New Chunk: ${chunk.length.toString().padEnd(5,' ')} | ${progress} %`);
		let written = transferred;
		if(progress > previous){
			socket.write(`Upload Progress: ${progress}%`);
		}
		output_stream.write(chunk, function(){
			finished_writing({written, total_length, output_stream})
		});
	}
	
	function finished_writing({written, total_length, output_stream}){
		if(written >= total_length){
			output_stream.end(function(){
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