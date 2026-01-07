
/**
 * CMPE 250 Project 1 - Nightpass Survivor Card Game
 * 
 * This skeleton provides file I/O infrastructure. Implement your game logic
 * as you wish. There are some import that is suggested to use written below. 
 * You can use them freely and create as manys classes as you want. However, 
 * you cannot import any other java.util packages with data structures, you
 * need to implement them yourself. For other imports, ask through Moodle before 
 * using.
 * 
 * TESTING YOUR SOLUTION:
 * ======================
 * 
 * Use the Python test runner for automated testing:
 * 
 * python test_runner.py              # Test all cases
 * python test_runner.py --type type1 # Test only type1  
 * python test_runner.py --type type2 # Test only type2
 * python test_runner.py --verbose    # Show detailed diffs
 * python test_runner.py --benchmark  # Performance testing (no comparison)
 * 
 * Flags can be combined, e.g.:
 * python test_runner.py -bv              # benchmark + verbose
 * python test_runner.py -bv --type type1 # benchmark + verbose + type1
 * python test_runner.py -b --type type2  # benchmark + type2
 * 
 * MANUAL TESTING (For Individual Runs):
 * ======================================
 * 
 * 1. Compile: cd src/ && javac *.java
 * 2. Run: java Main ../testcase_inputs/test.txt ../output/test.txt
 * 3. Compare output with expected results
 * 
 * PROJECT STRUCTURE:
 * ==================
 * 
 * project_root/
 * ├── src/                     # Your Java files (Main.java, etc.)
 * ├── testcase_inputs/         # Input test files  
 * ├── testcase_outputs/        # Expected output files
 * ├── output/                  # Generated outputs (auto-created)
 * └── test_runner.py           # Automated test runner
 * 
 * REQUIREMENTS:
 * =============
 * - Java SDK 8+ (javac, java commands)
 * - Python 3.6+ (for test runner)
 * 
 * @author Alaz Kilic
 */

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.math.*;

public class Main {
    public static void main(String[] args) {
        // Check command line arguments
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            System.out.println("Example: java Main ../testcase_inputs/test.txt ../output/test.txt");
            return;
        }

        String inFile = args[0];
        String outFile = args[1];

