public class Customer
{
    double arriveTime;
    int id;

    // Constructor
    public Customer(int id, Double  arriveTime){
        this.id = id;
        this.arriveTime = arriveTime;
    }

    public String printInfo(){
        return "Customer#" + id + "--arrive time: " + arriveTime + "\n";
    }

}
