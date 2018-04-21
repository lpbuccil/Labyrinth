/**
 * Name: Lucas Buccilli
 * Class: CS2321
 * Project: Labyrinth
 * Description: Implementation of DFS, BFS and Dijkstra
 */

package cs2321;

import net.datastructures.Edge;
import net.datastructures.Entry;
import net.datastructures.Graph;
import net.datastructures.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/* CS2321 Project: The Labyrinth
 * 
 * Do NOT change the setupLabyrinth function.
 *         
 * Implement the dfsPath, bfsPath, shortestPath, and totalPathDistance functions below.
 *
 */
public class Labyrinth {
    public static final int WALL = 1;
    public static final String PARSE_CHARACTER = ",";

    private Graph<RoomCoordinate, Walkway> mGraph;
    private int mWidth = -1;
    private int mHeight = -1;

    public Labyrinth(String aFileName) {
        mGraph = setupLabyrinth(aFileName);
        
    }

    /*
     * Complete the totalPathDistance function, which calculates how far the
     * given path traverses.
     */
    public static double totalPathDistance(Iterable<Edge<Walkway>> path) {
        //Adds all distances of each edge
        double totalDistance = 0.0;
        for (Edge<Walkway> e : path) {
            totalDistance += e.getElement().getDistance();
        }

        return totalDistance;
    }

    public static void main(String[] aArguments) {
        Labyrinth lLabyrinth = new Labyrinth("SmallLabyrinth.txt");

    }

    /*
     * Width of the labyrinth (# of squares, not pixels)
     */
    public int getWidth() {
        return mWidth;
    }

    /*
     * Height of the labyrinth (# of squares, not pixels)
     */
    public int getHeight() {
        return mHeight;
    }

    /*
     * Create the graph based on the maze specification in the input file !!!
     * Don't Change this method !!!
     */
    public Graph<RoomCoordinate, Walkway> setupLabyrinth(String aFileName) {
        Scanner lFile = null;
        try {
            lFile = new Scanner(new File(aFileName));
            lFile.useDelimiter(",\n");
        } catch (FileNotFoundException eException) {
            System.out.println(eException.getMessage());
            eException.printStackTrace();
            System.exit(-1);
        }

        //You need to copy your Adjacent Graph implementation to this project.
        // Otherwise the following line has compiler error because AdjListGraph(...) does not exist.
        Graph<RoomCoordinate, Walkway> lGraph = new AdjListGraph<RoomCoordinate, Walkway>();

        try {
            int lXSize = 0;
            int lYSize = 0;
            if (lFile.hasNext()) {
                String[] lDimensions = lFile.nextLine().split(PARSE_CHARACTER);
                lXSize = Integer.parseInt(lDimensions[0]);
                lYSize = Integer.parseInt(lDimensions[1]);
            }

            mWidth = lXSize;
            mHeight = lYSize;

			/* Create all the room coordinates */
            Vertex<?>[][] lVertices = new Vertex<?>[lXSize][lYSize];
            for (int lYIndex = 0; lYIndex < lYSize; lYIndex++) {
                for (int lXIndex = 0; lXIndex < lXSize; lXIndex++) {
                    RoomCoordinate lNextRoomCoordinate = new RoomCoordinate(lXIndex, lYIndex);
                    Vertex<RoomCoordinate> lNextRoom = lGraph.insertVertex(lNextRoomCoordinate);
                    lVertices[lXIndex][lYIndex] = lNextRoom;
                }
            }

            for (int lYIndex = 0; lYIndex < lYSize; lYIndex++) {
                String[] lWalls = lFile.nextLine().split(PARSE_CHARACTER);
                for (int lXIndex = 0; lXIndex < lXSize; lXIndex++) {
                    if (Integer.parseInt(lWalls[lXIndex]) != WALL) {
                        Vertex<RoomCoordinate> lVertex1 = (Vertex<RoomCoordinate>) lVertices[lXIndex][lYIndex];
                        Vertex<RoomCoordinate> lVertex2 = (Vertex<RoomCoordinate>) lVertices[lXIndex][lYIndex - 1];

                        Walkway lNewWalkway = new Walkway(
                                lVertex1.getElement().toString() + lVertex2.getElement().toString(),
                                Integer.parseInt(lWalls[lXIndex]));
                        lGraph.insertEdge(lVertex1, lVertex2, lNewWalkway);
                    }
                }
            }

            for (int lYIndex = 0; lYIndex < lYSize; lYIndex++) {
                String[] lWalls = lFile.nextLine().split(PARSE_CHARACTER);
                for (int lXIndex = 0; lXIndex < lXSize; lXIndex++) {
                    if (Integer.parseInt(lWalls[lXIndex]) != WALL) {
                        Vertex<RoomCoordinate> lVertex1 = (Vertex<RoomCoordinate>) lVertices[lXIndex][lYIndex];
                        Vertex<RoomCoordinate> lVertex2 = (Vertex<RoomCoordinate>) lVertices[lXIndex - 1][lYIndex];

                        Walkway lNewWalkway = new Walkway(
                                lVertex1.getElement().toString() + lVertex2.getElement().toString(),
                                Integer.parseInt(lWalls[lXIndex]));
                        lGraph.insertEdge(lVertex1, lVertex2, lNewWalkway);
                    }
                }
            }
        } catch (Exception eException) {
            System.out.println(eException.getMessage());
            eException.printStackTrace();
            System.exit(-1);
        }

        return lGraph;
    }

