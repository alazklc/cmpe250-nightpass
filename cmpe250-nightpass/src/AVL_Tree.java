import java.util.ArrayList;

public class AVL_Tree {
    public Node rootNode;
    private int sortingMode;
    private int nodeCount;


    AVL_Tree() {
        this.rootNode = null;
        this.nodeCount = 0;
    }

    AVL_Tree(int mode) {
        this.rootNode = null;
        this.nodeCount = 0;
        this.sortingMode = mode; // Set the mode
    }

    public int getNodeCount() {
        return this.nodeCount;
    }

    public int getHeight(Node node){
        if(node==null){
            return -1;
        }
        return node.nodeHeight;
    }

    private int getMaxHealth(Node node) {
        if (node == null) {
            // Use MIN_VALUE so it never wins a "max" comparison
            return Integer.MIN_VALUE;
        }
        return node.subTreeMaxHealth;
    }

    public void setSortingMode(int mode) {
        this.sortingMode = mode;
    }

    public void setNodeCount(int count) {
        this.nodeCount = count;
    }

    private int getMinHealth(Node node) {
        if (node == null) {
            // Use MAX_VALUE so it never wins a "min" comparison
            return Integer.MAX_VALUE;
        }
        return node.subTreeMinHealth;
    }



    //this function  is for collecting the data of subtrees and updating them.
    //ı did this implemention since the first method ı tried without using former subtree information, failed the time constraint.
    private void updateStats(Node node) {
        if (node == null) return;

        // Always update height first
        node.nodeHeight = 1 + Math.max(getHeight(node.leftNode), getHeight(node.rightNode));

        if (this.sortingMode == 3) {
            int leftMin = (node.leftNode == null) ? Integer.MAX_VALUE : node.leftNode.subTreeMinHealth;
            int rightMin = (node.rightNode == null) ? Integer.MAX_VALUE : node.rightNode.subTreeMinHealth;
            int leftMax = (node.leftNode == null) ? Integer.MIN_VALUE : node.leftNode.subTreeMaxHealth;
            int rightMax = (node.rightNode == null) ? Integer.MIN_VALUE : node.rightNode.subTreeMaxHealth;

            node.subTreeMinHealth = Math.min(node.card.missingHealth, Math.min(leftMin, rightMin));
            node.subTreeMaxHealth = Math.max(node.card.missingHealth, Math.max(leftMax, rightMax));
        }

        else {
            int leftMin = (node.leftNode == null) ? Integer.MAX_VALUE : node.leftNode.subTreeMinHealth;
            int rightMin = (node.rightNode == null) ? Integer.MAX_VALUE : node.rightNode.subTreeMinHealth;
            int leftMax = (node.leftNode == null) ? Integer.MIN_VALUE : node.leftNode.subTreeMaxHealth;
            int rightMax = (node.rightNode == null) ? Integer.MIN_VALUE : node.rightNode.subTreeMaxHealth;

            node.subTreeMinHealth = Math.min(node.card.currentHealth, Math.min(leftMin, rightMin));
            node.subTreeMaxHealth = Math.max(node.card.currentHealth, Math.max(leftMax, rightMax));
        }
    }

    private int getBalance(Node node) {
        if (node == null) {
            return 0;
        }else{
            return getHeight(node.leftNode) - getHeight(node.rightNode);
        }
    }

    public int getSortingMode() {
        return this.sortingMode;
    }

