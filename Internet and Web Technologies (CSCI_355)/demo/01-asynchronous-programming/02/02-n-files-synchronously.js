/*
=======================
02-n-files-synchronously.js
=======================
Student ID:23960073
Comment (Required):

=======================
*/

const fs = require("fs");
var n = Math.floor(Math.random() * 99) + 1; //input size 0 < n < 100
var count = 1;
var file = `${count.toString().padStart(2,"0")}_output.txt`;
var path = `./output/${file}`;

writeFileSyn();
function writeFileSyn(){
    fs.writeFile(path, "Data-2", do_after_write);
}

function do_after_write(err){
    console.log(`${file} Finshed Writing.`);
    if(count >= n){
        console.log(`Finshed Writing.`)
    }
    else{
        count++;
        file = `${count.toString().padStart(2,"0")}_output.txt`;
        path = `./output/${file}`;
        writeFileSyn();
    }
}
