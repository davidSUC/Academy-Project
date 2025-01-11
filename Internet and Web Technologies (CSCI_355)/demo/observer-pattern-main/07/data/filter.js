const catfacts = require("./cat-facts.json");
let cf = catfacts.filter(fact => fact.length < 42);
console.table(cf);