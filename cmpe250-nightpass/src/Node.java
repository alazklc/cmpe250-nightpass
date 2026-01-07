import java.util.ArrayList;
public class Node {
    public Card card;
    public Node leftNode;
    public Node rightNode;
    public int nodeHeight;
    public int subTreeMaxHealth;
    public int subTreeMinHealth;

    Node(Card card) {
        this.card = card;
        this.leftNode = null;
        this.rightNode = null;
        this.nodeHeight = 0;

        // For a leaf node, its subtree stats are just its own card's stats
        this.subTreeMaxHealth = card.currentHealth;
        this.subTreeMinHealth = card.currentHealth;
    }

}
