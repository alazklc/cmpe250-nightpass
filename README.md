# Nightpass: Survival Card Game Simulation

**Course:** CMPE250 - Data Structures and Algorithms  
**Context:** Advanced Tree Structures & Game Logic Optimization

## Project Overview

Nightpass is a high-performance simulation engine for a strategic card survival game. The system models a duel between a player ("The Survivor") and an AI opponent ("The Stranger"), involving complex deck management, nightly attacks, and resource optimization.

The primary objective of this project was to implement efficient dynamic data structures to handle massive query volumes under strict time constraints. Unlike standard applications, this project **strictly prohibited** the use of most standard Java Collections (e.g., `TreeMap`, `PriorityQueue`), requiring the implementation of self-balancing binary search trees from scratch.

## Core Capabilities

* **High-Frequency Transaction Processing:** Capable of processing over 500,000 distinct game commands (insertion, deletion, query) within milliseconds.
* **Complex Priority Rules:** Resolves conflicting battle logic (e.g., prioritizing "weakest" vs. "strongest" units) simultaneously without performance degradation.
* **Dynamic State Management:** Handles complex game phases including damage calculation, card revival ("Healing Phase"), and graveyard management.

## Technical Implementation

To achieve $O(\log N)$ time complexity for all critical operations, the simulation relies on a custom implementation of AVL Trees.

### 1. Custom AVL Tree Implementation
A robust, self-balancing Binary Search Tree (BST) was engineered to manage the card decks. This structure ensures that the tree height remains logarithmic relative to the number of nodes, preventing performance degradation even in worst-case insertion scenarios.
* **Features:** Implements standard rotations (Left, Right, Left-Right, Right-Left) to maintain the balance factor.



### 2. Dual-Tree Strategy
A key algorithmic challenge was the requirement to efficiently query both the *minimum* and *maximum* values of the deck simultaneously based on shifting game rules. To solve this, the engine synchronizes two distinct AVL trees:
* **`deckP1`:** Organized to optimize Min-Attack priority queries.
* **`deckP2`:** Organized to optimize Max-Attack priority queries.
Both trees share references to the same underlying Card objects, allowing for rapid state synchronization.

### 3. Optimized I/O
Given the scale of the test cases, standard input methods were insufficient. The project utilizes `BufferedReader` and `BufferedWriter` to handle massive streams of game data with minimal overhead.

## Project Structure

```text
cmpe250-nightpass/
├── src/
│   ├── Main.java       # Entry point, file I/O handling, and main game loop
│   ├── AVL_Tree.java   # Custom self-balancing BST implementation & search logic
│   ├── Node.java       # Tree node class containing height and subtree metadata
│   └── Card.java       # Entity class representing game units
├── testcase_inputs/    # Large-scale scenarios for stress testing
├── testcase_outputs/   # Expected validation results
└── test_runner.py      # Automated Python script for batch testing
