const fs = require("fs");
const net = require("net");
const CloseEmitter = require("./modules/CloseEmitter.js");

const host = "localhost"
const port = 3000;

const socket = net.createConnection({ host, port });

socket.on("data", data_handler);
function data_handler(chunk){
	console.log(`Received Chunk: ${chunk.length} Bytes`);
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