        // Initialize file reader (OPTIMIZED: Using BufferedReader)
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(inFile));
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + inFile);
            e.printStackTrace();
            return;
        }

        // Initialize file writer (OPTIMIZED: Using BufferedWriter)
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException e) {
            System.out.println("Writing error: " + outFile);
            e.printStackTrace();
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            return;
        }

        // I have created two trees, one for each sort mode, because ı want O(log(n)) complexity.
        //in my previous method the tree wasonly useful for Priority one and three.
        //now ı have one tree for P1 AND P3 and another tree for P2 AND P4.
        AVL_Tree deckP1 = new AVL_Tree(1); // Mode 1 for P1/P3
        AVL_Tree deckP2 = new AVL_Tree(2); // Mode 2 for P2/P4
        AVL_Tree discardPileTree = new AVL_Tree(3);


        int insertionCounter = 0;

        int ourPoints = 0;
        int strangerPoints=0;

        // Process commands line by line
        try {
            // (OPTIMIZED: Using BufferedReader.readLine() and String.split())
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" "); // Parse line without new Scanner
                String command = parts[0];
                String out = "";

                switch (command) {
                    case "draw_card": {
                        // (OPTIMIZED: Reading from String array)
                        String name = parts[1];
                        int att = Integer.parseInt(parts[2]);
                        int hp = Integer.parseInt(parts[3]);

                        Card card = new Card(name, att, hp, insertionCounter++);

                        //Node cardNode = new Node(card);
                        deckP1.insertion(card);
                        deckP2.insertion(card);

                        out = "Added " + name + " to the deck"; // suggested method for draw_card command
                        break;
                    }
                    case "battle": {
                        // 1. Read Inputs (OPTIMIZED: Reading from String array)
                        int att = Integer.parseInt(parts[1]);
                        int hp = Integer.parseInt(parts[2]);
                        int heal = Integer.parseInt(parts[3]);

                        // 2. Initialize variables
                        int revivalCount = 0;
                        int remainingHeal = heal;
                        String battleResultOutput = "";
                        Card playedCard = null;
                        int priority = 0;

                        // ---------------------------
                        // PHASE 1: BATTLE PHASE
                        // ---------------------------

                        //all priorities are being checked in order

                        // Priority 1
                        playedCard = deckP1.deckSearcher(att, hp, 0);
                        if (playedCard != null) {
                            priority = 1;
                        }

                        // Priority 2
                        if (playedCard == null) {
                            playedCard = deckP2.deckSearcher(att, hp, 2);
                            if (playedCard != null) {
                                priority = 2;
                            }
                        }

                        // Priority 3
                        if (playedCard == null) {
                            playedCard = deckP1.deckSearcher(att, hp, 1);
                            if (playedCard != null) {
                                priority = 3;
                            }
                        }

                        // Priority 4
                        if (playedCard == null) {
                            playedCard = deckP2.deckSearcher(att, hp, 3);
                            if (playedCard != null) {
                                priority = 4;
                            }
                        }

                        // ---------------------------
                        // PHASE 1.B: BATTLE RESOLUTION
                        // ---------------------------

                        if (playedCard == null) {
                            strangerPoints += 2;
                            battleResultOutput = "No card to play";
                        } else {
                            String cardName = playedCard.name;
                            int originalSurvivorHealth = playedCard.currentHealth;
                            int originalSurvivorAttack = playedCard.currentAttack;

                            // Remove from both trees
                            deckP1.deletion(playedCard);
                            deckP2.deletion(playedCard);

                            int newHealth = originalSurvivorHealth - att;
                            boolean cardDies = newHealth <= 0;

                            // Scoring
                            int survivorScoreThisTurn = 0;
                            int strangerScoreThisTurn = 0;

                            if (hp - originalSurvivorAttack <= 0) {
                                survivorScoreThisTurn = 2;
                            } else if (originalSurvivorAttack > 0) {
                                survivorScoreThisTurn = 1;
                            }

                            if (cardDies) {
                                strangerScoreThisTurn = 2;
                            } else if (att > 0) {
                                strangerScoreThisTurn = 1;
                            }

                            ourPoints += survivorScoreThisTurn;
                            strangerPoints += strangerScoreThisTurn;

                            if (cardDies) {
                                // Move to discard pile
                                playedCard.missingHealth = playedCard.baseHealth;
                                playedCard.insertionOrder = insertionCounter++;
                                discardPileTree.insertion(playedCard);
                                battleResultOutput = "Found with priority " + priority + ", Survivor plays " + cardName +
                                        ", the played card is discarded";
                            } else {
                                // Card survives
                                playedCard.currentHealth = newHealth;
                                double newAttackDouble = ((double) playedCard.baseAttack * playedCard.currentHealth) / playedCard.baseHealth;
                                playedCard.currentAttack = Math.max(1, (int) Math.floor(newAttackDouble));
                                playedCard.insertionOrder = insertionCounter++;
                                deckP1.insertion(playedCard);
                                deckP2.insertion(playedCard);
                                battleResultOutput = "Found with priority " + priority + ", Survivor plays " + cardName +
                                        ", the played card returned to deck";
                            }
                        }

                        // ---------------------------
                        // PHASE 2: HEALING PHASE
                        // ---------------------------

                        // Full revive loop (Largest H_missing first)
                        // This loop repeatedly finds the most expensive card that can be fully revived with the remaining heal points.
                        while (remainingHeal > 0 && discardPileTree.getNodeCount() > 0) {
                            Card cardToRevive = discardPileTree.findCardForFullRevive(remainingHeal);

                            // If no card can be fully revived, this returns null and the loop breaks.
                            if (cardToRevive == null) {
                                break;
                            }

                            // A card was found. Process the full revival.
                            revivalCount++;
                            remainingHeal -= cardToRevive.missingHealth;
                            discardPileTree.deletion(cardToRevive); // Remove from discard

                            // Apply full revive penalty (10%) and restore stats
                            cardToRevive.baseAttack = (int) Math.floor(cardToRevive.baseAttack * 0.90);
                            cardToRevive.currentHealth = cardToRevive.baseHealth;
                            cardToRevive.currentAttack = Math.max(1, cardToRevive.baseAttack);
                            cardToRevive.missingHealth = 0;
                            cardToRevive.insertionOrder = insertionCounter++;

                            // Add back to the active decks
                            deckP1.insertion(cardToRevive);
                            deckP2.insertion(cardToRevive);
                        }

                        // Partial Revive (Smallest H_missing)
                        // This block only runs if the loop above finished AND heal points remain.
                        // By definition, no card can be fully revived, so we must do a partial revive.
                        if (remainingHeal > 0 && discardPileTree.getNodeCount() > 0) {
                            // Get the card with the smallest missingHealth
                            Card cardToRevive = discardPileTree.getMinCard();

                            if (cardToRevive != null) {
                                // Remove from discard to update it
                                discardPileTree.deletion(cardToRevive);

                                // NOTE: We do not check if (remainingHeal >= cardToRevive.missingHealth)
                                // because that would be a full revive, which P1/P2 already failed to find.
                                // This block only handles partial revives.

                                // Apply partial revive penalty (5%)
                                cardToRevive.baseAttack = (int) Math.floor(cardToRevive.baseAttack * 0.95);

                                // Apply the remaining healing and update missingHealth
                                // The card remains in the discard pile
                                cardToRevive.missingHealth -= remainingHeal;
                                cardToRevive.insertionOrder = insertionCounter++;

                                // Add the partially revived card back to the discard pile
                                discardPileTree.insertion(cardToRevive);

                                // All remaining points are spent in this phase
                                remainingHeal = 0;
                            }
                        }

                        // ---------------------------
                        // 4. FINAL OUTPUT
                        // ---------------------------
                        out = battleResultOutput + ", " + revivalCount + " cards revived";

                        break;
                    }

                    case "find_winning": {
                        if (ourPoints >= strangerPoints) {
                            out = "The Survivor, Score: " + ourPoints;
                        } else {
                            out = "The Stranger, Score: " + strangerPoints;
                        } // suggested method for find_winning command
                        break;
                    }
                    case "deck_count": {
                        out = "Number of cards in the deck: " + deckP2.getDeckCount();//doesnt matter P1 OR P2.
                        break;
                    }


                    case "discard_pile_count": {
                        out = "Number of cards in the discard pile: " + discardPileTree.getDeckCount();
                        break;
                    }

                    case "steal_card": {
                        // (OPTIMIZED: Reading from String array)
                        int att = Integer.parseInt(parts[1]);
                        int hp = Integer.parseInt(parts[2]);

                        // Call the method in AVL_Tree
                        Card stolenCard = deckP1.findStealCard(att, hp);

                        if (stolenCard != null) {

                            // If found, we must also delete it from deckP2
                            deckP1.deletion(stolenCard);
                            deckP2.deletion(stolenCard);

                            out = "The Stranger stole the card: " + stolenCard.name;
                        } else {
                            out = "No card to steal";
                        } // suggested method for steal_card command
                        break;
                    }
                    default: {
                        System.out.println("Invalid command: " + command);
                        // (OPTIMIZED: No inner scanner to close)
                        writer.close();
                        reader.close();
                        return;
                    }
                }

                // (OPTIMIZED: No inner scanner to close)

                try {
                    writer.write(out);
                    writer.write("\n"); // uncomment if each output needs to be in a new line and
                    // you did not implement that inside the functions.
                } catch (IOException e2) {
                    System.out.println("Writing error");
                    e2.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("Error processing commands: " + e.getMessage());
            e.printStackTrace();
        }

        // Clean up resources
        try {
            writer.close(); // This will also flush the BufferedWriter
        } catch (IOException e2) {
            System.out.println("Writing error");
            e2.printStackTrace();
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("end");
        return;
    }
}
