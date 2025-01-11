/* Example 07A: MessagingServer.js */
const EventEmitter = require('events');
class MessageServer extends EventEmitter{
    constructor(){
        super();
        this.forward = this.forward.bind(this);
    }
    register(...args){
        let publishers = [args].flat();
        publishers.forEach(publisher => publisher.on("publish", this.forward));
    }
    deregister(...args){
        let publishers = [args].flat();
        publishers.forEach(publisher => publisher.off("publish" , this.forward));
    }
    forward(message){
        this.emit("publish", message);
    }
}
module.exports = MessageServer;