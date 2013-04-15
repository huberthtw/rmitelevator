/**
 * RACT-PAL (RACT Path-Planning Algorithms Library) - A Library of Path Planning
 * Algorithms
 * 
 * Copyright (C) 2010 Abhijeet Anand, RACT - RMIT Agent Contest Team, School of
 * Computer Science and Information Technology,
 * RMIT University, Melbourne VIC 3000.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package au.rmit.ract.planning.pathplanning.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import au.rmit.ract.planning.pathplanning.ai.heuristics.DistanceHeuristics;
import au.rmit.ract.planning.pathplanning.ai.heuristics.Heuristics;
import au.rmit.ract.planning.pathplanning.entity.ComputedPlan;
import au.rmit.ract.planning.pathplanning.entity.Edge;
import au.rmit.ract.planning.pathplanning.entity.State;
import au.rmit.ract.planning.pathplanning.entity.Plan;
import au.rmit.ract.planning.pathplanning.entity.SearchDomain;
import au.rmit.ract.planning.pathplanning.entity.SearchNode;

/**
 * This is a test class to test an extension of Moving Target D-Star Lite for moving sources.
 * 
 * @author Abhijeet Anand (<a href="mailto:abhijeet.anand@rmit.edu.au">abhijeet [dot] anand [at]
 *         rmit [dot] edu [dot] au</a>)
 * 
 */
public class MovingSourceTargetDStarLitePlanner implements PathPlanner {
    
    /**
     * This is the super list containing all the generated {@link SearchNode}s
     * so far.
     */
    private ArrayList<SearchNode>     m_allSNodesList        = null;
    /**
     * The priority queue containing all the open nodes sorted on their key
     */
    private PriorityQueue<SearchNode> m_openList             = null;
    /**
     * As per the definition of DELETED list within MT-D* Lite, this list
     * contains all the nodes in the previous search tree that are not in the
     * sub-tree rooted at the current start node.
     */
    private HashSet<SearchNode>       m_deletedList          = null;
    /**
     * This list contains the expanded nodes during successive iterations.
     */
    private HashSet<State>             m_expandedNodesList    = null;
    /**
     * The heuristics in use by the algorithm
     */
    private DistanceHeuristics        m_heuristics           = null;
    /**
     * Whether the algorithm was initialised during its first run of findPath
     */
    private boolean                   initialised            = false;
    /**
     * 
     */
    private boolean                   mapShiftingIsOptimised = true;
    /**
     * Number of times findPath method was called. This is basically to keep
     * track of reruns of the algorithm.
     */
    private int                       runCount               = 0;
    /**
     * This key modifier provides a lower bound on the priorities calculated for
     * nodes and hence preventing any unnecessary reordering of the PQ
     */
    private float                     k_m                    = 0;
    /**
     * Private reference to the search domain such that it can be shared among
     * methods.
     */
    private SearchDomain              m_map                  = null;
    /**
     * The previous start state/node.
     */
    private SearchNode                sNode_oldStart         = null;
    /**
     * The previous target state/node.
     */
    private SearchNode                sNode_oldGoal          = null;
    /**
     * The current start state/node.
     */
    private SearchNode                sNode_currentStart     = null;
    /**
     * The current goal state/node.
     */
    private SearchNode                sNode_currentGoal      = null;
    /**
     * Previously calculated path.
     */
    private ComputedPlan              m_lastPath             = null;
    /**
     * This constant denotes whether something is traversable; can be
     * Float/Boolean
     */
    private static final float        BLOCKED                = Float.POSITIVE_INFINITY;
    
    // Constants defining annotation indices and their default values.
    private static final int          G                      = /* "g" */0;
    private static final int          RHS                    = /* "rhs" */1;
    private static final int          H                      = /* "h" */2;
    private static final int          KEY1                   = /* "key1" */3;
    private static final int          KEY2                   = /* "key1" */4;
    // private static final int F = /* "f" */5;
    
    private static final float        DEF_G                  = BLOCKED;
    private static final float        DEF_RHS                = BLOCKED;
    
    // private static final float DEF_H = 0;
    
    // private final Object lock = new Object();
    
