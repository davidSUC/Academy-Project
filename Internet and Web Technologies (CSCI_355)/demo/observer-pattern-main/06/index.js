/* Example 06B: index.js */
const Stock = require("./modules/Stock");
const DayEmitter = require("./modules/DayEmitter");
const portfolio_data = require("./data/stocks.json");			// Portfolio Data

const day_emitter = new DayEmitter(2400);
let portfolio = portfolio_data.map(stock => new Stock({...stock, day_emitter}));
let current_line = portfolio.length + 1;

portfolio.forEach(function(stock, index){
    stock.on("newday", function({ticker, name, price, previous, change}){
        process.stdout.cursorTo(0, index + 1);
        process.stdout.clearLine();
        console.log(`${ticker} ${name} ${price.toFixed(2)} ${change.toFixed(2)}`);
        process.stdout.cursorTo(0, current_line);
    });
});

day_emitter.on("newday", function({mm_dd}){
    process.stdout.cursorTo(0, 0);
    process.stdout.clearLine();
    process.stdout.write(mm_dd);
    process.stdout.cursorTo(0, current_line);
});
day_emitter.start();