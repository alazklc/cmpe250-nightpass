# Nightpass: Survival Card Game Simulation 🌑🎴

> **CMPE250 - Data Structures and Algorithms Project**
>
> *A high-performance Java simulation of a strategic card battle game, built with custom data structures (AVL Trees) under strict algorithmic constraints.*

## 📖 About the Project
This project simulates **"Nightpass"**, a survival card game where the player ("The Survivor") duels against an AI opponent known as "The Stranger". The goal is to manage a deck of cards, survive nightly attacks using complex priority rules, and optimize resource management to outlast the opponent.

The simulation is designed to handle **large-scale test cases** (500k+ commands) efficiently, strictly forbidding standard Java Collections (except `ArrayList`) to enforce custom data structure implementation.

## 🚀 Technical Highlights & Algorithms
To achieve **$O(\log N)$** time complexity for card operations and meet strict time limits, this project implements:

* **Custom AVL Tree Implementation:** A self-balancing Binary Search Tree (BST) was built from scratch to handle insertions, deletions, and complex queries efficiently.
* **Dual-Tree Strategy:** To satisfy conflicting battle priority rules (e.g., finding the *minimum* attack card vs. the *maximum* attack card), the simulation maintains two synchronized AVL trees simultaneously:
    * **`deckP1`:** Sorted for Min-Attack priority logic.
    * **`deckP2`:** Sorted for Max-Attack priority logic.
* **Optimized I/O:** Uses `BufferedReader` and `BufferedWriter` to process massive input/output files within milliseconds.
* **Complex Game Logic:** Simulates battle phases, dynamic stat scaling (damage calculation), and a "Healing Phase" where dead cards are revived from the discard pile with specific penalties.

## 📂 Project Structure
```text
cmpe250-nightpass/
├── src/
│   ├── Main.java        # Entry point, file I/O, and game loop
│   ├── AVL_Tree.java    # Custom AVL Tree implementation & search logic
│   ├── Node.java        # Tree nodes holding subtree metadata
│   └── Card.java        # Card entity class
├── testcase_inputs/     # Input scenarios for testing
├── testcase_outputs/    # Expected results
└── test_runner.py       # Automated Python test script
