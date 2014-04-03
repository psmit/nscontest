package eu.petersmit.nscontest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by psmit on 3/28/14.
 */
public class DepthFirstSearch {
    private SearchTree searchTree;
    private GameData gameData;


    public DepthFirstSearch(SearchTree searchTree, GameData gameData) {
        this.searchTree = searchTree;
        this.gameData = gameData;
    }

    public List<Move> search() {

        SearchTree.Node root = searchTree.getRoot();
        System.err.println(searchTree.minCost(root));
        searchTree.expandNode(root);
        SearchTree.Node lastChild = root.children.get(root.children.size() - 1);
        searchTree.expandNode(lastChild);

        List<Move> moves = new ArrayList<Move>();

        for (SearchTree.Node child : root.children) {
            System.err.println(searchTree.minCost(child));
            moves.add(child.move);
        }

        moves.add(lastChild.move);
        System.err.println("KDJLJDFJDJSFDJSFJDJFLJDSFJ");
        for (SearchTree.Node child : lastChild.children) {
            System.err.println(searchTree.minCost(child));
            moves.add(child.move);
        }

        return moves;
    }
}
