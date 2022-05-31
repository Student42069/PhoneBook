package phonebook;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Search {
    private final String pathToSorted;
    private String[] names;
    private String[] directory;

    Search(String pathToDirectory, String pathToFind, String pathToSorted) {
        this.pathToSorted = pathToSorted;
        try {
            //All the names are loaded in an array, same for every line of the directory
            //of contacts
            this.names = (new String(Files.readAllBytes(Paths.get(pathToFind)))).split("\\R");
            this.directory = (new String(Files.readAllBytes(Paths.get(pathToDirectory)))).split(
                    "\\R");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public int[] linearSearch() {
        return this.linearSearch(true);
    }
    /*
    / @params doPrint : if the method is called alone by itself the data about the search
    /                   will be printed
    / @return int[] : array of the important data about the search, that is the time it
    /                 took, number of names found and number of name it should have
    /                 found.
     */
    private int[] linearSearch(boolean doPrint) {
        if (doPrint) System.out.println("Start searching (linear search)...");

        long startTime = System.currentTimeMillis();
        int count = 0;

        for (String s : names) {
            for (String g : directory) {
                if (g.split(" ", 2)[1].equals(s)) {
                    count++;
                }
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        if (doPrint) {
            System.out.println("Found " + count + " / " + names.length
                    + " entries. Time taken: "
                    + (elapsedTime / 60000) + " min. "
                    + (elapsedTime / 1000) % 60 + " sec. "
                    + (elapsedTime % 1000) + " ms.\n");
        }

        return new int[]{(int) elapsedTime, count, this.names.length};
    }

    /*
    / A method that will sort and search using bubble sort and jump search, but
    / transition back to linear search if the sorting is taking more than 10 time the
    / time it took linear search alone.
    /
    / @params linearTime : the time it took the linear search to find all the names.
    /
     */
    public void bubbleJump(int linearTime) {
        System.out.println("Start searching (bubble sort + jump search)...");

        int[] bubbleSortValues = bubbleSort(linearTime);
        int broke = bubbleSortValues[1];
        long sortingTime = bubbleSortValues[0];
        int[] searchingValues = jumpSearch(false);

        if(broke==0) {
            System.out.println("Found " + searchingValues[1] + " / " + searchingValues[2]
                    + " entries. Time taken: "
                    + ((sortingTime + searchingValues[0]) / 60000) + " min. "
                    + ((sortingTime + searchingValues[0]) / 1000) % 60 + " sec. "
                    + ((sortingTime + searchingValues[0]) % 1000) + " ms.\nSorting time: "
                    + (sortingTime / 60000) + " min. "
                    + (sortingTime / 1000) % 60 + " sec. "
                    + (sortingTime % 1000) + " ms.\nSearching time: "
                    + (searchingValues[0] / 60000) + " min. "
                    + (searchingValues[0] / 1000) % 60 + " sec. "
                    + (searchingValues[0] % 1000) + " ms.\n");
        } else {
            int[] searchingValues1 = linearSearch(false);
            int sortTime = bubbleSortValues[0];
            int searchTime = searchingValues1[0];
            int total = searchTime + sortTime;
            System.out.println("Found " + searchingValues1[1] + " / " + searchingValues1[2]
                    + " entries. Time taken: "
                    + (total / 60000) + " min. "
                    + (total / 1000) % 60 + " sec. "
                    + (total % 1000) + " ms.\nSorting time: "
                    + (sortTime / 60000) + " min. "
                    + (sortTime / 1000) % 60 + " sec. "
                    + (sortTime % 1000) + " ms. - STOPPED, moved to linear " +
                    "search\nSearching time: "
                    + (searchTime / 60000) + " min. "
                    + (searchTime / 1000) % 60 + " sec. "
                    + (searchTime % 1000) + " ms.\n");
        }
    }

    public int bubbleSort() { return this.bubbleSort(0)[0]; }

    private int[] bubbleSort(int linearTime) {
        boolean doCompare = linearTime > 0;
        // broke is an int and not a boolean because and didn't want to mix int,
        // boolean and long in the return array
        int broke = 0;

        long startTime = System.currentTimeMillis();

        boolean found = true;
        String temp;

        // This is essentially the bubble sort algorithm, with the slight addition of
        // the found variable, if going through the full array no modifications were
        // made, it means it is sorted and the job is done.
        while (found) {
            found = false;
            for (int i = 0; i < this.directory.length - 1; i++) {
                if (this.directory[i].split(" ", 2)[1]
                        .compareTo(this.directory[i + 1].split(" ", 2)[1]) > 0) {
                    temp = this.directory[i + 1];
                    this.directory[i + 1] = this.directory[i];
                    this.directory[i] = temp;
                    found = true;
                }
            }

            if (doCompare && ((System.currentTimeMillis()-startTime) > (10L * linearTime))) {
                broke = 1;
                break;
            }
        }
        if(broke==0) {
            printToFile("BubbleSorted.txt");
        }
        long elapsedTime = System.currentTimeMillis() - startTime;

        // If the method was called by itself and not in a complex method the data about
        // the sort will be printed.
        if(!doCompare) {
            System.out.println("BubbleSort. Time taken: " + (elapsedTime / 60000) + " min. "
                    + (elapsedTime / 1000) % 60 + " sec. " + (elapsedTime % 1000) + " ms.\n");
        }

        return new int[]{ (int) elapsedTime, broke };
    }

    public int[] selectionSort() {
        long startTime = System.currentTimeMillis();

        String temp;
        File sortedDirectory = new File(pathToSorted.substring(0, pathToSorted.indexOf(
                "sorted.txt")) + "MySorted.txt");

        // Selection sort
        try (PrintWriter pw = new PrintWriter(sortedDirectory)) {
            for (int i = 0; i < this.directory.length; i++) {
                for (int j = i+1; j < this.directory.length; j++) {
                    if (this.directory[i].split(" ", 2)[1]
                            .compareTo(this.directory[j]
                                    .split(" ", 2)[1]) > 0) {
                        temp = this.directory[i];
                        this.directory[i] = this.directory[j];
                        this.directory[j] = temp;
                    }
                }
                //System.out.println(directory[i]);
                pw.println(directory[i]);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("MySort. Time taken: " + (elapsedTime/60000) + " min. "
                + (elapsedTime/1000)%60 + " sec. " + (elapsedTime%1000) + " ms.\n");

        return new int[]{(int) elapsedTime};
    }

    public int[] jumpSearch(){ return jumpSearch(true); }

    private int[] jumpSearch(boolean doPrint) {
        int count = 0;
        long startTime = System.currentTimeMillis();

        for (String name : this.names) {
            if (jumpSearch(name)) {
                count++;
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        if(doPrint) {
            System.out.println("JumpSearch. Found " + count + " / " + names.length
                    + " entries. Time taken: " + (elapsedTime / 60000) + " min. "
                    + (elapsedTime / 1000) % 60 + " sec. " + (elapsedTime % 1000) + " ms.\n");
        }

        return new int[]{(int) elapsedTime, count, this.names.length};
    }
    /*
    /   The jump search algorithm
    /   @params name : the name we are searching for.
    /   @return boolean : true if the name was found otherwise false.
     */
    private boolean jumpSearch(String name) {
        int index;
        int i;
        int step = (int) Math.floor(Math.sqrt(directory.length));

        for (i = 0; i < directory.length; i += step) {
            if (directory[i].split(" ", 2)[1].equals(name)) {
                return true;
            } else if (directory[i].split(" ", 2)[1].compareTo(name) > 0) {
                index = i - 1;
                while ((index > (i - step)) && (index >= 1)) {
                    if (directory[index].split(" ", 2)[1].equals(name)) {
                        return true;
                    }
                    index--;
                }
                return false;
            }
        }
        index = directory.length - 1;
        while (index > i - step) {
            if (directory[index].split(" ", 2)[1].equals(name)) {
                return true;
            }
            index--;
        }
        return false;
    }

    public void quickBinary(){
        System.out.println("Start searching (quick sort + binary search)...");

        int sortingTime = quickSort(false);
        int[] searchingValues = binarySearch();

        System.out.println("Found " + searchingValues[1] + " / " + searchingValues[2]
                + " entries. Time taken: "
                + ((sortingTime + searchingValues[0]) / 60000) + " min. "
                + ((sortingTime + searchingValues[0]) / 1000) % 60 + " sec. "
                + ((sortingTime + searchingValues[0]) % 1000) + " ms.\nSorting time: "
                + (sortingTime / 60000) + " min. "
                + (sortingTime / 1000) % 60 + " sec. "
                + (sortingTime % 1000) + " ms.\nSearching time: "
                + (searchingValues[0] / 60000) + " min. "
                + (searchingValues[0] / 1000) % 60 + " sec. "
                + (searchingValues[0] % 1000) + " ms.\n");

    }

    private int[] binarySearch() {
        long startTime = System.currentTimeMillis();
        int count = 0;

        for (String name : names) {
            count += binarySearch(name);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        return new int[]{(int) elapsedTime, count, this.names.length};
    }

    private int binarySearch(String name) {
        int left = 0;
        int right = directory.length;
        int middle;

        while(left <= right){
            middle = (left + right) /2;
            if (directory[middle].split(" ", 2)[1].equals(name)) {
                return 1;
            } else if(directory[middle].split(" ", 2)[1].compareTo(name) > 0) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }
        return 0;
    }

    public int quickSort() { return quickSort(true); }

    private int quickSort(boolean doPrint) {
        long startTime = System.currentTimeMillis();
        quickSort(directory, 0, directory.length - 1 );
        long elapsedTime = System.currentTimeMillis() - startTime;
        printToFile("QuickSorted.txt");

        if(doPrint){System.out.println("QuickSort. Time taken: " + (elapsedTime/60000) + " min. "
                + (elapsedTime/1000)%60 + " sec. " + (elapsedTime%1000) + " ms.\n");}

        return (int) elapsedTime;
    }

    private void quickSort(String[] arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex-1);
            quickSort(arr, partitionIndex+1, end);
        }
    }

    private int partition(String[] arr, int begin, int end) {
        String pivot = arr[end].split(" ", 2)[1];
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (arr[j].split(" ", 2)[1].compareTo(pivot) <= 0) {
                i++;

                String swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        String swapTemp = arr[i+1];
        arr[i+1] = arr[end];
        arr[end] = swapTemp;

        return i+1;
    }
    /*
    /   Method to print the sorted directory of contacts to a file
    /   @params filename : how the file should be named
     */
    private void printToFile(String filename){
        File sortedDirectory = new File(this.pathToSorted.substring(0,
                this.pathToSorted.indexOf(
                        "sorted.txt")) + filename);

        try (PrintWriter pw = new PrintWriter(sortedDirectory)) {
            for (String s : directory) {
                pw.println(s);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void hashTableSearch() {
        System.out.println("Start searching (hash table)...");

        int count = 0;

        long startTime = System.currentTimeMillis();
        HashMap <String, String> contacts = new HashMap<>();
        for(String contact : directory) {
            contacts.put(contact.split(" ", 2)[1], contact.split(" ", 2)[0]);
        }
        long creatingTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for(String name : names){
            if(contacts.containsKey(name)){
                count++;
            }
        }
        long searchingTime = System.currentTimeMillis() - startTime;
        long total = creatingTime + searchingTime;

        System.out.println("Found " + count + " / " + names.length
                + " entries. Time taken: "
                + (total / 60000) + " min. "
                + (total / 1000) % 60 + " sec. "
                + (total % 1000) + " ms.\nCreating time: "
                + (creatingTime / 60000) + " min. "
                + (creatingTime / 1000) % 60 + " sec. "
                + (creatingTime % 1000) + " ms." +
                "\nSearching time: "
                + (searchingTime / 60000) + " min. "
                + (searchingTime / 1000) % 60 + " sec. "
                + (searchingTime % 1000) + " ms.\n");
    }
}