    /**
     * Complete the dfsPath function by implementing a Depth First Search
     * algorithm to find a path from start to end. At each vertex, the adjacent
     * edges shall be searched in the order of NORTH, EAST, SOURTH and WEST.
     *
     * @param start an RoomCoordinate object represents the start location,
     *              this object does not exist in the graph
     * @param end   an RoomCoordinate object represents the start location,
     *              this object does not exist in the graph
     * @return Return the sequence of edges traversed in order to get from the
     * start to the finish locations. If there is
     * NO path, do NOT return null. Return an empty sequence.
     */
    @TimeComplexity("O(n+m)")
    public Iterable<Edge<Walkway>> dfsPath(RoomCoordinate start, RoomCoordinate end) {
        /*TCJ
         * We traverse each node in the graph atlest once (n), for each node we traverse, we also traverse the adjList (total number of edges)
         */

        HashMap<Vertex<RoomCoordinate>, Boolean> visited = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        HashMap<Vertex<RoomCoordinate>, Edge<Walkway>> forest = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        ArrayList<Edge<Walkway>> path = new ArrayList<>();


        //Find start and end vertex
        Vertex<RoomCoordinate> v = null;
        Vertex<RoomCoordinate> z = null;
        for (Vertex<RoomCoordinate> r : mGraph.vertices()) {
            if (r.getElement().compareTo(start) == 0) {
                v = r;
            }
            if (r.getElement().compareTo(end) == 0) {
                z = r;
            }
        }

        //run dfs from start vertex
        //room coordinates were not in graph
        if ((v == null) || (z == null)) {
            return path;
        } else {
            DFS(mGraph, v, forest, visited);
        }

        //start at the end vertex, get the path leading into z, add to path, then get the vertex opposite of z, continue until you hit start
        //no path exists
        if (forest.get(z) == null) {
            return path;
        } else {
            while (z != v) {

                Edge<Walkway> E = forest.get(z);
                path.addFirst(E);
                z = mGraph.opposite(z, E);
            }
            return path;
        }


    }

