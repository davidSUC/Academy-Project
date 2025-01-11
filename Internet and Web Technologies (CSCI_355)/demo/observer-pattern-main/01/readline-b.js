/* Example 01B: readline-b.js */

const readline = require('readline');
const rl = readline.createInterface({input: process.stdin});
const print = input => console.log(`Received: ${input}`);

rl.on('line', print);
rl.on('line', print);

console.log("Event Listeners will each indepently fire when an event is emitted");
