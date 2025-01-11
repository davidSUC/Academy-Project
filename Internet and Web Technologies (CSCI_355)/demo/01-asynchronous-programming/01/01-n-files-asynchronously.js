/*
=======================
01-n-files-asynchronously.js
=======================
Student ID: 23960073
Comment (Required):

=======================
*/
const fs = require("fs");

var n = Math.floor(Math.random() * 99) + 1; //random size 0 < n < 100.
var count = 1;

for(let i=1; i<n; i++){
    let file = `${i.toString().padStart(2,"0")}_output.txt`;
    let path = `./output/${file}`;
    fs.writeFile(path, "Data-1", (err) => do_after_write(err, file));
}
    
    function do_after_write(err, file){
        if(err){
            throw err;
        }
        console.log(`${file} Writing Finshed`);
        count++;
        if(count >= n){
            console.log(`Writing Complete`);
        }
    }