    public void DFS(Graph<RoomCoordinate, Walkway> G, Vertex<RoomCoordinate> u, HashMap<Vertex<RoomCoordinate>, Edge<Walkway>> forest, HashMap<Vertex<RoomCoordinate>, Boolean> visited) {

        //add u to visited
        visited.put(u, true);

        //create vertices for where to go next
        Vertex<RoomCoordinate> north = null;
        Vertex<RoomCoordinate> east = null;
        Vertex<RoomCoordinate> south = null;
        Vertex<RoomCoordinate> west = null;


        //for each edge leaving u (there can only be a max of 4)
        for (Edge<Walkway> e : G.outgoingEdges(u)) {
            //Get the vertex connected to the edge and u; w-e-u
            Vertex<RoomCoordinate> w = G.opposite(u, e);

            //compare the coordinates and set each vertex w to its proper compass location

            //x values are the same, so it must be north or south
            if (w.getElement().getX() == u.getElement().getX()) {
                //compare y values
                if (w.getElement().getY() > u.getElement().getY()) {
                    south = w;
                } else {
                    north = w;
                }
                //x values are different, must be west or south
            } else if (w.getElement().getX() < u.getElement().getX()) {
                west = w;
            } else {
                east = w;
            }
        }

        //check if north vertex has been visited, if not, add to forest and run dfs
        if (north != null) {
            if (visited.get(north) == null) {
                forest.put(north, G.getEdge(north, u));
                DFS(G, north, forest, visited);
            }
        }

        //check if east vertex has been visited, if not, add to forest and run dfs
        if (east != null) {
            if (visited.get(east) == null) {
                forest.put(east, G.getEdge(east, u));
                DFS(G, east, forest, visited);
            }
        }

        //check if south vertex has been visited, if not, add to forest and run dfs
        if (south != null) {
            if (visited.get(south) == null) {
                forest.put(south, G.getEdge(south, u));
                DFS(G, south, forest, visited);
            }
        }

        //check if west vertex has been visited, if not, add to forest and run dfs
        if (west != null) {
            if (visited.get(west) == null) {
                forest.put(west, G.getEdge(west, u));
                DFS(G, west, forest, visited);
            }
        }
    }

    /**
     * Complete the dfsPath function by implementing a Breadth First Search
     * algorithm to find a path from start to end. At each vertex, the adjacent
     * edges shall be searched in the order of NORTH, EAST, SOURTH and WEST.
     *
     * @param start an RoomCoordinate object represents the start location,
     *              this object does not exist in the graph
     * @param end   an RoomCoordinate object represents the start location,
     *              this object does not exist in the graph
     * @return Return the sequence of edges traversed in order to get from the
     * start to the finish locations. If there is
     * NO path, do NOT return null. Return an empty sequence.
     */
    @TimeComplexity("O(n+m)")
    public Iterable<Edge<Walkway>> bfsPath(RoomCoordinate start, RoomCoordinate end) {
        /*TCJ
         * We traverse each node in the graph atlest once (n), for each node we traverse, we also traverse the adjList (total number of edges)
         */

        HashMap<Vertex<RoomCoordinate>, Boolean> visited = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        HashMap<Vertex<RoomCoordinate>, Edge<Walkway>> forest = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        DoublyLinkedList<Vertex<RoomCoordinate>> queue = new DoublyLinkedList<>();
        ArrayList<Edge<Walkway>> path = new ArrayList<>();

        //find start and end vertices
        Vertex<RoomCoordinate> startV = null;
        Vertex<RoomCoordinate> endV = null;
        for (Vertex<RoomCoordinate> r : mGraph.vertices()) {
            if (r.getElement().compareTo(start) == 0) {
                startV = r;
            }
            if (r.getElement().compareTo(end) == 0) {
                endV = r;
            }
        }

        //room coordinates were not in graph
        if ((startV == null) || (endV == null)) {
            return path;
        } else {
            BFS(mGraph, startV, visited, forest, queue);
        }

        //start at the end vertex, get the path leading into z, add to path, then get the vertex opposite of z, continue until you hit start


        //no path exists
        if (forest.get(endV) == null) {
            return path;
        } else {
            while (endV != startV) {

                Edge<Walkway> E = forest.get(endV);
                path.addFirst(E);
                endV = mGraph.opposite(endV, E);
            }
            return path;
        }
    }

