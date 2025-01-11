const fs = require("fs");
const net = require("net");
const CloseEmitter = require("./modules/CloseEmitter.js");

const host = "localhost";
const port = 3000;

const input_path = "./input/small.dummy";
const length = fs.statSync(input_path).size;
const file_stream = fs.createReadStream(input_path);

const socket = net.createConnection({ host, port });
socket.on("connect", connect_handler);
function connect_handler (){
	console.log("Connected, File Upload Initiated");
	socket.write(length.toString());
}

socket.on("data", data_handler);
function data_handler(chunk){
	// Assume 1 Chunk for Messages
	if(chunk.toString() === "Ready"){
		file_stream.pipe(socket, {end:false});
		file_stream.on('end', () => {
			console.log('All Data Sent');
		});
	}
	else{
		console.log(chunk.toString());
	}
}

socket.on("error", error_handler);
function error_handler(err){
	if(err.code === "ECONNREFUSED"){
		console.log("Server Offline");
		process.exit();
	}
	else{
		console.error(err);
		fs.appendFile("log/error.txt", `${new Date()}: ${err}\r\n`, ()=>{
			process.exit();
		});
	}
}

socket.on("end", end_handler);
function end_handler(){
    console.log("Received End");
	process.exit();
}

const close_emitter = new CloseEmitter();
close_emitter.on("close", terminate_connection);
function terminate_connection(){
	socket.end(function(){
		process.exit();
	});
}
