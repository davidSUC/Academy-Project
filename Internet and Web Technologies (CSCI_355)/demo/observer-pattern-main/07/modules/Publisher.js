/* Example 07B: Publisher.js */
const EventEmitter = require('events');
class Publisher extends EventEmitter{
    publish(data){
        this.emit("publish", data);
    }
}
module.exports = Publisher;