package com.example.logicPart;
import java.util.*;
//import java.util.Map.Entry;
//import java.util.stream.Collectors;

class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
class Edge {
    float price, time;
    char mode;
    int destination;

    public Edge(float price, float time, char mode, int destination) {
        this.price = price;
        this.time = time;
        this.mode = mode;
        this.destination = destination;
    }
}

public class Graph {
    static List<List<Edge>> adj = new ArrayList<>();
    public static Map<String, Integer> stateMappingNumber = new HashMap<>();
    public static  Map<Integer, String> numberMappingState = new HashMap<>();
    public static int countOfState;


    public Graph() {
        int MAX_STATES = 1000000;
        for (int i = 0; i < MAX_STATES; i++) {
            adj.add(new ArrayList<>());
        }
        countOfState = 0;
    }

    static void pathremove(StringBuilder s) {
        int n = s.length() - 1;
        while (n >= 0) {
            char c = s.charAt(n);
            if (c == '*') {
                s.deleteCharAt(n);
                break;
            } else {
                s.deleteCharAt(n);
            }
            n--;
        }
    }
    public String allRoute(int source, int destination) {
        for (Map.Entry<String, Integer> entry : stateMappingNumber.entrySet()) {
            numberMappingState.put(entry.getValue(), entry.getKey());
        }
        int[] vis = new int[countOfState];
        StringBuilder ans = new StringBuilder();
        StringBuilder path = new StringBuilder();
//        bfs(source, destination, vis, ans);
        dfs(source, destination, vis, ans, path);
        return ans.toString();
    }

    static void bfs(int source, int destination, int[] vis, StringBuilder ans) {
        Queue<Pair<Integer, StringBuilder>> queue = new LinkedList<>();
        queue.offer(new Pair<>(source, new StringBuilder()));
        vis[source] = 1;

        while (!queue.isEmpty()) {
            int node = queue.peek().getKey();
            StringBuilder path = queue.peek().getValue();
            queue.poll();

            if (node == destination) {
                ans.append(path);
                ans.append("?");
                return;
            }

            for (Edge it : adj.get(node)) {
                if (vis[it.destination] == 0) {
                    StringBuilder newPath = new StringBuilder(path.toString());
                    newPath.append("*");
                    newPath.append(numberMappingState.get(node));
                    newPath.append("_");
                    newPath.append(it.mode);
                    newPath.append("_");
                    newPath.append(numberMappingState.get(it.destination));
                    queue.offer(new Pair<>(it.destination, newPath));
                    vis[it.destination] = 1;
                }
            }
        }
    }

    private static void addInGraph(int source, int destination, float price, float time, char mode) {
        adj.get(source).add(new Edge(price, time, mode, destination));
    }


    public void makeGraph(String source, String destination, float price, float time, char mode) {
        int sourceNum, destinationNum;
        if (stateMappingNumber.containsKey(source)) {
            sourceNum = stateMappingNumber.get(source);
        } else {
            stateMappingNumber.put(source, countOfState);
            sourceNum = countOfState;
            countOfState++;
        }

        if (stateMappingNumber.containsKey(destination)) {
            destinationNum = stateMappingNumber.get(destination);
        } else {
            stateMappingNumber.put(destination, countOfState);
            destinationNum = countOfState;
            countOfState++;
        }
        addInGraph(sourceNum, destinationNum, price, time, mode);
    }


