/**
 * Name:  Lucas Buccilli
 * Username: lpbuccil
 * Description: Implementation of Graph interface. A graph can be declared as either directed or undirected.
 * In the case of an undirected graph, methods outgoingEdges and incomingEdges return the same collection,
 * and outDegree and inDegree return the same value.
 */

package cs2321;

import net.datastructures.*;

/*
 * Implement Graph interface. A graph can be declared as either directed or undirected.
 * In the case of an undirected graph, methods outgoingEdges and incomingEdges return the same collection,
 * and outDegree and inDegree return the same value.
 * 
 * @author CS2321 Instructor
 */
public class AdjListGraph<V, E> implements Graph<V, E> {

    private DoublyLinkedList<Vertex<V>> Verticies = new DoublyLinkedList<>();
    private DoublyLinkedList<Edge<E>> Edges = new DoublyLinkedList<>();
    private boolean DirectedGraph;

    /**
     * Default constructor if boolean directed is specified
     *
     * @param directed
     */
    public AdjListGraph(boolean isDirected) {
        DirectedGraph = isDirected;
    }

    /**
     * Default constructor, assumes graph is not directed
     */
    public AdjListGraph() {
        DirectedGraph = false;
    }

    /**
     * @return the positional list of edges
     */
    public Iterable<Edge<E>> edges() {
        return Edges;
    }

    public void setVisited(Vertex<V> v, boolean b){
        GraphVertex<V> nV = (GraphVertex) v;
        nV.setVisited(b);
    }

    public boolean getVisited(Vertex<V> v){
        GraphVertex<V> nv = (GraphVertex) v;
        return nv.visited;
    }
    /**
     * Returns the verticies the edge connects
     *
     * @param e edge
     * @return vertices that the edge connects to
     * @throws IllegalArgumentException
     */
    public Vertex[] endVertices(Edge<E> e) throws IllegalArgumentException {
        GraphEdge<E> edge = validate(e);
        return edge.getEndpoints();
    }


    /**
     * Adds an edge between two verticies
     *
     * @param u a vertex
     * @param v a vertex
     * @param o element of the edge
     * @return edge with element o between vertices u and v
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("O(1)")
    public Edge<E> insertEdge(Vertex<V> u, Vertex<V> v, E o)
            throws IllegalArgumentException {
        /*TCJ
         * Uses a hash map for adj list. Could be o(n) if hash has to seperate chain
         * Should be o(1) if keys are spread out
         */

