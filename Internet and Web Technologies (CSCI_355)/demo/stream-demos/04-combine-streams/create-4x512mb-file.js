const fs = require("fs");
const files_dir = "./input/";
const files = 4;
let files_written = 0;
let largeBuffer = Buffer.from('01', 'utf-8');
for(let i=0; i < 28; i++) {
	largeBuffer = Buffer.concat([largeBuffer, largeBuffer]);
}
for(let i=0; i < files; i++){
	fs.writeFile(`${files_dir}512MB-${i}.dummy`, largeBuffer, ()=>{
		files_written++;
		if(files_written === files){
			console.log("All files Written");
		}
	});
}
console.log("Please Wait File Writing In Progress");