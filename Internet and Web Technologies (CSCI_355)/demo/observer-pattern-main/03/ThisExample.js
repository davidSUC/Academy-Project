/* Example 03: ThisExample.js */
class ThisExample {
    constructor(){
        this.data = "data";
    }
    wait(){
        setTimeout(function(){ console.log(this); }, 0);	// Timeout Object
        setTimeout(() => { console.log(this); }, 1000);		// ThisExample Object
    }
}
let example = new ThisExample();
example.wait();