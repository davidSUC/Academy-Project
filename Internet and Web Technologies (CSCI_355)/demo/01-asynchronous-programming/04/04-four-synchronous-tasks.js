/*
=======================
04-four-synchronous-tasks.js
=======================
Student ID:23960073
Comment (Required):

=======================
*/
const fs = require("fs");
const dns = require("dns");
const zlib = require("zlib");
const input_file = "./input/domain.deflated";
const output_file = "./output/ip_address.txt";

fs.readFile(input_file, null, do_after_read);
    function do_after_read(err,data){
            zlib.inflate(data, after_compress);
    }

    function after_compress(err,buffer){
        let domain = buffer.toString("utf8");
            dns.resolve(domain, (err, records) => after_resolution(err, records, domain));
    }

    function after_resolution(err, records, domain){
        let result = `${records}\t${domain}`;
            fs.writeFile(output_file, result, ()=> console.log("Writing Complete"));
    }