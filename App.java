import java.io.*;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static String fileName = "default.txt";

    public static void main(String[] args) {
        int choice;
        do {
            displayMenu();
            choice = getChoice();
            executeChoice(choice);
        } while (choice != 0);
        
        scanner.close();
        System.out.println("Program terminated.");
    }

    private static void displayMenu() {
        System.out.println("""
???? Text File Editor Menu ????
1. Create a new file
2. Select an existing file
3. Read file contents
4. Write to file (append)
5. Clear file contents
6. Display a range of lines
7. Insert text at specific line
0. Exit
""");
        System.out.println("Current file: " + fileName);
        System.out.print("Enter your choice: ");
    }

    private static int getChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a number!");
            scanner.next();
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); 
        return choice;
    }

    private static void executeChoice(int choice) {
        switch (choice) {
            case 1:
                createNewFile();
                break;
            case 2:
                selectExistingFile();
                break;
            case 3:
                readFileContents();
                break;
            case 4:
                writeToFile();
                break;
            case 5:
                clearFileContents();
                break;
            case 6:
                displayLineRange();
                break;
            case 7:
                insertAtLine();
                break;
            case 0:
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void createNewFile() {
        System.out.print("Enter new file name: ");
        String newFileName = scanner.nextLine();
        
        try {
            File file = new File(newFileName);
            if (file.createNewFile()) {
                System.out.println("File created successfully: " + file.getName());
                fileName = newFileName;
            } else {
                System.out.println("File already exists. Use option 2 to select it.");
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    private static void selectExistingFile() {
        System.out.print("Enter file name: ");
        String selectedFileName = scanner.nextLine();
        
        File file = new File(selectedFileName);
        if (file.exists()) {
            fileName = selectedFileName;
            System.out.println("File selected: " + fileName);
        } else {
            System.out.println("File not found. Use option 1 to create it.");
        }
    }

    private static void readFileContents() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File does not exist. Use option 1 to create it.");
            return;
        }
        
        if (file.length() == 0) {
            System.out.println("File is empty.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("\n--- File Contents ---");
            String line;
            int lineNumber = 1;
            
            while ((line = reader.readLine()) != null) {
                System.out.printf("%3d | %s\n", lineNumber, line);
                lineNumber++;
            }
            
            System.out.println("--- End of File ---");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static void writeToFile() {
        System.out.println("Enter text to append (empty line to finish):");
        
        String[] linesToAdd = getMultipleLines();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            for (int i = 0; i < linesToAdd.length; i++) {
                if (linesToAdd[i] != null) {
                    writer.write(linesToAdd[i]);
                    writer.newLine();
                }
            }
            System.out.println("Text added to file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static String[] getMultipleLines() {
        String[] lines = new String[100]; 
        int count = 0;
        
        String line;
        while (true) {
            line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            
            if (count < lines.length) {
                lines[count++] = line;
            } else {
                System.out.println("Maximum number of lines reached.");
                break;
            }
        }
        
        return lines;
    }

    private static void clearFileContents() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            // Opening the file with false flag will clear it
            System.out.println("File cleared successfully.");
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }

    private static void displayLineRange() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File does not exist. Use option 1 to create it.");
            return;
        }
        
        if (file.length() == 0) {
            System.out.println("File is empty.");
            return;
        }
        
        System.out.print("Enter starting line number: ");
        int startLine = getPositiveInteger();
        
        System.out.print("Enter ending line number: ");
        int endLine = getPositiveInteger();
        
        if (startLine > endLine) {
            System.out.println("Invalid range. Start line must be less than or equal to end line.");
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("\n--- File Contents (Lines " + startLine + " to " + endLine + ") ---");
            String line;
            int lineNumber = 1;
            
            // Skip lines before the start line
            while (lineNumber < startLine && (line = reader.readLine()) != null) {
                lineNumber++;
            }
            
            // Read and display lines in the range
            while (lineNumber <= endLine && (line = reader.readLine()) != null) {
                System.out.printf("%3d | %s\n", lineNumber, line);
                lineNumber++;
            }
            
            if (lineNumber <= startLine) {
                System.out.println("Line range exceeds file length.");
            }
            
            System.out.println("--- End of Range ---");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static int getPositiveInteger() {
        int num;
        while (true) {
            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid number!");
                scanner.next();
            }
            num = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            if (num > 0) {
                return num;
            } else {
                System.out.println("Please enter a positive number!");
            }
        }
    }

    private static void insertAtLine() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File does not exist. Use option 1 to create it.");
            return;
        }
        
        System.out.print("Enter line number to insert at: ");
        int targetLine = getPositiveInteger();
        
        System.out.println("Enter text to insert (empty line to finish):");
        String[] linesToInsert = getMultipleLines();
        
        // Read all contents of the file
        String[] fileContents = readAllLines();
        int totalLines = countLinesInArray(fileContents);
        
        if (targetLine > totalLines + 1) {
            System.out.println("Line number exceeds file length. The file has " + totalLines + " lines.");
            return;
        }
        
        // Prepare new content with inserted lines
        String[] newContents = new String[fileContents.length + 100]; // Extra space for inserted lines
        int newIndex = 0;
        
        // Copy lines before the target line
        for (int i = 0; i < targetLine - 1 && i < totalLines; i++) {
            newContents[newIndex++] = fileContents[i];
        }
        
        // Insert the new lines
        for (int i = 0; i < linesToInsert.length && linesToInsert[i] != null; i++) {
            newContents[newIndex++] = linesToInsert[i];
        }
        
        // Copy remaining lines
        for (int i = targetLine - 1; i < totalLines; i++) {
            newContents[newIndex++] = fileContents[i];
        }
        
        // Write back to file
        writeAllLines(newContents);
        
        System.out.println("Text inserted successfully at line " + targetLine + ".");
    }

    private static String[] readAllLines() {
        String[] lines = new String[1000]; // Arbitrary limit
        int lineCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null && lineCount < lines.length) {
                lines[lineCount++] = line;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        
        return lines;
    }

    private static int countLinesInArray(String[] lines) {
        int count = 0;
        while (count < lines.length && lines[count] != null) {
            count++;
        }
        return count;
    }

    private static void writeAllLines(String[] lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (int i = 0; i < lines.length && lines[i] != null; i++) {
                writer.write(lines[i]);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}