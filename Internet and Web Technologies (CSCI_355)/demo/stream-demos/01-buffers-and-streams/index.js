const fs = require("fs");
const source = fs.createReadStream("./input/long_text.json");
// length 143,327 Bytes
const destination = fs.createWriteStream("./output/copy.json");

source.on("data", function(chunk){
	console.log(`New Chunk Received: ${chunk.length}`);
	console.log(chunk.toString().substring(0,20));	//chunk is a Buffer object
	destination.write(chunk);
});

source.on("end", function(){
	console.log("Finished Reading Data");
	destination.end(function(){
		console.log("Finished Writing Data");
	});
});
