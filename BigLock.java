/**
 * A simple subclass of Item that is a key. Used
 * to display to user the inventory screen.
 */
public class BigLock extends Item
{
    private char symbol;
    private String type;
    private String name;
    String category;

    public BigLock() {
        type = "Lock";
        category = "Item";
        symbol = '$';
        name = "Big Locked Chest";
    }

    public String getCategory() {
        return category;
    }
    
    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public boolean unlock() {
        return true;
    }
    
     public char getSymbol() {
        return symbol;
    }
}
