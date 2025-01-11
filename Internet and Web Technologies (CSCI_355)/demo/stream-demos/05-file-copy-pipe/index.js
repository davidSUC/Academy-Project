const fs = require("fs");

let source = fs.createReadStream("input/long_text.json");
let destination = fs.createWriteStream("output/copy.json");

source.on("end", function(){
	console.log("Finished Sending Data");
});
destination.on("finish", function(){
	console.log("Finished Received Data");
});

source.pipe(destination);
