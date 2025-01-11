/*
=======================
03-personal-hosts-file.js
=======================
Student ID:23960073
Comment (Required):

=======================
*/
const fs = require("fs");
const dns = require("dns");
const input_file = "./input/domains.txt";
const output_file = "./output/hosts.txt";

const results = [];
var lines = Array();

fs.readFile(input_file, `utf8`, after_read);
function after_read(err,data){
        let resolved = {count : 0};
        lines = data.toString().split("\r\n");
        for(let i = 0; i < lines.length; i++){
            resolve(lines[i], resolved);
        }
}

function resolve(domain, resolved){
    dns.resolve(domain, after_resolution);
    function after_resolution (err, records){
        resolved.count++;
        results.push(`${records.toString()}\t${domain}`);
        if(resolved.count == lines.length){
            fs.writeFile(output_file, results.join(`\r\n`), do_after_writing);
        }
    }
}

function do_after_writing(err){
        results.forEach(element => console.log(element)); //check
        console.log(`Finshed Writing.`)
}