    private void BFS(Graph<RoomCoordinate, Walkway> G, Vertex<RoomCoordinate> v, HashMap<Vertex<RoomCoordinate>, Boolean> visited, HashMap<Vertex<RoomCoordinate>, Edge<Walkway>> forest, DoublyLinkedList<Vertex<RoomCoordinate>> queue) {


        //create vertices for directions to go
        Vertex<RoomCoordinate> north = null;
        Vertex<RoomCoordinate> east = null;
        Vertex<RoomCoordinate> south = null;
        Vertex<RoomCoordinate> west = null;

        //put v in visited
        visited.put(v, true);

        //engueue
        queue.addLast(v);

        //while we still have unexplored vertices
        while (queue.size() > 0) {
            //dequeue
            Vertex<RoomCoordinate> w = queue.removeFirst();

            //for each edge attached to w
            for (Edge<Walkway> e : G.outgoingEdges(w)) {

                //get vertex attached to edge opposite if w, u-e-w
                Vertex<RoomCoordinate> u = G.opposite(w, e);

                //set each vertext to apporiate position
                if (u.getElement().getX() == w.getElement().getX()) {
                    if (u.getElement().getY() > w.getElement().getY()) {
                        south = u;
                    } else {
                        north = u;
                    }
                } else if (u.getElement().getX() < w.getElement().getX()) {
                    west = u;
                } else {
                    east = u;
                }
            }

            //go to north vertex first
            if (north != null) {
                if (visited.get(north) == null) {
                    queue.addLast(north);
                    visited.put(north, true);
                    forest.put(north, G.getEdge(north, w));
                }
            }

            if (east != null) {
                if (visited.get(east) == null) {
                    queue.addLast(east);
                    visited.put(east, true);
                    forest.put(east, G.getEdge(east, w));
                }
            }
            if (south != null) {
                if (visited.get(south) == null) {
                    queue.addLast(south);
                    visited.put(south, true);
                    forest.put(south, G.getEdge(south, w));
                }
            }
            if (west != null) {
                if (visited.get(west) == null) {
                    queue.addLast(west);
                    visited.put(west, true);
                    forest.put(west, G.getEdge(west, w));
                }
            }
        }
    }

    /**
     * Complete the shortestPath function by implementing Dijkstra's
     * algorithm to find a path from start to end. At each vertex, the adjacent
     * edges shall be searched in the order of NORTH, EAST, SOURTH and WEST.
     *
     * @param start an RoomCoordinate object represents the start location,
     *              this object does not exist in the graph
     * @param end   an RoomCoordinate object represents the start location,
     *              this object does not exist in the graph
     * @return Return the sequence of edges traversed in order to get from the
     * start to the finish locations. If there is
     * NO path, do NOT return null. Return an empty sequence.
     */
    @TimeComplexity("O((n+m)log(n)")
    public Iterable<Edge<Walkway>> shortestPath(RoomCoordinate start, RoomCoordinate end) {
        /*TCJ
         * We updates the distance for each node (n) by adding to our heapPQ (log(n))
         * Thus we have (nlogn), then for each vertex, attached to our starting, we search out from there. In worst case
         * all vertices are connected (n), we then remove min from pq (logn) then wel look at all vertices attached to the pqmin
         * deg(pqmin) then after we replace the key on the pq, which may require a resort (logn)
         * now we have (2nlogn+nlogn)
         */

        HashMap<Vertex<RoomCoordinate>, Double> shortestDistance = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        HashMap<Vertex<RoomCoordinate>, Double> cloud = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        HeapPQ<Double, Vertex<RoomCoordinate>> adaptablePQ = new HeapPQ<>();
        HashMap<Vertex<RoomCoordinate>, Entry<Double, Vertex<RoomCoordinate>>> pqEntry = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        HashMap<Vertex<RoomCoordinate>, Edge<Walkway>> forest = new HashMap<>((int) ((double) mGraph.numVertices() * 1.8));
        ArrayList<Edge<Walkway>> path = new ArrayList<>();


        //find start and end vertex
        Vertex<RoomCoordinate> startV = null;
        Vertex<RoomCoordinate> endV = null;
        for (Vertex<RoomCoordinate> r : mGraph.vertices()) {
            if (r.getElement().compareTo(start) == 0) {
                startV = r;
            }
            if (r.getElement().compareTo(end) == 0) {
                endV = r;
            }
        }

        //coordinates were not in graph
        if ((startV == null) || (endV == null)) {
            return path;
        } else {
            dijkstra(mGraph, startV, shortestDistance, cloud, adaptablePQ, pqEntry, forest);
        }

        //path doesnt exist
        if (forest.get(endV) == null) {
            return path;
        } else {
            //construct path
            while (endV != startV) {
                Edge<Walkway> E = forest.get(endV);
                path.addFirst(E);
                endV = mGraph.opposite(endV, E);
            }
            return path;
        }
    }

