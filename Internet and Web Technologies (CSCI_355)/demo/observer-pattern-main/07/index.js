/* Example 07D: index.js */

const MessageServer = require("./modules/MessageServer");
const Publisher = require("./modules/Publisher");
const Subscriber = require("./modules/Subscriber");

let cat_news = new MessageServer();
let dog_news = new MessageServer();
let animal_news = new MessageServer();

// Publishers emit Signals to MessageServers during publications
let cat_facts = new Publisher();
let cat_chronicles = new Publisher();
let daily_dog = new Publisher();

cat_news.register(cat_facts, cat_chronicles);
dog_news.register(daily_dog);
animal_news.register(cat_news, dog_news);	//We can register MS to other MS

//Subscribers subscribe to MessangingServers
let cat_fan = new Subscriber("Cat Fan", (data) => print(1, data)); // Print to Col 1
let dog_fan = new Subscriber("Dog Fan", (data) => print(2, data)); // Print to Col 2
let animal_fan = new Subscriber("Animal Fan", (data) => print(3, data)); //Print to Col 3

cat_fan.subscribe(cat_news);
dog_fan.subscribe(dog_news);
animal_fan.subscribe(animal_news);

// Create 3 Columns within the terminal
let column_cursor = [0,0,0];
function print(col, message){
    col = col - 1;	// 0-index is easier to work with, but 1-index is easier to remember
    process.stdout.cursorTo((col) * 32, column_cursor[col]);
    process.stdout.write(message);
    column_cursor[col]++;
    process.stdout.cursorTo(0, Math.max(...column_cursor) + 1);
}
console.clear();
print(1, "Cat Fan");		// Print to Col 1
print(2, "Dog Fan");		// Print to Col 2
print(3, "Animal Fan");		// Print to Col 3

// Get Data
const cat_data = require("./data/cat-facts.json");
const dog_data = require("./data/dog-facts.json");
function random_fact(facts){
    return facts[facts.length * Math.random() | 0]
}

// Keyboard Events
const readline = require("readline");
readline.emitKeypressEvents(process.stdin);
process.stdin.setRawMode(true);

process.stdin.on('keypress', (character, keypress) => {
      if (character === '1') {
        cat_chronicles.publish(`[CC]${random_fact(cat_data).substring(0,28)}`);
    }
    else if (character === '2') {
        cat_facts.publish(`[CF]${random_fact(cat_data).substring(0,28)}`);
    }
    else if (character === '3') {
        daily_dog.publish(`[DD]${random_fact(dog_data).substring(0,28)}`);
    }
    else if (keypress.name === 'c' && keypress.ctrl === true) {
        process.exit(0);
    }
});
