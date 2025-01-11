// Demo works only in Terminal

const fs = require("fs");
const destination = fs.createWriteStream("output/stdin.txt");

process.stdin.on('data', (chunk) => {
	destination.write(chunk);
});