    /*
     * =======================================================================*
     * ----------------------------- INNER CLASS -----------------------------*
     * =======================================================================*
     */

    /*
     * =======================================================================*
     * ----------------------------- CONSTRUCTORS ----------------------------*
     * =======================================================================*
     */
    /**
     * Creates a Moving Target D-Star Path Planner based on a default heuristic,
     * Manhattan Distance heuristics, which assumes that the search domain is a
     * two-dimensional grid world, having made no assumptions about the
     * connectedness.
     */
    public MovingSourceTargetDStarLitePlanner(DistanceHeuristics h) {
        m_allSNodesList = new ArrayList<SearchNode>();
        m_openList = new PriorityQueue<SearchNode>(11,
                new Comparator<SearchNode>() {
                    
                    
                    public int compare(SearchNode sNode1, SearchNode sNode2) {
                        return compareKeys(sNode1, sNode2);
                    }
                });
        m_deletedList = new HashSet<SearchNode>();
        m_expandedNodesList = new HashSet<State>();
        m_lastPath = new ComputedPlan();
        // m_searchTree = new Tree<SearchNode>();
        m_heuristics = h;
    }
    
    /*
     * =======================================================================*
     * ---------------------------- STATIC METHODS ---------------------------*
     * =======================================================================*
     */

    /*
     * =======================================================================*
     * ---------------------------- PUBLIC METHODS ---------------------------*
     * =======================================================================*
     */
    /**
     * Reset the search such that the next time findPath is called, the search is started from
     * scratch. This is required if the agent moves off path as this situation is not accounted by
     * the algorithm.
     * TODO This method should be made private and the logic to call it, must be implemented by
     * findPath
     */
    public synchronized boolean resetSearch() {
        initialised ^= true;
        runCount = 0;
        
        return !initialised;
    }
    
    public ArrayList<State> currentTreeNodes() {
        ArrayList<State> treeNodes = new ArrayList<State>();
        for (SearchNode treeNode : constructTree()) {
            treeNodes.add(treeNode.getNode());
        }
        
        return treeNodes;
    }
    
    /*
     * =======================================================================*
     * --------------------------- ACCESSOR METHODS --------------------------*
     * =======================================================================*
     */

    /*
     * =======================================================================*
     * --------------------------- MUTATOR METHODS ---------------------------*
     * =======================================================================*
     */

    /*
     * =======================================================================*
     * --------------------- OVERRIDDEN INTERFACE METHODS --------------------*
     * =======================================================================*
     */

    /*
     * (non-Javadoc)
     * @see
     * au.rmit.ract.planning.pathplanning.ai.PathPlanner#findPath(au.rmit.ract.planning.pathplanning
     * .entity.SearchDomain, au.rmit.ract.planning.pathplanning.entity.Node,
     * au.rmit.ract.planning.pathplanning.entity.Node)
     */
    
    public Plan findPath(SearchDomain map, State sNode, State tNode) {
        // Date inTime = Calendar.getInstance().getTime();
        // If the destination is not traversable, there can be no path. Same
        // applies to the start node.
        if (map.isBlocked(sNode) || map.isBlocked(tNode)) {
            return null;
        }
        
        // Initialise the system.
        ++runCount;
        initialise(map, sNode, tNode);
        
        // Do some trick to take ? of source moving off-path
        if (m_lastPath.contains(sNode)) {
            // Follow the planning as normal
        } else {
            // Do some recalculation of g and rhs values.
        }
        
        // Restructure the search tree if in 2+ iteration and Start != Target
        if (runCount > 1 && !sNode.equals(tNode)) {
            restructureSearchTree(sNode, tNode);
        }
        
        if (!sNode.equals(tNode)) { // Current Start != Current Target
        
            sNode_oldStart = sNode_currentStart;
            sNode_oldGoal = sNode_currentGoal;
            
            // Find the path between current start and goal nodes.
            computeCostMinimalPath();
            
            if (sNode_currentGoal.get(RHS) == BLOCKED) {
                return null; /* path does not exist */
            }
            
            // At this point, a path was definitely found, which means we need to
            // create a Path by traversing through the parent pointers
            ComputedPlan path = new ComputedPlan();
            System.out.println("MTDSL: Populating Path"); // SOP
            // Populate the path
            SearchNode target = sNode_currentGoal;
            try {
                while (target != null && !sNode_currentStart.equals(target)) {
                    // System.out.print(target); // SOP target
                    path.prependStep(target.getNode()); // FIXME NPExcptn in 2+ Itr
                    target = target.getParent();
                }
            } catch (Exception e) {
                // TODO Handle exception while creating path
                e.printStackTrace();
            }
            System.out.println("MTDSL: Final Target: " + target); // REMOVE SOP
            if (target == null)
                return null; // Target should be sNode_currentGoal after WHILE ends
            path.prependStep(sNode);
            path.setCost(sNode_currentGoal.get(G));
            System.out.println("MTDSL: Path found"); // SOP
            
            // System.gc(); // Free up lost and unused memory
            
            // Date outTime = Calendar.getInstance().getTime();
            // System.out.println("Time Taken: MTDSTAR: " + (outTime.getTime() - inTime.getTime()));
            // // SOP TimeTaken
            return path;
        }
        
        return null;
    }
    
