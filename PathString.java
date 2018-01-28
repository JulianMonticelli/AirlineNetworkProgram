public class PathString extends ComparableB {
    String pathString;
    double cost;
    
    public PathString(String s, double c) {
        pathString = s;
        cost = c;
    }
    
    public String getPathString() {
        return pathString;
    }
    
    public double getCost() {
        return cost;
    }
    @Override
    public int compareTo(Object obj, boolean b) { // throwAwayValue to make this all work out with the same file
        PathString ps = (PathString) obj;
        if(cost < ps.getCost()) return -1;
        else if (cost > ps.getCost()) return 1;
        else return 0;
    }
}