    private void dijkstra(Graph<RoomCoordinate, Walkway> G, Vertex<RoomCoordinate> src, HashMap<Vertex<RoomCoordinate>, Double> shortestDistance, HashMap<Vertex<RoomCoordinate>, Double> cloud, HeapPQ<Double, Vertex<RoomCoordinate>> adaptablePQ, HashMap<Vertex<RoomCoordinate>, Entry<Double, Vertex<RoomCoordinate>>> pqEntry, HashMap<Vertex<RoomCoordinate>, Edge<Walkway>> forest) {

        double distance;
        //set default distances for all vertices
        for (Vertex<RoomCoordinate> v1 : G.vertices()) {
            if (v1 == src) {
                distance = 0.0;
            } else {
                distance = Double.MAX_VALUE;
            }

            //add all vertices to the known shortest distance
            shortestDistance.put(v1, distance);

            //add to PQ and update pqEntry
            Entry<Double, Vertex<RoomCoordinate>> e1 = adaptablePQ.insert(distance, v1);
            pqEntry.put(v1, e1);
        }

        //while we still have unexplored vertices
        while (adaptablePQ.size() > 0) {
            //get smallest distance
            Entry<Double, Vertex<RoomCoordinate>> entry = adaptablePQ.removeMin();
            distance = entry.getKey();
            Vertex<RoomCoordinate> v = entry.getValue();
            //update cloud
            cloud.put(v, distance);

            Vertex<RoomCoordinate> north = null;
            Vertex<RoomCoordinate> east = null;
            Vertex<RoomCoordinate> south = null;
            Vertex<RoomCoordinate> west = null;

            //find and set vertices nsew of vertex
            for (Edge<Walkway> e : G.outgoingEdges(v)) {


                Vertex<RoomCoordinate> w = G.opposite(v, e);

                if (w.getElement().getX() == v.getElement().getX()) {
                    if (w.getElement().getY() > v.getElement().getY()) {
                        south = w;
                    } else {
                        north = w;
                    }
                } else if (w.getElement().getX() < v.getElement().getX()) {
                    west = w;
                } else {
                    east = w;
                }

            }

            //if the vertex in the north position exists
            if (north != null) {
                if (cloud.get(north) == null) {
                    //get possible new distance
                    Double newDistance = distance + G.getEdge(north, v).getElement().getDistance();

                    //check if distance is better
                    if (newDistance < shortestDistance.get(north)) {

                        //if it is, update known shortest distance, pw, and forest
                        shortestDistance.put(north, newDistance);
                        adaptablePQ.replaceKey(pqEntry.get(north), newDistance);
                        forest.put(north, G.getEdge(north, v));
                    }
                }
            }

            if (east != null) {
                if (cloud.get(east) == null) {
                    Double newDistance = distance + G.getEdge(east, v).getElement().getDistance();
                    if (newDistance < shortestDistance.get(east)) {
                        shortestDistance.put(east, newDistance);
                        adaptablePQ.replaceKey(pqEntry.get(east), newDistance);
                        forest.put(east, G.getEdge(east, v));
                    }
                }
            }

            if (south != null) {
                if (cloud.get(south) == null) {
                    Double newDistance = distance + G.getEdge(south, v).getElement().getDistance();
                    if (newDistance < shortestDistance.get(south)) {
                        shortestDistance.put(south, newDistance);
                        adaptablePQ.replaceKey(pqEntry.get(south), newDistance);
                        forest.put(south, G.getEdge(south, v));
                    }
                }
            }

            if (west != null) {
                if (cloud.get(west) == null) {
                    Double newDistance = distance + G.getEdge(west, v).getElement().getDistance();
                    if (newDistance < shortestDistance.get(west)) {
                        shortestDistance.put(west, newDistance);
                        adaptablePQ.replaceKey(pqEntry.get(west), newDistance);
                        forest.put(west, G.getEdge(west, v));
                    }
                }
            }
        }
    }


}
