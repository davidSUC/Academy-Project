/* Example 02B: index.js */

const DayEmitter = require("./modules/DayEmitter");
const day_emitter = new DayEmitter();
day_emitter.on("newday", function({mm_dd}){
    process.stdout.cursorTo(0, 0);
    process.stdout.clearLine();
    process.stdout.write(mm_dd);
    process.stdout.cursorTo(0, 1);
});
console.clear();
day_emitter.start();