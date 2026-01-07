import java.util.ArrayList;
public class Card {
    public String name;
    public int initialAttack;
    public int initialHealth;
    public int baseAttack;
    public int baseHealth;
    public int currentAttack;
    public int currentHealth;
    public int missingHealth;
    public int insertionOrder;

    Card(String name, int initialAttack, int initialHealth, int insertionOrder){
        this.name = name;
        this.initialAttack = initialAttack;
        this.initialHealth = initialHealth;
        this.insertionOrder = insertionOrder;

        this.baseAttack = initialAttack;
        this.baseHealth = initialHealth;

        this.missingHealth = 0;

        this.currentAttack = initialAttack;
        this.currentHealth = initialHealth;
    }


}
