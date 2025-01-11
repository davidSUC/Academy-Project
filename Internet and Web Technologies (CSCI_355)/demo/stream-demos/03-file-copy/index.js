const readline = require('readline');
const fs = require("fs");

const length = 1073741824;
let transferred = 0;

let source = fs.createReadStream("input/1GB.dummy");
let destination = fs.createWriteStream("output/copy.dummy");

source.on("data", function(chunk){
	destination.write(chunk);
	transferred += chunk.length;
	readline.cursorTo(process.stdout, 0, 0);
	readline.clearLine(process.stdout, 0);
	console.log(`${Math.floor(transferred / length * 100)} %`);
});

source.on("end", function(){
	console.log("Finished Sending Data");
	destination.end(function(){
		console.log("Finished Received Data");
	});
});