    /*
     * (non-Javadoc)
     * @see au.rmit.ract.planning.pathplanning.ai.PathPlanner#annotations()
     */
    
    public HashMap<State, String> annotations() {
        HashMap<State, String> annotations = new HashMap<State, String>();
        
        for (SearchNode sNode : m_allSNodesList) {
            String annotationString = "";
            annotationString += "G:" + sNode.get(G);
            annotationString += ", RHS:" + sNode.get(RHS);
            annotationString += ", H:" + sNode.get(H);
            annotationString += ", KEY1:" + sNode.get(KEY1);
            annotationString += ", KEY2:" + sNode.get(KEY2);
            
            annotations.put(sNode.getNode(), annotationString);
        }
        return annotations;
    }
    
    /*
     * (non-Javadoc)
     * @see au.rmit.ract.planning.pathplanning.ai.PathPlanner#expandedNodes()
     */
    
    public ArrayList<State> expandedNodes() {
        return new ArrayList<State>(m_expandedNodesList);
    }
    
    /*
     * (non-Javadoc)
     * @seeau.rmit.ract.planning.pathplanning.ai.PathPlanner#setHeuristics(au.rmit.ract.planning.
     * pathplanning.ai.heuristics.Heuristics)
     */
    
    public boolean setHeuristics(Heuristics heuristics) {
        m_heuristics = DistanceHeuristics.class.cast(heuristics);
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see au.rmit.ract.planning.pathplanning.ai.PathPlanner#unexpandedNodes()
     */
    
    public ArrayList<State> unexpandedNodes() {
        ArrayList<State> unexpanded = new ArrayList<State>();
        for (SearchNode node : m_openList) {
            unexpanded.add(node.getNode());
        }
        return unexpanded;
    }
    
    /*
     * =======================================================================*
     * --------------------------- UTILITY METHODS ---------------------------*
     * =======================================================================*
     */
    /**
     * This method initialises the system only once. Repeated calls to this
     * method will simply return without performing any further initialisation.
     * 
     * @param map
     *            The SearchDomain to be used
     * @param sNode
     *            The start Node
     * @param tNode
     *            The target Node
     */
    private void initialise(SearchDomain map, State sNode, State tNode) {
        if (!initialised) {
            m_openList.clear();
            m_allSNodesList.clear();
            m_deletedList.clear();
            m_expandedNodesList.clear();
            // m_searchTree.clear();
            
            m_map = map;
            k_m = 0;
            sNode_currentStart = newSearchNode(sNode);
            sNode_currentGoal = newSearchNode(tNode);
            
            sNode_currentStart.set(RHS, 0);
            
            insertInOpenList(sNode_currentStart, calculateKey(sNode_currentStart));
            
            // System.out.println("Current Start: " + sNode_currentStart + " Current Goal: "
            // + sNode_currentGoal); // SOP init current start and goal
            // System.out.println("Key Modifier: " + k_m); // SOP init Key Modifier
            
            initialised ^= true;
            return;
        }
        return;
    }
    
    /**
     * Creates and initialises a new SearchNode for the Node provided as
     * parameter. If the node already exists in the super list, returns it
     * instead.
     * 
     * @param node
     * @return A new SearchNode or the one in the super list if created earlier.
     */
    private SearchNode newSearchNode(State node) {
        /*
         * FIXME There may be a possible bug here in code due to
         * creation of a new SearchNode object. This may result in
         * incorrect values of f, g, h etc., associated with every Node.
         * This is due to the fact that at time of creation of
         * SearchNode objects, these values are all reset to a
         * default value, which is not true if such an object was
         * already created previously. This is semantically incorrect
         * and hence a bug.
         */
        SearchNode newNode = new SearchNode(node);
        
        // A dirty hack to fix the above mentioned bug. This needs a more robust
        // and clean implementation. Currently it relies on the Node
        // implementing equals() method properly.
        if (m_allSNodesList.contains(newNode)) {
            int index = m_allSNodesList.indexOf(newNode);
            newNode = null;
            newNode = m_allSNodesList.get(index);
        } else { // Initialise this new SearchNode
            newNode.set(G, DEF_G);
            newNode.set(RHS, DEF_RHS);
            m_allSNodesList.add(newNode);
        }
        return newNode;
    }
    
    /**
     * @param sNode
     * @return
     */
    private float[] calculateKey(SearchNode sNode) {
        try {
            assert sNode != null;
            sNode.set(H, m_heuristics.h(m_map, sNode.getNode(),
                    sNode_currentGoal.getNode()));
            float g_rhs = Math.min(sNode.get(G), sNode.get(RHS));
            
            return new float[] { g_rhs + sNode.get(H) + k_m, g_rhs, sNode.get(G) };
        } catch (Exception e) {
            System.out.println("Exception?? Probably ran out of search nodes. "
                            + "Returning key=[KEY1, KEY2, G][Infinity, Infinity, Infinity]");
            System.out.println("OpenList: " + m_openList);
            return new float[] { BLOCKED, BLOCKED, BLOCKED };
        }
    }
    
    /**
     * 
     */
    private void computeCostMinimalPath() {
        System.out.println("MTDSL: computeCostMinimalPath begins"); // REMOVE SOP
        while ((compareKeys(calculateKey(m_openList.peek()), calculateKey(sNode_currentGoal)) < 0
                || sNode_currentGoal.get(RHS) > sNode_currentGoal.get(G))
                && m_openList.peek() != null) {
            // Get the top node in open list and compare its key with top key
            SearchNode u = m_openList.peek();
            float[] topKey_old = new float[] { u.get(KEY1), u.get(KEY2), u.get(G) };
            float[] topKey_new = calculateKey(u);
            
            if (compareKeys(topKey_old, topKey_new) < 0) {
                updateOpenList(u, topKey_new);
            } else if (u.get(G) > u.get(RHS)) {
                u.set(G, u.get(RHS));
                m_openList.remove(u);
                // System.out.println("Node Expanded: " + u); // SOP Expanded.
                m_expandedNodesList.add(u.getNode());
                
                ArrayList<State> neighbours = m_map.getSuccessors(u.getNode());
                for (State node : neighbours) {
                    SearchNode s = newSearchNode(node);
                    float cost_u_s = m_map.cost(u.getNode(), s.getNode());
                    if (!s.equals(sNode_currentStart)
                            && (s.get(RHS) > (u.get(G) + cost_u_s))) {
                        s.setParent(u);
                        s.set(RHS, u.get(G) + cost_u_s);
                        updateNode(s);
                    }
                }
            } else {
                // System.out.println(">>>>>>>>> Same G and RHS? <<<<<<<<< " + u);
                // SOP same G and RHS?
                
                u.set(G, BLOCKED);
                ArrayList<State> neighbours = m_map.getSuccessors(u.getNode());
                neighbours.add(u.getNode()); // ∀ s ∈ Succ(u) ∪ {u}
                
                for (State node : neighbours) {
                    SearchNode s = newSearchNode(node);
                    if (!sNode_currentStart.equals(s) && u.equals(s.getParent())) {
                        // FIXME NPE;ITR2+
                        Object[] min = leastRhsFromPredecessors(s);
                        s.set(RHS, (Float) min[0]);
                        if (s.get(RHS) == BLOCKED) {
                            s.setParent(null);
                        } else {
                            // s.setParent(predecessorWithLeastRHS(s));
                            s.setParent((SearchNode) min[1]);
                        }
                    }
                    updateNode(s);
                }
            }
        }
        System.out.println("MTDSL: computeCostMinimalPath ends"); // REMOVE SOP
    }
    
    /**
     * @param sNode
     * @param tNode
     */
    private void restructureSearchTree(State sNode, State tNode) {
        // (WHAT FOLLOWS IS IN THE SECOND OR FURTHER ITERATIONS)
        m_expandedNodesList.clear();
        sNode_currentStart = newSearchNode(sNode);
        sNode_currentGoal = newSearchNode(tNode);
        
        // System.out.println("Current Start: " + sNode_currentStart + " Current Goal: "
        // + sNode_currentGoal); // SOP restructure current nodes
        // System.out.println("Old Start: " + sNode_oldStart + " Old Goal: "
        // + sNode_oldGoal); // SOP restructure old nodes
        
        // Adjust the key modifier appropriately
        try {
            k_m += m_heuristics.h(m_map, sNode_oldGoal.getNode(), tNode);
        } catch (Exception e) {
            // TODO Handle the generated exception gracefully while attempting to update k_m
            e.printStackTrace();
        }
        // System.out.println("Key Modifier " + k_m); // SOP k_m new
        
        // if new start is other than old start
        if (!sNode_oldStart.equals(sNode_currentStart)) {
            // Shift the map appropriately
            if (mapShiftingIsOptimised) {
                shiftMapUsingOptimisedDeletion();
            } else {
                shiftMapUsingBasicDeletion();
            }
        }
        
        // Get all the edges with changed costs and process them
        ArrayList<Edge> changes = m_map.getChangedEdges();
        assert changes != null;
        for (Edge edge : changes) {
            SearchNode u = newSearchNode(edge.getStart());
            SearchNode v = newSearchNode(edge.getEnd());
            // System.out.println("U: " + u); // SOP u in edge
            // System.out.println("V: " + v); // SOP v in edge
            
            if (edge.oldCost() > edge.cost()) {
                if (!v.equals(sNode_currentStart)
                        && v.get(RHS) > u.get(G) + edge.cost()) {
                    v.setParent(u);
                    v.set(RHS, u.get(G) + edge.cost());
                    updateNode(v);
                }
            } else {
                if (!v.equals(sNode_currentStart) && v.getParent() != null
                        && v.getParent().equals(u)) {
                    Object[] min = leastRhsFromPredecessors(v);
                    v.set(RHS, (Float) min[0]);
                    if (v.get(RHS) == BLOCKED) {
                        v.setParent(null);
                    } else {
                        // v.setParent(predecessorWithLeastRHS(v));
                        v.setParent((SearchNode) min[1]);
                    }
                    updateNode(v);
                }
            }
        }
        System.out.println("MTDSL: Processed all changes."); // REMOVE SOP
    }
    
    /**
     * 
     */
    private void shiftMapUsingBasicDeletion() {
        sNode_currentStart.setParent(null);
        Object[] min = leastRhsFromPredecessors(sNode_oldStart);
        sNode_oldStart.set(RHS, (Float) min[0]);
        
        if (sNode_oldStart.get(RHS) == BLOCKED) {
            sNode_oldStart.setParent(null);
        } else {
            // sNode_oldStart.setParent(predecessorWithLeastRHS(sNode_oldStart));
            sNode_oldStart.setParent((SearchNode) min[1]);
        }
        updateNode(sNode_oldStart);
    }
    
    /**
     * 
     */
    private void shiftMapUsingOptimisedDeletion() {
        // System.out.println("OptimisedDeletion"); // SOP optim del
        
        // Creating the deleted list using new method
        HashSet<SearchNode> tempDeletedList = constructDeletedList();
        m_deletedList.clear();
        sNode_currentStart.setParent(null);
        
        // The following block of code is missing in the pseudo code but present in Xiaoxun's code
        sNode_oldStart.set(RHS, BLOCKED);
        sNode_oldStart.set(G, BLOCKED);
        sNode_oldStart.setParent(null);
        
        // Creating the deleted list using new method // Temporarily moved here from top
        // HashSet<SearchNode> tempDeletedList = constructDeletedList();
        
        // ∀ s ∈ S in the search tree but not the subtree rooted at s_start
        for (SearchNode searchNode : tempDeletedList) {
            searchNode.setParent(null);
            searchNode.set(RHS, BLOCKED);
            searchNode.set(G, BLOCKED);
            m_openList.remove(searchNode);
            m_deletedList.add(searchNode);
        }
        // ∀ s ∈ DELETED
        for (SearchNode s : m_deletedList) {
            // System.out.println("Unprocessed deleted s: " + s); // SOP unprocessed
            for (State node : m_map.getPredecessors(s.getNode())) {
                SearchNode sPrime = newSearchNode(node);
                float cost_sPrime_s = m_map.cost(node, s.getNode());
                if (s.get(RHS) > sPrime.get(G) + cost_sPrime_s) {
                    s.set(RHS, sPrime.get(G) + cost_sPrime_s);
                    s.setParent(sPrime);
                }
            }
            // System.out.println("Processed deleted s: " + s); // SOP Processed s
            if (s.get(RHS) < BLOCKED) {
                insertInOpenList(s, calculateKey(s));
                // System.out.println("RHS < BLOCKED in DELETED: " + s); // SOP rhs < blocked
            }
        }
    }
    
    /**
     * @param aNode
     * @param bNode
     * @return
     * @throws NullPointerException
     */
    private int compareKeys(SearchNode aNode, SearchNode bNode) throws NullPointerException {
        if (aNode != null && bNode != null) {
            if (aNode.get(KEY1) < bNode.get(KEY1)) {
                return -1;
            } else if (aNode.get(KEY1) > bNode.get(KEY1)) {
                return 1;
            } else { // a.KEY1 == b.KEY1
                if (aNode.get(KEY2) < bNode.get(KEY2)) {
                    return -1;
                } else if (aNode.get(KEY2) > bNode.get(KEY2)) {
                    return 1;
                } else { // Ties could be broken here since the keys are equivalent
                    if (aNode.get(G) < bNode.get(G))
                        return -1;
                    else if (aNode.get(G) > bNode.get(G))
                        return 1;
                    return 0;
                }
            }
        } else {
            throw new NullPointerException("Cannot compare null SearchNodes: " +
                    aNode == null ? "aNode" : "bNode");
        }
    }
    
    /**
     * @param key1
     * @param key2
     * @return
     * @throws IllegalArgumentException
     */
    private int compareKeys(float[] key1, float[] key2)
            throws IllegalArgumentException {
        if (key1.length == 3 && key2.length == 3) {
            if (key1[0] < key2[0]) {
                return -1;
            } else if (key1[0] > key2[0]) {
                return 1;
            } else { // a.KEY1 == b.KEY1
                if (key1[1] < key2[1]) {
                    return -1;
                } else if (key1[1] > key2[1]) {
                    return 1;
                } else { // Ties could be broken here since the keys are equivalent
                    if (key1[2] < key2[2])
                        return -1;
                    else if (key1[2] > key2[2])
                        return 1;
                    return 0;
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Length of the keys cannot be less than 3",
                    new Throwable((key1.length < 3 || key1.length > 3) ? "key1"
                            : "key2"));
        }
    }
    
    /**
     * @param node
     * @param key
     */
    private void insertInOpenList(SearchNode node, float[] key) {
        if (key.length >= 2) {
            node.set(KEY1, key[0]);
            node.set(KEY2, key[1]);
            m_openList.add(node);
        }
    }
    
    /**
     * @param node
     * @param key
     */
    private void updateOpenList(SearchNode node, float[] key) {
        if (key.length >= 2 && m_openList.contains(node) && m_openList.remove(node)) {
            node.set(KEY1, key[0]);
            node.set(KEY2, key[1]);
            m_openList.add(node);
        }
    }
    
    /**
     * @param u
     */
    private void updateNode(SearchNode u) {
        if (u.get(G) != u.get(RHS) && m_openList.contains(u)) {
            updateOpenList(u, calculateKey(u));
        } else if (u.get(G) != u.get(RHS) && !m_openList.contains(u)) {
            insertInOpenList(u, calculateKey(u));
        } else if (u.get(G) == u.get(RHS) && m_openList.contains(u)) {
            m_openList.remove(u);
        }
    }
    
    /**
     * @param aNode
     * @return
     */
    private Object[] leastRhsFromPredecessors(SearchNode aNode) {
        State node = aNode.getNode();
        ArrayList<State> preds = m_map.getPredecessors(node);
        Float minRHS = BLOCKED;
        SearchNode minRhsNode = aNode;
        
        for (State pnode : preds) {
            SearchNode pred = newSearchNode(pnode);
            Float predRHS = pred.get(G) + m_map.cost(pnode, node);
            // minRHS = predRHS < minRHS ? predRHS : minRHS;
            if (predRHS < minRHS) {
                minRHS = predRHS;
                minRhsNode = pred;
            }
        }
        return new Object[] { minRHS, minRhsNode };
    }
    
    /**
     * This method could be [EDIT: should be] merged with leastRhsFromPredecessors
     * 
     * @param aNode
     * @return
     */
    @SuppressWarnings("unused")
    private SearchNode predecessorWithLeastRHS(SearchNode aNode) {
        State node = aNode.getNode();
        ArrayList<State> preds = m_map.getPredecessors(node);
        float minRHS = BLOCKED;
        SearchNode minRhsNode = aNode;
        
        for (State pnode : preds) {
            SearchNode pred = newSearchNode(pnode);
            float predRHS = pred.get(G) + m_map.cost(pnode, node);
            if (predRHS < minRHS) {
                minRHS = predRHS;
                minRhsNode = pred;
            }
        }
        return minRhsNode;
    }
    
    private HashSet<SearchNode> constructDeletedList() {
        HashSet<SearchNode> deletedList = new HashSet<SearchNode>();
        HashSet<SearchNode> tempList = new HashSet<SearchNode>();
        
        // First process all the nodes starting from the ones within open list
        for (SearchNode openNode : m_openList) {
            // Traverse all the way up to the current start or old start
            // Condition in WHILE: parent != null (Old Start)
            // Condition within WHILE: parent != currentStart => TERMINATE/CONTINUE
            tempList.clear();
            SearchNode leaf = openNode;
            boolean next = true;
            while (leaf != null && !leaf.equals(sNode_oldStart) && next) {
                tempList.add(leaf);
                leaf = leaf.getParent();
                if (leaf == null || leaf.equals(sNode_currentStart)) {
                    tempList.clear();
                    next = false;
                }
            }
            deletedList.addAll(tempList); // Append the nodes to Deleted List
        }
        
        // Now process all the ones between old start and current start(excluding).
        SearchNode leaf = sNode_currentStart.getParent();
        tempList.clear();
        while (leaf != null && !leaf.equals(sNode_oldStart)) { // BEGIN WHILE leaf is not null
            tempList.add(leaf);
            leaf = leaf.getParent();
        } // END WHILE leaf is not null
        deletedList.addAll(tempList); // Append the nodes to Deleted List
        
        deletedList.add(sNode_oldStart);
        deletedList.remove(sNode_currentStart);
        
        System.out.println("MTDSL: Deleted list generated."); // REMOVE SOP
        return deletedList;
    }
    
    private HashSet<SearchNode> constructTree() {
        HashSet<SearchNode> tree = new HashSet<SearchNode>();
        
        // for (SearchNode openNode : m_openList) {
        // SearchNode leaf = openNode;
        // while (leaf != null) {
        // tree.add(leaf);
        // leaf = leaf.getParent();
        // }
        // }
        return tree;
    }
    
}
