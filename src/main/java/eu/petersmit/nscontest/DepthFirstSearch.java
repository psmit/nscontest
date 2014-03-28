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
        searchTree.expandNode(root);
        SearchTree.Node lastChild = root.children.get(root.children.size() - 1);
        searchTree.expandNode(lastChild);

        List<Move> moves = new ArrayList<Move>();

        for (SearchTree.Node child : root.children) {
            moves.add(child.move);
        }

        moves.add(lastChild.move);
        for (SearchTree.Node child : lastChild.children) {
            moves.add(child.move);
        }

        return moves;
    }
}
