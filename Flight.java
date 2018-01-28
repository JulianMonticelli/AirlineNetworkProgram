public class Flight extends ComparableB{
    private int to;
    private int from;
    private double distance;
    private double price;
    public Flight() { } // do nothing for instantiation

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public String toString() {
        return "" + Airline.airport[this.from-1] + " -> " + Airline.airport[this.to-1] + " (" + this.distance + "mi, $" + this.price + ")";
    }
    @Override
    public int compareTo(Object obj, boolean priceCompare) {
        Flight fli = (Flight) obj;
        if(priceCompare) {
            if(price < fli.getPrice()) return -1;
            else if (price > fli.getPrice()) return 1;
            else return 0;
        } else {
            if(distance < fli.getDistance()) return -1;
            else if (distance > fli.getDistance()) return 1;
            else return 0;
        }
    }
    public double getWeight(boolean priceCompare) {
        if(priceCompare) {
            return price;
        }
        else {
            return distance;
        }
    }
}