        //Check if edge already exists
        if (getEdge(u, v) == null) {
            //Create new edge
            GraphEdge<E> newEdge = new GraphEdge<>(u, v, o);
            //Add edge to Edges list
            newEdge.setPosition(Edges.addLast(newEdge));
            //Check if vertices are valid
            GraphVertex<V> start = validate(u);
            GraphVertex<V> end = validate(v);
            //adds edge to maps of vertices
            start.getOutgoing().put(v, newEdge);
            end.getIncoming().put(u, newEdge);
            return newEdge;
        } else {
            throw new IllegalArgumentException("The edge you are trying to insert already exists");
        }
    }

    /**
     * Adds a new vertex
     *
     * @param o element of the new vertex
     * @return the new vertex in list
     */
    @TimeComplexity("O(1)")
    public Vertex<V> insertVertex(V o) {
        /*TCJ
         * Adds last to doubly linked list
         */
        GraphVertex<V> newVertex = new GraphVertex<>(o, DirectedGraph);
        newVertex.setPosition(Verticies.addLast(newVertex));
        return newVertex;
    }

    /**
     * Returns the number of edges of the graph
     *
     * @return
     */
    public int numEdges() {
        return Edges.size();
    }

    /**
     * Returns the number of vertices in the graph
     *
     * @return
     */
    public int numVertices() {
        return Verticies.size();
    }

    /**
     * Returns the opposite vertex attached to the edge
     *
     * @param v one vertex of the edge
     * @param e the edge
     * @return the other vertex
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(1)")
    public Vertex<V> opposite(Vertex<V> v, Edge<E> e)
            throws IllegalArgumentException {

        //Check if edge and vertex are valid
        GraphEdge<E> edge = validate(e);
        Vertex<V>[] endpoints = edge.getEndpoints();

        //Check what side of the edge the vertex is connected to
        //Returns the other vertex or throws exception if vertex is not attached to edge
        if (endpoints[0] == v) {
            return endpoints[1];
        } else if (endpoints[1] == v) {
            return endpoints[0];
        } else {
            throw new IllegalArgumentException("V is not attached to this endge");
        }
    }

    /**
     * Removes an edge from the graph
     *
     * @param e edge to remove
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("O(1)")
    public void removeEdge(Edge<E> e) throws IllegalArgumentException {
        /*TCJ
         * Uses a hash map for adj list. Could be o(n) if hash has to seperate chain
         * Should be o(1) if keys are spread out
         */

        //Check if edge is valid
        GraphEdge<E> edge = validate(e);

        //Removes the edge from vertices map
        for (Vertex<V> vertex : edge.getEndpoints()) {
            GraphVertex<V> currentVertex = validate(vertex);
            currentVertex.Incoming.remove(opposite(vertex, edge));
            currentVertex.Outgoing.remove(opposite(vertex, edge));
        }
        //Remove the edge from the edge list
        Edges.remove(edge.getPosition());
    }

    /**
     * Removes a vertex from the graph
     *
     * @param v vertex to remove
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(deg(v)")
    public void removeVertex(Vertex<V> v) throws IllegalArgumentException {
        /*TCJ
         * Has to remove each edge that the vertex is attached to
         */
        GraphVertex<V> vertex = validate(v);

        //Remove edge from outgoing map
        for (Edge<E> currentEdge : vertex.getOutgoing().values()) {
            removeEdge(currentEdge);
        }

        //Remove edge from incoming map
        for (Edge<E> currentEdge : vertex.getIncoming().values()) {
            removeEdge(currentEdge);
        }

        //Remove vertex from vertices list
        Verticies.remove(vertex.getPosition());
    }

    /**
     * Replaces the element of the edge
     *
     * @param e edge to replace the element of
     * @param o new element
     * @return old element
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(1)")
    public E replace(Edge<E> e, E o) throws IllegalArgumentException {

        //Validate edge
        GraphEdge<E> edge = validate(e);
        E temp = edge.getElement();
        //set edges elemet to new element
        edge.setElement(o);
        return temp;
    }

    /**
     * Replaces the element of the vertex
     *
     * @param v vertex to replace the element of
     * @param o new element
     * @return old element
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(1)")
    public V replace(Vertex<V> v, V o) throws IllegalArgumentException {
        //Validate vertex
        GraphVertex<V> vertex = validate(v);
        V temp = vertex.getElement();
        //Set vertex's element to new element
        vertex.setElement(o);
        return temp;
    }

    /**
     * Returns list of vertices
     *
     * @return
     */
    public Iterable<Vertex<V>> vertices() {
        return Verticies;
    }

    /**
     * Returns the number of outgoing edges
     *
     * @param v vertex to check degree of
     * @return int of number of outgoing edges
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(1)")
    @Override
    public int outDegree(Vertex<V> v) throws IllegalArgumentException {
        GraphVertex<V> vertex = validate(v);
        return vertex.getOutgoing().size();
    }

    /**
     * Returns the number of incoming edges
     *
     * @param v vertex to check degree of
     * @return int of number of incoming edges
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(1)")
    @Override
    public int inDegree(Vertex<V> v) throws IllegalArgumentException {
        GraphVertex<V> vertex = validate(v);
        return vertex.getIncoming().size();
    }

    /**
     * Returns a list of outgoing edges of a vertex
     *
     * @param v vertex to get list of outgoing edges
     * @return list of outgoing edges
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(1)")
    @Override
    public Iterable<Edge<E>> outgoingEdges(Vertex<V> v)
            throws IllegalArgumentException {
        GraphVertex<V> vertex = validate(v);
        return vertex.getOutgoing().values();
    }

    /**
     * Returns a list of incoming edges of a vertex
     *
     * @param v vertex to get list of incoming edges
     * @return list of incoming edges
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(1)")
    @Override
    public Iterable<Edge<E>> incomingEdges(Vertex<V> v)
            throws IllegalArgumentException {
        GraphVertex<V> vertex = validate(v);
        return vertex.getIncoming().values();
    }

    /**
     * Gets the edge between two vertices
     *
     * @param u one of the vertices
     * @param v another vertex
     * @return the edge if it exists otherwise return null
     * @throws IllegalArgumentException
     */
    @TimeComplexity("O(n)")
    @TimeComplexityExpected("O(1)")
    @Override
    public Edge<E> getEdge(Vertex<V> u, Vertex<V> v)
            throws IllegalArgumentException {
        GraphVertex<V> startingVertex = validate(u);
        return startingVertex.getOutgoing().get(v);
    }

    /**
     * Helper method to validate that the vertex is of type innervertex
     *
     * @param v vertex
     * @return innerVertex of v
     */
    public GraphVertex<V> validate(Vertex<V> v) {
        if (v instanceof GraphVertex) {
            return (GraphVertex<V>) v;
        } else {
            throw new IllegalArgumentException("Vertex v is not a valid instance of InnerVertex");
        }

    }

    /**
     * Helper method to validate that the edge is of type inneredge
     *
     * @param e edge
     * @return innerEdge of e
     */
    public GraphEdge<E> validate(Edge<E> e) {
        if (e instanceof GraphEdge) {
            return (GraphEdge<E>) e;
        } else {
            throw new IllegalArgumentException("Edge e is not a valid instance of InnerEdge");
        }

    }


    /**
     * Class of innerVertex, adds methods to vertex. makes vertex pposition aware and creates a map for incoming and outgoing edges
     *
     * @param <V>
     */
    private class GraphVertex<V> implements Vertex<V> {
        private V Element;
        private Position<Vertex<V>> Position;
        private Map<Vertex<V>, Edge<E>> Outgoing, Incoming;
        boolean visited;

        public GraphVertex(V element, boolean DirectedGraph) {
            Element = element;
            Outgoing = new UnorderedMap<>();

            if (DirectedGraph) {
                Incoming = new UnorderedMap<>();
            } else {
                Incoming = Outgoing;
            }

            visited = false;
        }

        @Override
        public V getElement() {
            return Element;
        }

        public void setElement(V element) {
            Element = element;
        }

        public Position<Vertex<V>> getPosition() {
            return Position;
        }

        public void setPosition(Position<Vertex<V>> p) {
            Position = p;
        }

        public Map<Vertex<V>, Edge<E>> getOutgoing() {
            return Outgoing;
        }

        public Map<Vertex<V>, Edge<E>> getIncoming() {
            return Incoming;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }
    }

    /**
     * Class for inner Edge, ass method to edge. Makes edge position aware for the position list it is in, also creates pointers to the map
     * Of which vertex it points to
     *
     * @param <E>
     */
    private class GraphEdge<E> implements Edge {
        private E Element;
        private Position<Edge<E>> Position;
        private Vertex<V>[] Endpoints;

        public GraphEdge(Vertex<V> u, Vertex<V> v, E element) {
            Element = element;
            Endpoints = (Vertex<V>[]) new Vertex[]{u, v};
        }

        @Override
        public E getElement() {
            return Element;
        }

        public void setElement(E element) {
            Element = element;
        }

        public net.datastructures.Position<Edge<E>> getPosition() {
            return Position;
        }

        public void setPosition(net.datastructures.Position<Edge<E>> position) {
            Position = position;
        }

        public Vertex<V>[] getEndpoints() {
            return Endpoints;
        }
    }

}