    public String minPrice(int source,int destination) {
        PriorityQueue<Pair<Float, Pair<Integer, StringBuilder>>> pq = new PriorityQueue<>(Comparator.comparingDouble(p -> p.getKey()));
        pq.add(new Pair<>(0.0f, new Pair<>(source, new StringBuilder())));
        for (Map.Entry<String, Integer> entry : stateMappingNumber.entrySet()) {
            numberMappingState.put(entry.getValue(), entry.getKey());
        }
        while (!pq.isEmpty()) {
            float price = pq.peek().getKey();
            int node = pq.peek().getValue().getKey();
            StringBuilder path = pq.peek().getValue().getValue();
            pq.poll();
            int[] vis = new int[countOfState];
            Arrays.fill(vis, 0);
            vis[node] = 1;

            if (node == destination) {
                StringBuilder ans = new StringBuilder();
                int tempNum = 0;
                String tempMode = "";
                String tempSource = numberMappingState.get(source);
                String tempDestination;

                for (int i = 1; i < path.length(); i++) {
                    char currentChar = path.charAt(i);
                    if (currentChar == ' ') {
                        if (!tempMode.isEmpty()) {
                            ans.append("Take ").append(tempMode).append(" From ").append(tempSource).append(" to ");
                            tempDestination = numberMappingState.get(tempNum);
                            ans.append(tempDestination).append(" then ");

                            tempNum = 0;
                            tempMode = "";
                            tempSource = tempDestination;
                            tempDestination = "";
                        }
                    } else if (currentChar == '_') {
                        continue;
                    } else if (currentChar >= 'A' && currentChar <= 'Z') {
                        if (currentChar == 'B') {
                            tempMode += "Bus";
                        } else if (currentChar == 'F') {
                            tempMode += "Flight";
                        } else {
                            tempMode += "Train";
                        }
                    } else {
                        tempNum = tempNum * 10 + (currentChar - '0');
                    }
                }
                ans.append(" it will take ").append(price).append("Rs that is minimum price");
                return ans.toString();
            }

            for (Edge it : adj.get(node)) {
                if (vis[it.destination] == 0) {
                    StringBuilder temp = new StringBuilder(" ").append(it.destination).append("_").append(it.mode).append(" ");
                    pq.add(new Pair<>(price + it.price, new Pair<>(it.destination, new StringBuilder(path.toString() + temp.toString()))));
                }
            }
        }
        return "no route";
    }

    public String minTime(int source, int destination) {
        PriorityQueue<Pair<Float, Pair<Integer, StringBuilder>>> pq = new PriorityQueue<>(Comparator.comparingDouble(p -> p.getKey()));
        pq.add(new Pair<>(0.0f, new Pair<>(source, new StringBuilder())));
        for (Map.Entry<String, Integer> entry : stateMappingNumber.entrySet()) {
            numberMappingState.put(entry.getValue(), entry.getKey());
        }
        int[] vis = new int[countOfState];
        Arrays.fill(vis, 0);

        for (Map.Entry<String, Integer> entry : stateMappingNumber.entrySet())
            numberMappingState.put(entry.getValue(), entry.getKey());

        while (!pq.isEmpty()) {
            float time = pq.peek().getKey();
            int node = pq.peek().getValue().getKey();
            StringBuilder path = pq.peek().getValue().getValue();
            pq.poll();
            vis[node] = 1;

            if (node == destination) {
                StringBuilder ans = new StringBuilder();
                int tempNum = 0;
                String tempMode = "";
                String tempSource = numberMappingState.get(source);
                String tempDestination;

                for (int i = 1; i < path.length(); i++) {
                    char currentChar = path.charAt(i);
                    if (currentChar == ' ') {
                        if (!tempMode.isEmpty()) {
                            ans.append("Take ").append(tempMode).append(" From ").append(tempSource).append(" to ");
                            tempDestination = numberMappingState.get(tempNum);
                            ans.append(tempDestination).append(" then ");

                            tempNum = 0;
                            tempMode = "";
                            tempSource = tempDestination;
                            tempDestination = "";
                        }
                    } else if (currentChar == '_') {
                        continue;
                    } else if (currentChar >= 'A' && currentChar <= 'Z') {
                        if (currentChar == 'B') {
                            tempMode += "Bus";
                        } else if (currentChar == 'F') {
                            tempMode += "Flight";
                        } else {
                            tempMode += "Train";
                        }
                    } else {
                        tempNum = tempNum * 10 + (currentChar - '0');
                    }
                }
                ans.append("it will take ").append(time).append(" hours that's Fastest Route");
                return ans.toString();
            }

            for (Edge it : adj.get(node)) {
                if (vis[it.destination] == 0) {
                    StringBuilder temp = new StringBuilder(" ").append(it.destination).append("_").append(it.mode).append(" ");
                    pq.add(new Pair<>(time + it.time, new Pair<>(it.destination, new StringBuilder(path.toString() + temp.toString()))));
                }
            }
        }
        return "no route";
    }

}

class Main {
    public static void main(String[] args) {
    }
}
