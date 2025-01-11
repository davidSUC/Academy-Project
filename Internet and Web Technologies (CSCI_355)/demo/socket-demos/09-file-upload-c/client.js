const fs = require("fs");
const net = require("net");
const path = require('path');
const readline = require("readline");

const CloseEmitter = require("./modules/CloseEmitter.js");

const io = readline.createInterface({
	input: process.stdin,
	output: process.stdout
});

const host = "192.168.1.207";		// 192.168.1.207
const port = 3000;

io.question("Upload What File or !quit to exit\r\n", io_handler);

function io_handler(input_path){
	if(input_path === "!quit"){
		process.exit();
	}
	try{
		fs.accessSync(input_path);
		const file_stream = fs.createReadStream(input_path);
		const filename = path.basename(input_path);
		const length = fs.statSync(input_path).size;
		connect(file_stream, filename, length);
	}
	catch (err){
		console.log("Invalid File Path");
		io.question("Upload What File? ", io_handler);
	}

}

function connect(file_stream, filename, length){
	const socket = net.createConnection({ host, port });
	
	socket.on("connect", connect_handler);
	function connect_handler (){
		console.clear();
		console.log("Connected, File Upload Initiated");
		socket.write(`${length}|${filename}`);
	}
	socket.on("data", data_handler);
	function data_handler(chunk){
		if(chunk.toString() === "Ready"){
			file_stream.pipe(socket, {end:false});
			file_stream.on('end', () => {
				console.log('All Data Sent');
			});
		}
		else if(chunk.toString().startsWith("Upload Progress")){
			readline.cursorTo(process.stdout, 0, 1);
			console.log(chunk.toString());
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

}


const close_emitter = new CloseEmitter();
close_emitter.on("close", terminate_connection);
function terminate_connection(){
	socket.end(function(){
		process.exit();
	});
}
