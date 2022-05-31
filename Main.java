package phonebook;

public class Main {
    public static void main(String[] args) {
        String pathToDirectory = "C:\\Users\\Magic\\Downloads\\directory.txt";
        String pathToFind = "C:\\Users\\Magic\\Downloads\\find.txt";
        String pathToSorted = "C:\\Users\\Magic\\Downloads\\sorted.txt";

        Search linear = new Search(pathToDirectory, pathToFind, pathToSorted);
        int[] linearValues = linear.linearSearch();

        Search bubbleJump = new Search(pathToDirectory, pathToFind, pathToSorted);
        bubbleJump.bubbleJump(linearValues[0]);

        Search quickBinary = new Search(pathToDirectory, pathToFind, pathToSorted);
        quickBinary.quickBinary();

        Search hashTable = new Search(pathToDirectory, pathToFind, pathToSorted);
        hashTable.hashTableSearch();
    }
}