
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TennerGrid {
    private static final int ROWS = 4;
    private static final int COLUMNS = 10;
    private static int[][] grid = new int[ROWS][COLUMNS];
    private static int[] numbers = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    private static int consistency = 0;
    private static int assignments = 0;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        int totalAssignments = 0;
        int totalConsistency = 0;
        int runNum = 0;

        System.out.println("Welcome to the Tenner Grid!\n");

        while (flag) {

            totalAssignments += assignments;
            totalConsistency += consistency;

            assignments = 0;
            consistency = 0;

            System.out.println("How do you want to solve the Tenner Grid?");
            System.out.println("1- Simple Backtracking");
            System.out.println("2- Forward Checking");
            System.out.println("3- Forward Checking with MRV Heuristic");
            System.out.println("4- Exit");
            int choice = scanner.nextInt();

            long startTime = 0;

            switch (choice) {
                case 1:
                    generateTennerGrid();
                    printInitialGrid();
                    startTime = System.nanoTime();
                    if (simpleBacktrack(0, 0)) {
                        printFinalGrid();
                    } else
                        System.out.println("SORRY, unable to solve the grid");
                    break;

                case 2:
                    generateTennerGrid();
                    printInitialGrid();
                    startTime = System.nanoTime();
                    if (forwardChecking(0, 0)) {
                        printFinalGrid();
                    } else
                        System.out.println("SORRY, unable to solve the grid");
                    break;

                case 3:
                    generateTennerGrid();
                    printInitialGrid();
                    startTime = System.nanoTime();
                    if (forwardCheckingMRV(0, 0)) {
                        printFinalGrid();
                    } else
                        System.out.println("SORRY, unable to solve the grid");
                    break;

                case 4:
                    flag = false;
                    break;

            }

            long endTime = System.nanoTime();
            long runningTime = (endTime - startTime) / 1_000_000;

            System.out.println("Time used to solve the problem: " + runningTime + " milliseconds");

            System.out.println("Number of variable assignments: " + assignments);
            System.out.println("Number of consistency checks: " + consistency);
            System.out.println("\n");
            runNum++;
        } // end while
        System.out.println("Assignments Median: " + (totalAssignments / runNum));

    }// end main

    //////////////////////////////////////////////

    // we will generate the grid
    private static void generateTennerGrid() {

        int row = 0; // counter
        shuffle(numbers); // Change the order of numbers

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                grid[i][j] = numbers[j];
            }
            shuffle(numbers); // after assigning to the row 'i' we will shuffle again
            row++;
            boolean notValid = InitialStateValidty(row);
            while (notValid) {
                // if it's not valid shuffle until it's valid
                shuffle(numbers);
                notValid = InitialStateValidty(row);
            }
        }

        generateSum(); // method to generate the sum

        randomEmptyCells();// method to choose a randomly empty cells

    }

    //////////////////////////////////////////////

    private static void shuffle(int[] numbers) {
        // Change the order of numbers
        Random random = new Random();
        for (int i = numbers.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = numbers[i];
            numbers[i] = numbers[index];
            numbers[index] = temp;
        }
    }

    //////////////////////////////////////////////

    private static void randomEmptyCells() {
        Random random = new Random();
        for (int i = 0; i < ROWS - 1; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (random.nextDouble() < 0.5) {
                    grid[i][j] = -1;
                }
            }
        }
    }

    //////////////////////////////////////////////

    private static void generateSum() {
        // We add the numbers in the columns together until they give us the total
        for (int j = 0; j < COLUMNS; j++) {
            int sum = 0;
            for (int i = 0; i < ROWS - 1; i++) {
                sum += grid[i][j];
            }
            grid[ROWS - 1][j] = sum;
        }

    }

    //////////////////////////////////////////////

    private static boolean InitialStateValidty(int row) {
        // this method will check the validty of the initial state when we assign random
        // numbers to each row

        for (int i = 0; i < COLUMNS; i++) {
            if (grid[row - 1][i] == numbers[i]) {
                return true;
            }
            if (i > 0 && grid[row - 1][i - 1] == numbers[i]) {
                return true;
            }
            if (i < 9 && grid[row - 1][i + 1] == numbers[i]) {
                return true;
            }
        }

        return false;
    }

    //////////////////////////////////////////////

    private static boolean isTheNumberInConnectingCells(int row, int col, int num) {
        // when we assigning numbers to the empty cells we need to check if it's valid

        if (row > 0 && grid[row - 1][col] == num) {
            consistency++;
            return true;
        }
        if (row < ROWS - 1 && grid[row + 1][col] == num) {
            consistency++;
            return true;
        }
        if (col > 0 && grid[row][col - 1] == num) {
            consistency++;
            return true;
        }
        if (col < COLUMNS - 1 && grid[row][col + 1] == num) {
            consistency++;
            return true;
        }

        if (row < ROWS - 1 && col < COLUMNS - 1 && grid[row + 1][col + 1] == num) {
            consistency++;
            return true;
        }

        if (row > 0 && col > 0 && grid[row - 1][col - 1] == num) {
            consistency++;
            return true;
        }

        if (row < ROWS - 1 && col > 0 && grid[row + 1][col - 1] == num) {
            consistency++;
            return true;
        }

        if (row > 0 && col < COLUMNS - 1 && grid[row - 1][col + 1] == num) {
            consistency++;
            return true;
        }

        return false;

    }

    //////////////////////////////////////////////

    private static boolean checkSum(int row, int col, int num) {

        // here will check if we exceed the sum
        int sum = 0;
        for (int i = 0; i < ROWS - 1; i++) {
            if (grid[i][col] == -1)
                continue;
            else
                sum += grid[i][col];

        }

        sum += num;
        consistency++;
        if (sum > grid[ROWS - 1][col])
            return false;

        // If we are in the penultimate row we need to check that when we add up the
        // numbers in the column it will be equal to the sum
        if (row == ROWS - 2 && sum != grid[ROWS - 1][col])
            return false;

        return true;

    }

    //////////////////////////////////////////////

    private static boolean isTheNumberInTheRow(int row, int num) {
        // this method will check if the num is already exist in the row
        for (int x = 0; x < COLUMNS; x++) {
            consistency++;
            if (grid[row][x] == num) {
                return true;
            }
        }
        return false;
    }

    //////////////////////////////////////////////

    private static boolean isValid(int row, int col, int num) {
        if (isTheNumberInConnectingCells(row, col, num))
            return false;
        else if (isTheNumberInTheRow(row, num))
            return false;
        else if (!checkSum(row, col, num))
            return false;
        else
            return true;
    }

    //////////////////////////////////////////////

    private static void printInitialGrid() {
        System.out.println("\n --------Initial State---------");
        // Print the grid
        for (int i = 0; i < ROWS; i++) {
            System.out.print("|");
            for (int j = 0; j < COLUMNS; j++) {
                // System.out.printf("%2d ", grid[i][j]);
                // Print empty cells as "-"
                if (grid[i][j] == -1) {
                    System.out.printf("%2s ", "-"); // Adjust spacing as needed
                } else {
                    System.out.printf("%2d ", grid[i][j]); // Adjust spacing as needed
                }
            }
            System.out.println("|");
        }
        System.out.println(" ------------------------------");
    }

    //////////////////////////////////////////////

    private static void printFinalGrid() {
        System.out.println("\n ---------Final State---------");
        for (int i = 0; i < ROWS; i++) {
            System.out.print("|");
            for (int j = 0; j < COLUMNS; j++) {
                System.out.printf("%2d ", grid[i][j]);
            }
            System.out.println("|");
        }
        System.out.println(" ------------------------------\n");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean simpleBacktrack(int row, int col) {

        // if we are in the end of the grid
        if (row == ROWS - 1 && col == COLUMNS) {
            return true;
        }

        // if we are in the last column we should move to the next row and start with
        // column number 0
        if (col == COLUMNS) {
            row++;
            col = 0;
        }

        // check if the current cell is empty or not
        if (grid[row][col] != -1) {
            return simpleBacktrack(row, col + 1);
        }

        for (int num = 0; num < 10; num++) {
            assignments++;
            if (isValid(row, col, num)) {// check if number is valid or not
                grid[row][col] = num;
                if (simpleBacktrack(row, col + 1)) {
                    return true;
                }
            }
            grid[row][col] = -1;
            assignments++;
        }

        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean forwardChecking(int row, int col) {
        if (row == ROWS - 1 && col == COLUMNS) {
            return true;
        }
        if (col == COLUMNS) {
            row++;
            col = 0;
        }
        if (grid[row][col] != -1) {
            return forwardChecking(row, col + 1);
        }
        for (int num = 0; num < 10; num++) {
            if (isValid(row, col, num)) {
                grid[row][col] = num;
                assignments++;
                if (checkForward(row, col) && forwardChecking(row, col + 1)) {
                    return true;
                }
                grid[row][col] = -1;
                assignments++;
            }
        }
        return false;
    }

    //////////////////////////////////////////////

    private static boolean checkForward(int row, int col) {
        for (int i = row - 1; i >= 0; i--) {
            if (grid[i][col] == -1) {
                return false;
            }
        }
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean forwardCheckingMRV(int row, int col) {
        if (row == ROWS - 1 && col == COLUMNS) {
            return true;
        }
        if (col == COLUMNS) {
            row++;
            col = 0;
        }
        if (grid[row][col] != -1) {
            return forwardCheckingMRV(row, col + 1);
        }
        List<Integer> possibleValues = getPossibleValues(row, col);
        final int currentRow = row;
        final int currentCol = col;
        possibleValues.sort(Comparator.comparingInt(value -> countRemainingValues(currentRow, currentCol, value)));
        for (int num : possibleValues) {
            grid[row][col] = num;
            assignments++;
            if (checkForward(row, col) && forwardCheckingMRV(row, col + 1)) {
                return true;
            }
            grid[row][col] = -1;
            assignments++;
        }
        return false;
    }

    //////////////////////////////////////////////

    private static List<Integer> getPossibleValues(int row, int col) {
        List<Integer> possibleValues = new ArrayList<>();
        for (int num = 0; num < 10; num++) {
            if (isValid(row, col, num)) {
                possibleValues.add(num);
            }
        }
        return possibleValues;
    }

    //////////////////////////////////////////////

    private static int countRemainingValues(int row, int col, int value) {
        int count = 0;
        for (int r = row; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (grid[r][c] == -1 && isValid(r, c, value)) {
                    count++;
                }
            }
        }
        return count;
    }

}
