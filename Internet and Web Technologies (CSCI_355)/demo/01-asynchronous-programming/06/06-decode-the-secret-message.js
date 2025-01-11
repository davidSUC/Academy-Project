/*
=======================
06-decode-the-secret-message.js
=======================
Student ID:23960073
Comment (Required):

=======================
*/
const fs = require("fs");
const { buffer } = require("stream/consumers");
const zlib = require("zlib");
const files_dir = "./input/";
const input_files = fs.readdirSync(files_dir);

const n = input_files.length;	//input size 0 < n < 100
const lines = [];
let count = 0;

fs.readFile(`${files_dir}${input_files[count]}`, {encoding:null}, after_read);
function after_read(err, data){
    zlib.inflate(data, after_decompress);
}
 
function after_decompress(err, buf){
    count++;
    lines.push(buf);
    if(count == n){
        fs.writeFile(`./output/secret-message.zip`, Buffer.concat(lines), () => console.log(`Finished.`));
    }
    else{
        fs.readFile(`${files_dir}${input_files[count]}`, {encoding:null}, after_read);
    }
}