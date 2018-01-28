public class UF {
    // code from UF.pdf
    private int count;  // how many vertexes are in the graph
    int[] id;           // array of ids
    
    public UF(int inputSize) {
        count = inputSize;
        id = new int[count];
        for (int i = 0; i < count; i++) {
            id[i] = i;
        }
    }
    
    public int find(int to) {
        return id[to]; // return union ID of a given vertex
    }
    
    public boolean connected(int to, int from) { 
        return find(to-1) == find(from-1);
    }
    
    public void union(int to, int from) {
        int toID = find(to-1); // Union ID of p
        int fromID = find(from-1); // Union ID of q
        if(toID == fromID)
            return; // they're already in the same group, do nothing
        for(int i = 0; i < id.length; i++) {
            if(id[i] == toID)
                id[i] = fromID; //  set all unions of toID in set to fromID
        }
        count--; // 
    }
}