    private int comparingCard(Card c1, Card c2){
        if (this.sortingMode == 1){
            if(c1.currentAttack>c2.currentAttack){
                return 1;
            }else if (c1.currentAttack<c2.currentAttack){
                return -1;
            } else{
                if(c1.currentHealth>c2.currentHealth){
                    return 1;
                } else if (c1.currentHealth<c2.currentHealth){
                    return -1;
                } else{
                    if(c1.insertionOrder>c2.insertionOrder){
                        return 1;
                    } else if(c1.insertionOrder<c2.insertionOrder){
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        } else if (this.sortingMode == 2) {
            if(c1.currentAttack<c2.currentAttack){
                return 1;
            }else if (c1.currentAttack>c2.currentAttack){
                return -1;
            } else{
                if(c1.currentHealth>c2.currentHealth){
                    return 1;
                } else if (c1.currentHealth<c2.currentHealth){
                    return -1;
                } else{
                    if(c1.insertionOrder>c2.insertionOrder){
                        return 1;
                    } else if(c1.insertionOrder<c2.insertionOrder){
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        } else { //mode 3
            if (c1.missingHealth > c2.missingHealth) {
                return 1;
            } else if (c1.missingHealth < c2.missingHealth) {
                return -1;
            } else {
                // Tie-breaker: first discarded
                if (c1.insertionOrder > c2.insertionOrder) {
                    return 1;
                } else if (c1.insertionOrder < c2.insertionOrder) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public int getDeckCount(){
        return this.nodeCount;
    }

    public void insertion(Card card) {
        this.rootNode = recursiveInsertion(this.rootNode, new Node(card));
    }

    //left rotation logic used to balance the tree after ı change something in tree.
    public Node leftRotation(Node rootNode){
        Node LR = rootNode.rightNode.leftNode;
        rootNode.rightNode.leftNode = null;

        Node Root = rootNode.rightNode;
        rootNode.rightNode = null;

        Root.leftNode = rootNode;
        Root.leftNode.rightNode = LR;

        updateStats(rootNode);
        updateStats(Root);

        return Root;
    }

    //right rotation logic used to balance the tree after ı change something in tree.
    public Node rightRotation(Node rootNode){

        Node RL = rootNode.leftNode.rightNode;
        rootNode.leftNode.rightNode = null;

        Node Root = rootNode.leftNode;
        rootNode.leftNode = null;

        Root.rightNode = rootNode;
        Root.rightNode.leftNode = RL;

        updateStats(rootNode);
        updateStats(Root);

        return Root;

    }

    public Node recursiveInsertion(Node rootNode, Node node) {
        // This is the BST Insertion Part
        if(rootNode==null){
            this.nodeCount++;
            updateStats(node);
            return node;
        }
        if (comparingCard(rootNode.card, node.card) > 0) {
            rootNode.leftNode = recursiveInsertion(rootNode.leftNode, node);
        } else {
            rootNode.rightNode = recursiveInsertion(rootNode.rightNode, node);
        }

        //Call the balancer method
        return rebalanceAfterInsertion(rootNode, node);
    }

    private Node rebalanceAfterInsertion(Node rootNode, Node node) {
        // Update stats before balancing
        updateStats(rootNode);

        int balance = getBalance(rootNode);

        if(balance > 1){
            if(comparingCard(rootNode.leftNode.card, node.card)>0){

                return rightRotation(rootNode);

            }else {

                rootNode.leftNode = leftRotation(rootNode.leftNode);
                return rightRotation(rootNode);

            }
        } else if(balance < -1){
            if(comparingCard(rootNode.rightNode.card, node.card)<0){

                return leftRotation(rootNode);

            }else{

                rootNode.rightNode = rightRotation(rootNode.rightNode);
                return leftRotation(rootNode);

            }
        } else{
            return rootNode;
        }
    }

    public Card getMinCard() {
        if (this.rootNode == null) {
            return null;
        }
        // Calls  findMinNode method
        return findMinNode(this.rootNode).card;
    }

    private Node findMinNode(Node node) {
        Node current = node;
        while (current.leftNode != null) {
            current = current.leftNode;
        }
        return current;
    }

    private boolean isValid(Card card, int priority, int strAtt, int strHp) {
        if (priority == 1) {
            // Priority 1: Survive AND Kill
            if ( (card.currentHealth > strAtt) && !(card.currentAttack < strHp) ) {
                return true;
            } else {
                return false;
            }
        } else if (priority == 2) {
            // Priority 2: Survive AND NOT Kill
            if ( (card.currentHealth > strAtt) && (card.currentAttack < strHp) ) {
                return true;
            }
        } else if (priority == 3) {
            // Priority 3: Die AND Kill
            if ( (card.currentHealth <= strAtt) && !(card.currentAttack < strHp) ) {
                return true;
            }
        } else if (priority == 4) {
            // Priority 4: Always valid as it's the last chance
            return true;
        }

        // Default case, should not be reached
        return false;
    }

    // deletion method to delete items from tree
    public void deletion(Card card) {
        if (card != null) {
            this.rootNode = recursiveDeletion(this.rootNode, new Node(card));
        }
    }

    //recursive deletion method, actually this part is doing whole thing
    public Node recursiveDeletion(Node rootNode, Node nodeToDelete) {
        if (rootNode == null) {
            return null; // Card not found
        }

        int compare = comparingCard(nodeToDelete.card, rootNode.card);

        if (compare < 0) {
            rootNode.leftNode = recursiveDeletion(rootNode.leftNode, nodeToDelete);
        } else if (compare > 0) {
            rootNode.rightNode = recursiveDeletion(rootNode.rightNode, nodeToDelete);
        } else {
            //Node found
            this.nodeCount--;

            // Case 1: No children or one child
            if (rootNode.leftNode == null) {
                return rootNode.rightNode;
            } else if (rootNode.rightNode == null) {
                return rootNode.leftNode;
            }

            // Case 2: Two children
            // Find in-order largest in the left subtree
            Node largestLeft = findMaxNode(rootNode.leftNode);

            // Copy the other cards data to this node
            rootNode.card = largestLeft.card;

            updateStats(rootNode); //ADDED AT 3 AM

            // Delete the predecessor from the left subtree
            // ı create a new "key" node to ensure the comparison works
            rootNode.leftNode = recursiveDeletion(rootNode.leftNode, new Node(largestLeft.card));

            // I "deleted" the original, but added back the successor's card,
            // so we must increment the node count to compensate the double-decrement.
            this.nodeCount++;
        }

        // If the tree had only one node, it's null now.
        if (rootNode == null) {
            return null;
        }

        // Update stats and rebalance
        updateStats(rootNode);

        int balance = getBalance(rootNode);

        // Left-Left
        if (balance > 1 && getBalance(rootNode.leftNode) >= 0) {
            return rightRotation(rootNode);
        }
        // Left-Right
        if (balance > 1 && getBalance(rootNode.leftNode) < 0) {
            rootNode.leftNode = leftRotation(rootNode.leftNode);
            return rightRotation(rootNode);
        }
        // Right-Right
        if (balance < -1 && getBalance(rootNode.rightNode) <= 0) {
            return leftRotation(rootNode);
        }
        // Right-Left
        if (balance < -1 && getBalance(rootNode.rightNode) > 0) {
            rootNode.rightNode = rightRotation(rootNode.rightNode);
            return leftRotation(rootNode);
        }

        return rootNode;
    }

    private void findBestRecursive(Node rootNode, int priority, int strAtt, int strHp, ArrayList<Node> bestSoFar) {
        if (rootNode == null) {
            return;
        }

        //this is the part we decide where to go at the beginning depending on sorting mode.
        if (this.sortingMode == 1) { // P1/P3 (Min Attack sort)
            if (priority == 3 || priority == 1) {
                // We need to kill (A_cur >= strHp).
                // If this node can't kill, its entire left subtree can't kill.
                if (rootNode.card.currentAttack - strHp < 0) {
                    findBestRecursive(rootNode.rightNode, priority, strAtt, strHp, bestSoFar);
                    return;
                }
            }
        } else { // P2/P4 (Max Attack sort)
            if (priority == 2) {
                // We need to NOT kill (A_cur < strHp).
                // If this node can kill, its entire left subtree (all higher attacks) can kill to.
                if (rootNode.card.currentAttack - strHp >= 0) {
                    findBestRecursive(rootNode.rightNode, priority, strAtt, strHp, bestSoFar);
                    return;
                }
            }
            // For P4, any card is valid.
        }

        // In-Order Traversal

        findBestRecursive(rootNode.leftNode, priority, strAtt, strHp, bestSoFar);

        // If we found a valid card in the left subtree, it's the best one. Stop.
        if (!bestSoFar.isEmpty()) {
            return;
        }

        // 2. current node visiting: Check the current node
        if (isValid(rootNode.card, priority, strAtt, strHp)) {
            bestSoFar.add(rootNode); // This is the smallest valid card
            return;
        }

        // 3. GO RIGHT: We didn't find it on the left or at this node.
        findBestRecursive(rootNode.rightNode, priority, strAtt, strHp, bestSoFar);
    }



    private Node findMaxNode(Node node) {
        Node current = node;
        while (current.rightNode != null) {
            current = current.rightNode;
        }
        return current;
    }


    /**
     * Main function for finding the best card to play in a battle.
     * It just sets up the recursive call.
     */
    public Card deckSearcher(int strangerAttack, int strangerHealth, int priority){
        // We use an ArrayList to pass back the "best card" from the recursive calls
        // this array list is used just as a container to hold the cards reference
        ArrayList<Node> bestPlayableCardSoFar = new ArrayList<>();
        deckSearcherRecursive(this.rootNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);

        // If the list is still empty, we didn't find a card.
        if (bestPlayableCardSoFar.isEmpty()) {
            return null;
        } else {
            // Otherwise, return the card we found.
            return bestPlayableCardSoFar.get(0).card;
        }
    }

    /**
     * This is the helper that does all the searching.
     * The 'priority' variable tells it which set of rules to follow.
     */
    public void deckSearcherRecursive(Node currentNode, int strangerAttack, int strangerHealth, ArrayList<Node> bestPlayableCardSoFar, int priority){
        // I fell off the tree, so stop.
        if (currentNode == null) {
            return;
        }

        // Priorities 0 & 1 are for the Min-Attack Tree
        if(priority == 0 || priority == 1){
            if(priority == 0){

                // If no card in this whole branch can survive, don't bother looking.
                if(currentNode.subTreeMaxHealth <= strangerAttack){
                    return;
                }

                // If this card can not kill, the answer should be on the right.
                if(currentNode.card.currentAttack < strangerHealth){
                    deckSearcherRecursive(currentNode.rightNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);
                    return;
                }

                // This card is able to kill. But is there a better one (having smaller attack) on the left?
                deckSearcherRecursive(currentNode.leftNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);

                // If we found a good card on the left, it is better. I am done.
                if(!bestPlayableCardSoFar.isEmpty()) {
                    return;
                }

                // Left side was empty. Is this node a valid play? (Is it surviving?)
                if(currentNode.card.currentHealth > strangerAttack){
                    bestPlayableCardSoFar.add(currentNode);
                    return;
                }

                // This card isn't valid (it cant survive). Check the right side as a last chance.
                deckSearcherRecursive(currentNode.rightNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);

            } else if (priority == 1) {

                // If every card in this branch survives, they are not what we want. Skip.
                if (currentNode.subTreeMinHealth > strangerAttack) {
                    return;
                }

                // If this card can't kill, check the right side (higher attack).
                if (currentNode.card.currentAttack < strangerHealth) {
                    deckSearcherRecursive(currentNode.rightNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);
                    return;
                }

                // This card can kill. Check left for a better (smaller attack) one.
                deckSearcherRecursive(currentNode.leftNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);

                // Found a better one on the left, so we are done.
                if (!bestPlayableCardSoFar.isEmpty()) {
                    return;
                }

                // Left was empty. Is this node valid? (Does it die?)
                if (currentNode.card.currentHealth <= strangerAttack) {
                    bestPlayableCardSoFar.add(currentNode);
                    return;
                }

                // This card is not valid (it survives). Check the right side.
                deckSearcherRecursive(currentNode.rightNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);
            }
        }
        // Priorities 2 & 3 are for the Max-Attack Tree
        else if(priority == 2 || priority == 3){
            if(priority == 2){


                // If no card in this part can survive, skip it.
                if (currentNode.subTreeMaxHealth <= strangerAttack) {
                    return;
                }

                // We are in the Max-Attack tree. If this card kills, it's not what we want.
                // Check the right side (lower attack).
                if (currentNode.card.currentAttack >= strangerHealth) {
                    deckSearcherRecursive(currentNode.rightNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);
                    return;
                }

                // This card doesn't kill. Is there a better one (higher attack) on the left?
                deckSearcherRecursive(currentNode.leftNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);

                // Found a better one on the left so ı am done.
                if (!bestPlayableCardSoFar.isEmpty()) {
                    return;
                }

                // Left was empty. Is this node valid? (Does it survive?)
                if (currentNode.card.currentHealth > strangerAttack) {
                    bestPlayableCardSoFar.add(currentNode);
                    return;
                }

                // This node isn't valid (it dies). Check the right side.
                deckSearcherRecursive(currentNode.rightNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);

            } else if (priority == 3) {


                // If this card kills, it's invalid. Check the right side (lower attack).
                if (currentNode.card.currentAttack >= strangerHealth) {
                    deckSearcherRecursive(currentNode.rightNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);
                    return;
                }

                // This card is valid (it doesn't kill).
                // Check the left side for a better one (higher attack).
                deckSearcherRecursive(currentNode.leftNode, strangerAttack, strangerHealth, bestPlayableCardSoFar, priority);

                // If the left side didn't have a better card, then this node is the best.
                if (bestPlayableCardSoFar.isEmpty()) {
                    bestPlayableCardSoFar.add(currentNode);
                }
                // If bestSoFar[0] isn't null, it means the one from the left
                // was better (higher attack), so we just keep that one.
            }
        }

    }

    /**
     * Steal Card: Find the card with the minimum attack that is still
     * greater than attL and has health greater than hpL.
     * Searches the Min-Attack tree (deckP1).
     */
    public Card findStealCard(int attL, int hpL) {
        // Use a 1-item ArrayList to pass the result back from the recursion
        //my storage for card reference
        ArrayList<Node> bestSoFar = new ArrayList<>();
        findStealRecursive(this.rootNode, attL, hpL, bestSoFar);

        // Return the card if one was found, otherwise return null
        if (bestSoFar.isEmpty()) {
            return null;
        } else {
            return bestSoFar.get(0).card;
        }
    }


    private void findStealRecursive(Node incomingNode, int attL, int hpL, ArrayList<Node> bestSoFar) {
        if (incomingNode == null) {
            return;
        }

        // If the max health in this entire subtree of the node is too low,
        // we can skip this whole branch.
        if (incomingNode.subTreeMaxHealth <= hpL) {
            return;
        }

        // since ı am  in the Minimum Attack tree.
        // ıf the card is not suitable , the answer must be in the right subtree.
        if (incomingNode.card.currentAttack <= attL) {
            findStealRecursive(incomingNode.rightNode, attL, hpL, bestSoFar);
            return;
        }

        // This card's attack is valid (A > attL).
        // Check the left subtree first for a better card (one with a smaller attack).
        findStealRecursive(incomingNode.leftNode, attL, hpL, bestSoFar);

        // If we found a valid card on the left, it is the new best. Stop here.
        if (!bestSoFar.isEmpty()) {
            return;
        }

        // The left side was empty. Check if this node is valid.
        // We already know A > attL, so just check health.
        if (incomingNode.card.currentHealth > hpL) {
            bestSoFar.add(incomingNode); // This is the best card we've found so far.
            return;
        }

        // This node isn't valid (H <= hpL). Check the right subtree as a last resort.
        findStealRecursive(incomingNode.rightNode, attL, hpL, bestSoFar);
    }

    /**
     * Public function to find the best card to FULLY revive.
     * Finds the card with the largest missingHealth <= healPool.
     */
    public Card findCardForFullRevive(int healPool) {
        ArrayList<Card> bestSoFar = new ArrayList<>();
        findLargestRevivableRecursive(this.rootNode, healPool, bestSoFar);

        if (bestSoFar.isEmpty()) {
            return null;
        } else {
            return bestSoFar.get(0);
        }
    }

    /**
     * Recursive helper to find the card with the largest missingHealth <= healPool.
     * IMPORTANT: when multiple cards have the same missingHealth, choose the one
     * that was discarded earlier (smaller insertionOrder).
     *
     * This method performs a modified in-order traversal (Left, Node, Right)
     * to ensure all nodes are checked correctly for the tie-breaking rule.
     */
    private void findLargestRevivableRecursive(Node currentNode, int healPool, ArrayList<Card> bestSoFar) {
        if (currentNode == null) {
            return; // Base case
        }

        // Branch 1: Current node is NOT a valid candidate (too expensive)
        // If this card's missingHealth is greater than the pool,any card in the right subtree will also be too expensive.
        if (currentNode.card.missingHealth > healPool) {
            findLargestRevivableRecursive(currentNode.leftNode, healPool, bestSoFar);
            return; // We are done with this branch
        }

        // Branch 2: Current node IS a valid candidate (missingHealth <= healPool)
        // Since this node is valid, we must check all three


        // 1. Check Left Subtree
        // We must check the left first. This is critical for the tie-breaker.
        findLargestRevivableRecursive(currentNode.leftNode, healPool, bestSoFar);

        // 2. Check Current Node
        if (bestSoFar.isEmpty()) {
            // The left subtree had no valid candidates. This is the first one.
            bestSoFar.add(currentNode.card);
        } else {
            // Compare this node to the current best
            Card currentBest = bestSoFar.get(0);
            Card candidate = currentNode.card;

            //Prefer larger missingHealth
            if (candidate.missingHealth > currentBest.missingHealth) {
                bestSoFar.set(0, candidate);
            }
            //If missingHealth is equal, prefer smaller insertionOrder
            else if (candidate.missingHealth == currentBest.missingHealth) {
                if (candidate.insertionOrder < currentBest.insertionOrder) {
                    bestSoFar.set(0, candidate);
                }
            }
            //else (candidate.missingHealth < currentBest.missingHealth)
            //This means the 'currentBest' (from the left) is better. Do nothing.
        }

        //Finally, check the right subtree. It may contain a card with an
        //even larger missingHealth (that is still <= healPool).
        findLargestRevivableRecursive(currentNode.rightNode, healPool, bestSoFar);
    }

}