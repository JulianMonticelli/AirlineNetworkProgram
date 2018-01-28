
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;



public class Airline {
    private static final boolean DEBUG = false;
    public static int vertex_count;
    public static String airport[];
    public static Object[] flights;
    private static MinPQ<Flight> minPrice;
    private static MinPQ<Flight> minDistance;
    private static MinPQ<PathString> tripsUnderCost;
    private static String lineSep;
    private static Scanner kb;
    public Airline() {
        lineSep = System.getProperty("line.separator"); // LINUX/WINDOWS/OSX compatibility
        minPrice = new MinPQ<>(true);
        minDistance = new MinPQ<>(false);
    }
    public static void main(String[] args) throws FileNotFoundException {
        Airline al = new Airline();
        al.run();
    }
    private void run() throws FileNotFoundException {
        kb = new Scanner(System.in);
        String inputFile;
        String option = "";
        System.out.print("Enter the input file that you want to use as a graph:\n");
        inputFile = kb.nextLine();
        File f = new File(inputFile);
        loadGraph(f);
        System.out.println("At any time in the main menu, to get help, type \"help\" or \"h\"");
        while(!option.equalsIgnoreCase("q") && !option.equalsIgnoreCase("quit")) {
            System.out.println("\nOptions:\n1. Add a route to the schedule\n2. Remove a flight from the schedule\n"
                              +"3. Show entire graph\n4. Display minimum distance spanning tree\n"
                              +"5. A->B | Shortest path by distance\n6. A->B | Shortest path by hops\n"
                              +"7. A->B | Cheapest path\n8. All trips less than a given price\nh - help | q - quit");
            option = kb.nextLine();
            int optionVal = -1;
            try {
                optionVal = Integer.parseInt(option);
            } catch (Exception e) {
                // do nothing because we don't want to do anything.
            }
            if(option.startsWith("h") || option.startsWith("H")) {
                System.out.println("\nProgram help:\n=============\n\nTo perform a menu option, type the number of the "
                                  +"respective command.\n\nTo quit the program at any time at the menu, type 'q' or 'quit'");
            } else if (optionVal == 1) {
                addRoute(); // seems to work fine
            } else if (optionVal == 2) {
                removeRoute(); // seems to work fine
            } else if (optionVal == 3) {
                showGraph(); // works as desired
            } else if (optionVal == 4) {
                getMSTDistance(); // sufficient as of Farnan's clarification as to multiple graphs
            } else if (optionVal == 5) {
                shortestPathByDistance(); // seems to be working fine
            } else if (optionVal == 6) {
                shortestPathByHops(); // seems to be working fine
            } else if (optionVal == 7) {
                shortestPathByPrice(); // seems to be working fine
            } else if (optionVal == 8) {
                tripsLessThan(); // currently working on
            } else if (option.equalsIgnoreCase("q") || option.equalsIgnoreCase("quit")) {
                System.out.println("Exiting..."); // quit works fine :P
            } else {
                System.out.println("That command isn't recognized. Type h or help for help."); // if you hit here, you did something wrong
            }
        }
    }
    public void removeRoute() {
        int routeTo = 0;
        int routeFrom = 0;
        String option;
        while(routeFrom == 0) {
            System.out.println("\nSelect a city your route begins at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeFrom = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        while(routeTo == 0) {
            System.out.println("\nSelect a city your route ends at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeTo = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        int fr = flightRemove(routeFrom, routeTo);
        System.out.println("Successfully removed " + fr + " flights. \n");
    }
    public void addRoute() {
        int routeTo = 0;
        int routeFrom = 0;
        double distance = 0;
        double price = 0;
        String option;
        while(routeTo == 0) {
            System.out.println("\nSelect a city your route begins at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeTo = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        while(routeFrom == 0) {
            System.out.println("\nSelect a city your route ends at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeFrom = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        while(distance <= 0) {
            System.out.println("\nEnter a distance for your route: ");
                option = kb.nextLine();
                double optionVal = -1;
                try {
                    optionVal = Double.parseDouble(option);
                    if (optionVal > 0) {
                        distance = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        while(price <= 0) {
            System.out.println("\nSelect a price for your route: ");
                option = kb.nextLine();
                double optionVal = -1;
                try {
                    optionVal = Double.parseDouble(option);
                    if (optionVal >= 0) {
                        price = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                }
        }
        flightAdd(routeTo, routeFrom, distance, price);
        System.out.println("Success!\n");
    }
    private static void loadGraph(File f) throws FileNotFoundException {
        Scanner fileIn = new Scanner(f);
        int count = Integer.parseInt(fileIn.nextLine());
        airport = new String[count];
        flights = new Object[count];
        vertex_count = count;
        for(int i = 0; i < count; i++) {
            airport[i] = fileIn.nextLine();
            flights[i] = (Object)new LinkedList<Flight>();
        }
        while(fileIn.hasNext()) {
            String nl = fileIn.nextLine();
            if(DEBUG) System.out.println(nl);
            if(nl.equals("")) continue; // PLS don't give me a newline in my file that shouldn't be there
            if(nl.equals(lineSep)) continue;// ^ what he said
            String[] flightArgs = nl.split(" "); // String array from space delimited nextLine()
            flightAdd(Integer.parseInt(flightArgs[0]),Integer.parseInt(flightArgs[1]),Double.parseDouble(flightArgs[2]),Double.parseDouble(flightArgs[3]));
        }
    }
    public int flightRemove(int from, int to) {
        int flightsRemoved = 0;
        
        LinkedList flight0 = (LinkedList<Flight>)flights[from-1];
        ListIterator<Flight> f0i = flight0.listIterator();
        Queue<Flight> toBeRemoved_0 = new Queue<Flight>(); // queue for [from]list
        Queue<Flight> toBeRemoved_1 = new Queue<Flight>(); // queue for [to] list
        while(f0i.hasNext()) {
            Flight curr = (Flight)f0i.next();
            if(curr.getTo() == to) {
                toBeRemoved_0.enqueue(curr); // enqueue the flight that is to be removed
            } else if(curr.getFrom() == to && curr.getTo() == from) {
                toBeRemoved_0.enqueue(curr); // enqueue the flight that is to be removed
            }
        }
        while(!toBeRemoved_0.isEmpty()) { // while there are flights to be removed in this queue
            flight0.remove(toBeRemoved_0.dequeue()); // remove flight
            flightsRemoved++; // then increment flights removed
        }
        
        // must double our search for bi-directional graphs, yooo
        LinkedList flight1 = (LinkedList<Flight>)flights[to-1];
        ListIterator<Flight> f1i = flight1.listIterator();
        while(f1i.hasNext()) {
            Flight curr = (Flight)f1i.next();
            if(curr.getFrom() == from) {
                toBeRemoved_1.enqueue(curr); // enqueue the flight that is to be removed
            } else if(curr.getTo() == from && curr.getFrom() == to) {
                toBeRemoved_1.enqueue(curr); // enqueue the flight that is to be removed
            }
        }
        while(!toBeRemoved_1.isEmpty()) { // while there are flights to be removed in this queue
            flight1.remove(toBeRemoved_1.dequeue()); // remove flight
            flightsRemoved++; // then increment flights removed
        }
        
        if(flightsRemoved % 2 != 0) { // bidirectional graph should ALWAYS produce 2*n flights removed
            System.err.println("There is some sort of error - an uneven amount of flights were removed. Flights removed: " + flightsRemoved);
        }
        flightsRemoved /= 2; // since bidirectional, realistically 1 route/flight is removed, not 2
        return flightsRemoved;
    }
    public static void flightAdd(int loc1, int loc2, double distance, double price) {
        Flight flight = new Flight();
        Flight flightReverse = new Flight();

        // set appropriate flight details for flight to
        flight.setFrom(loc1);
        flight.setTo(loc2);
        flight.setDistance(distance);
        flight.setPrice(price);

        // set appropriate flight details for flight from
        flightReverse.setTo(loc1);
        flightReverse.setFrom(loc2);
        flightReverse.setDistance(distance);
        flightReverse.setPrice(price);

        LinkedList flight0 = (LinkedList<Flight>)flights[loc1-1];
        flight0.add(flight);
        //flights[loc1-1] = flight0;
        LinkedList flight1 = (LinkedList<Flight>)flights[loc2-1];
        flight1.add(flightReverse);
        //flights[loc2-1] = flight1;
    }
    public static void showGraph() {
        System.out.println("Whole Graph:\nThis includes flights in reverse order\n==================================\n\n");
        for (int i = 0; i < vertex_count; i++) {
            LinkedList l = (LinkedList<Flight>) flights[i];
            ListIterator li = l.listIterator();
            while(li.hasNext()) {
                Flight cf = (Flight)li.next();
                System.out.println(cf.toString());
            }
            System.out.println();
        }
        System.out.println("\n");
    }
    public static void listCitiesByUserIndex() { // cities are NEVER REMOVED as specified in README.txt
        for (int i = 0; i < airport.length; i++) {
            System.out.println(i+1 + ". " + airport[i]);
        }
    }
    public static void initKruskalPQ() {
        for (Object o : flights) {
            LinkedList l = (LinkedList<Flight>) o;
            ListIterator li = l.listIterator();
            while(li.hasNext()) {
                Flight cf = (Flight)li.next();
                minPrice.insert(cf);
                minDistance.insert(cf);
            }
        }
    }
    private static void getMSTDistance() {
        initKruskalPQ();
        Kruskal mst = new Kruskal(minDistance, false); // get min dist spanning tree
        Queue<Flight> q = (Queue<Flight>)mst.edges();
        Iterator qi = q.iterator();
        System.out.println("MINIMUM DISTANCE SPANNING TREE:\n" + "===============================");
        while(qi.hasNext()) {
            System.out.println(qi.next());
        }
        System.out.println();
    }
    private static void getMSTPrice() { // unused - but I kept it in the file 
        initKruskalPQ();
        Kruskal mst = new Kruskal(minPrice, true); // get min dist spanning tree
        Queue<Flight> q = (Queue<Flight>)mst.edges();
        Iterator qi = q.iterator();
        System.out.println("MINIMUM PRICE SPANNING TREE:\n" + "===============================");
        while(qi.hasNext()) {
            System.out.println(qi.next());
        }
        System.out.println();
    }
    private static void shortestPathByDistance() {
        int routeTo = 0;
        int routeFrom = 0;
        String option;
        while(routeFrom == 0) {
            System.out.println("\nSelect a city your route begins at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeFrom = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        while(routeTo == 0) {
            System.out.println("\nSelect a city your route ends at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeTo = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        
        Dijkstra sp = new Dijkstra(routeTo, false, false); // false b/c comparing distance, not price
        Queue<Flight> path = (Queue<Flight>)sp.pathTo(routeFrom);
        
        System.out.println("SHORTEST PATH FROM " + Airline.airport[routeFrom-1].toUpperCase() + " TO " + Airline.airport[routeTo-1].toUpperCase() + ":\n============================================================");
        if(path == null) {
            System.out.println("There is no path between the chosen cities.");
            return;
        }
        Flight f = new Flight();
        double runningTotal = 0;
        while(!path.isEmpty()) {
            f = path.dequeue();
            System.out.print(airport[f.getTo()-1] + " ->" + f.getDistance() + "mi-> ");
            runningTotal += f.getDistance();
        }
        if(f != null) System.out.println(airport[f.getFrom()-1]+"\nShortest path distance: " + runningTotal + "mi\n");
    }
    
    private static void shortestPathByPrice() {
        int routeTo = 0;
        int routeFrom = 0;
        String option;
        while(routeFrom == 0) {
            System.out.println("\nSelect a city your route begins at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeFrom = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        while(routeTo == 0) {
            System.out.println("\nSelect a city your route ends at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeTo = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        
        Dijkstra sp = new Dijkstra(routeTo, true, false); // true b/c comparing distance, not price
        Queue<Flight> path = (Queue<Flight>)sp.pathTo(routeFrom);
        
        System.out.println("CHEAPEST PATH FROM " + Airline.airport[routeFrom-1].toUpperCase() + " TO " + Airline.airport[routeTo-1].toUpperCase() + ":\n============================================================");
        if(path == null) {
            System.out.println("There is no path between the chosen cities.");
            return;
        }
        Flight f = new Flight();
        double runningTotal = 0;
        while(!path.isEmpty()) {
            f = path.dequeue();
            System.out.print(airport[f.getTo()-1] + " ->$" + f.getPrice() + "-> ");
            runningTotal+=f.getPrice();
        }
        if(f != null) System.out.println(airport[f.getFrom()-1]+"\nCheapest path price: $" + runningTotal + "\n");
    }
    private static void shortestPathByHops() {
        int routeTo = 0;
        int routeFrom = 0;
        String option;
        while(routeFrom == 0) {
            System.out.println("\nSelect a city your route begins at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeFrom = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        while(routeTo == 0) {
            System.out.println("\nSelect a city your route ends at: ");
                listCitiesByUserIndex();
                option = kb.nextLine();
                int optionVal = -1;
                try {
                    optionVal = Integer.parseInt(option);
                    if (optionVal > 0 && optionVal <= airport.length) {
                        routeTo = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        
        Dijkstra sp = new Dijkstra(routeTo, false, true); // false, true b/c comparing hops
        Queue<Flight> path = (Queue<Flight>)sp.pathTo(routeFrom);
        
        System.out.println("FEWEST-HOP PATH FROM " + Airline.airport[routeFrom-1].toUpperCase() + " TO " + Airline.airport[routeTo-1].toUpperCase() + ":\n========================================================");
        if(path == null) {
            System.out.println("There is no path between the chosen cities.");
            return;
        }
        Flight f = new Flight();
        int runningTotal = 0;
        while(!path.isEmpty()) {
            f = path.dequeue();
            System.out.print(airport[f.getTo()-1] + " -> ");
            runningTotal++;
        }
        if(f != null) System.out.println(airport[f.getFrom()-1] + "\nTotal hops:" + runningTotal + "\n");
    }
    
    private static void tripsLessThan() {
        double maxCost = 0;
        String option;
        while(maxCost <= 0) {
            System.out.println("\nEnter a max price in dollars($): ");
                option = kb.nextLine();
                double optionVal = -1;
                try {
                    optionVal = Double.parseDouble(option);
                    if (optionVal > 0) {
                        maxCost = optionVal;
                    }
                } catch (Exception e) {
                    // do nothing because we don't want to do anything.
                } 
        }
        tripsUnderCost = new MinPQ<>(false);
        for(int i = 0; i < vertex_count; i++) {
            boolean[] visited = new boolean[vertex_count];
            visited[i] = true; // you visited this vertex when you began your journey
            getPaths(i, maxCost, new PathString(airport[i], 0), visited, 0);
        }
        System.out.println("\nTrips under $" + maxCost + ":\nIncludes trips in reverse order as well\n============================================\n");
        while(!tripsUnderCost.isEmpty()) {
            PathString ps = tripsUnderCost.delMin();
            System.out.println("[Cost: $" + ps.getCost() + "] " + ps.getPathString() + '\n');
        }
    }
    
    private static void getPaths(int sv, double maxCost, PathString ps, boolean[] visited, int currentStop) {
        if(currentStop != 0) tripsUnderCost.insert(ps); // if not root, add this stop
        LinkedList ll = (LinkedList)flights[sv];
        ListIterator<Flight> li = ll.listIterator();
        while(li.hasNext()) {
            Flight f = li.next();
            if(!visited[f.getTo()-1]) {
                if(ps.getCost() + f.getPrice() < maxCost) {
                    PathString psNew = new PathString(ps.getPathString()+" ->$" + f.getPrice() + "-> " + airport[f.getTo()-1] + "", ps.getCost() + f.getPrice());
                    visited[f.getTo()-1] = true;
                    getPaths(f.getTo()-1, maxCost, psNew, visited, currentStop+1);
                    visited[f.getTo()-1] = false; // reset after recursive call returns
                }
            }
        }
    }
    
}