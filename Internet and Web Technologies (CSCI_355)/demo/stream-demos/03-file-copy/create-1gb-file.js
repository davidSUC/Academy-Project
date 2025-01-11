const fs = require("fs");
let largeBuffer = Buffer.from('01', 'utf-8');
for(let i=0; i <= 28; i++) {
	largeBuffer = Buffer.concat([largeBuffer, largeBuffer]);
}
fs.writeFile("input/1GB.dummy", largeBuffer, ()=>{
	console.log("File Written");
});
console.log("Please Wait File Writing In Progress");