/* Example 03B: index.js */
const Stock = require("./modules/Stock");
const DayEmitter = require("./modules/DayEmitter");
const portfolio_data = require("./data/stocks.json");						// Portfolio Data
const c = {Red: "\x1b[31m", Green: "\x1b[32m", Reset: "\x1b[0m"};	// Color Data

const day_emitter = new DayEmitter(2400);
let portfolio = portfolio_data.map(stock => new Stock({...stock, day_emitter}));

let current_line = portfolio.length + 1;

let tickerlen = portfolio_data.reduce((acc,cv) => (cv.ticker.length > acc) ? cv.ticker.length : acc, 0);
let namelen = portfolio_data.reduce((acc,cv) => (cv.name.length > acc) ? cv.name.length : acc, 0);
let pricelen = () => portfolio_data.reduce((acc,cv) => ((cv.price.toFixed(2)).length > acc) ? (cv.price.toFixed(2)).length : acc, 0);

portfolio.forEach(function(fund, index){
    fund.on("newday", function({ticker, name, price, previous, change}){
        let change_str = (change > 0) ? `+${(change).toFixed(2)}` : (change).toFixed(2);
        process.stdout.cursorTo(0, index+1);
        process.stdout.clearLine();
        let output_arrow = ((price * 100) - (previous * 100) === 0) ? " " : (price - previous > 0) ? `${c.Green}▲${c.Reset}` : `${c.Red}▼${c.Reset}`;
        let output_ticker = ticker.padEnd(tickerlen," ");
        let output_name  =  name.padEnd(namelen, " ");
        let output_price =  price.toFixed(2).padStart(pricelen() + 1," ");
        let output_change = (change === 0) ? "(0.00)" : (change > 0) ? `${c.Green}(${change_str})${c.Reset}` : `${c.Red}(${change_str})${c.Reset}`;
        console.log(`${output_arrow} ${output_ticker} ${output_name} ${output_price} ${output_change}`);
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
