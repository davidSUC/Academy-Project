const fs = require("fs");
const readline = require('readline');

const input_dir = "./input/";
const input_files = fs.readdirSync(input_dir);
const length = 536870912;
const output_dir = "./output/";
const output_file = "combined.file";

let destination = fs.createWriteStream(`${output_dir}${output_file}`);
const transfer_file = function(index){
	let source = fs.createReadStream(`${input_dir}${input_files[index]}`);
	let transferred = 0;
	source.on("data", function(chunk){
		destination.write(chunk);
		transferred += chunk.length;
		readline.cursorTo(process.stdout, 0, 0);
		readline.clearLine(process.stdout, 0);
		console.log(`Sending ${input_files[index]}: ${Math.floor(transferred / length * 100)} %`);
	});
	source.on("end", function(){
		console.log(`Finished Sending ${input_files[index]} Data`);
		index++;
		if(index < input_files.length){
			transfer_file(index);
		}
		else{
			destination.end(function(){
				console.log(`Finished Writing ${output_file}`);
			});
		}
	});
};
transfer_file(0);