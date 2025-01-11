/*
=======================
05-batch-b.js
=======================
Student ID:23960073
Comment (Required):

=======================
*/
const fs = require("fs");
const input_dir = "./input/";
const output_dir = "./output/";
const input_files = fs.readdirSync(input_dir);

const n = input_files.length;	//input size 0 < n < 100
const b = 5;    				//input size 0 < b < n

let files_read = 0;
let count = 1;
let output = ""

for(let i=0; i<n; i++){
    let input_path = `${input_dir}${input_files[i]}`;
    fs.readFile(input_path, "utf8", after_read);
}

function after_read(err, data){
    files_read++;
    output += `${data}\r\n`;
    if(files_read % b === 0 || files_read === n){
        let output_filename = `${output_dir}${count.toString().padStart(2,"0")}_output.txt`
        fs.writeFile(output_filename, output, () => console.log(`Finshed Writing: ${output_filename}`));
        output = "";
        count++;
    }
}
