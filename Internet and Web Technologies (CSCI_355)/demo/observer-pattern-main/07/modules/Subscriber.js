/* Example 07C: Subscriber.js */
class Subscriber{
    constructor(name, callback = (data) => console.log(data)){
        this.name = name;
        this.receive = this.receive.bind(this);
        this.onReceive = callback;
    }
    subscribe(...args){
        let message_servers = [args].flat();
        message_servers.forEach(server => server.on("publish", this.receive));
    }
    unsubscribe(...args){
        let message_servers = [args].flat();
        message_servers.forEach(server => server.off("publish" , this.receive));
    }
    receive(message){
        this.onReceive(message);
    }
}
module.exports = Subscriber;