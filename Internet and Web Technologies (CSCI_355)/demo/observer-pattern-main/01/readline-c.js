/* Example 01C: readline-c.js */

const readline = require('readline');
const rl = readline.createInterface({input: process.stdin});
rl.setMaxListeners(32);	// Default is 10

function print(input){
	console.log(`Received: ${input}`);
	rl.on('line', print);
}

rl.on('line', print);
console.log("If you need more listeners use the method setMaxListeners");
