const net = require("net");

const host = "localhost";		//or "127.0.0.1"
const port = 3000;

const socket = net.createConnection({host, port});

socket.on("connect", connect_handler);
function connect_handler(){
    console.log("Connection with Server Established");
}
