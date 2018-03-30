import java.util.ArrayList;
import java.util.List;

/**
 * This AlignText program is used to take a source text file, wrap the words in it to lines
 * specified by a maximum character length, and then align the text to a specified alignment.
 * 
 * The output is the final formatted text.
 * 
 * @author pmh20 (Haubrick PM)
 * @version 1.0
 * @since 30/09/2017
 */
public class AlignText {

    /**
     * The main method carries out the main flow of the program. It changes the setup based on the arguments it is given, and
     * calls the methods, which carry out the detailed work.
     * 
     * @see FileUtil#readFile(String)
     * @param args - This is an array of strings. This must either take 2 parameters (for the 'Basic' assignment),
     *               or 3 parameters (for the 'Enhancements' of the assignment).
     *    -args[0] - This parameter must be the exact path to the text file desired to be formatted.
     *    -args[1] - This parameter must be an integer that represents the max desired character length per line.
     *    -args[2] - This parameter is optional, and takes the form of a single capital letter to specify the desired
     *               alignment type. Use 'L' for left, 'R' for right, 'C' for centre, and 'J' for justify.
     */
    public static void main(String[] args) {

        if (args.length == 0) { // This ensures that the program is not run without any arguments.
            printError(1);
        }

        String fileName = args[0]; // This assigns the supplied file path to a String variable.
        int lineWrapNumber = 0;
        ArrayList<Integer> lastLineTracker = new ArrayList<Integer>(); // This ArrayList stores a list of integers, each representing
                                                                       // the index of the last line of each paragraph (for justifying).

        if (((args.length != 2) && (args.length != (2 + 1))) || !isValidInt(args[1]) || Integer.parseInt(args[1]) < 1) {
            printError(1); // This prints the error method if:   (a) Invalid number of arguments are supplied
                           //                                        (the "2" is for 'Basics', and "+1" signifying 'Enhancements')
                           //                                    (b) User did not supply a valid integer for the line length.

        } else if (args.length == 2) { // This block of code is the flow for the 'Basic' assignment.
            String[] paragraphs = FileUtil.readFile(fileName); // Returns the source text in the form of an array of paragraph Strings.
                                                               // This method also handles the exception of an incorrect file path.
            lineWrapNumber = Integer.parseInt(args[1]);
            List<String> ouputLines = wrapText(lineWrapNumber, paragraphs, lastLineTracker); // This method wraps the text to line length.
            rightAlign(ouputLines, lineWrapNumber); // This method aligns text to the right.

        } else if (args.length == (2 + 1)) { // This block of code is the flow for the assignment's 'Enhancements'.
            String[] paragraphs = FileUtil.readFile(fileName);
            lineWrapNumber = Integer.parseInt(args[1]);
            String alignment = args[2];
            List<String> ouputLines = wrapText(lineWrapNumber, paragraphs, lastLineTracker);

            if (alignment.equals("R")) {     ///////////////////////////////////////////////////////////////////////////////////////
                rightAlign(ouputLines, lineWrapNumber);                         //                                                //
            } else if (alignment.equals("L")) {                                 // This segment of code checks which              //
                leftAlign(ouputLines);                                          // type of alignment the user specifies,          //
            } else if (alignment.equals("C")) {                                 // and invokes the appropriate method.            //
                centreAlign(ouputLines, (float) lineWrapNumber);                //                                                //
            } else if (alignment.equals("J")) {                                 // If the specified alignment token is not        //
                justifyAlign(ouputLines, lastLineTracker, lineWrapNumber);      // valid (must be R, L, C, or J), the             //
            } else {                                                            // 'Enhancement'error is printed in the console.  //
                printError(2);                                                  //                                                //
            }     //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    /**
     * This method is used to handle the exception if anything but a valid integer is given as a parameter.
     * 
     * @param lineLength - This is the args[1] given to main, and should be the line length.
     * @return a boolean, stating whether the supplied parameter is an integer or not.
     */
    public static boolean isValidInt(String lineLength) {
        try {
            Integer.parseInt(lineLength);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * This method is invoked whenever certain errors occur, and prints the respective error message to the screen.
     * 
     * @param errorType - This is to distinguish between the error messages that the assignment spec requires.
     */
    public static void printError(int errorType) {
        if (errorType == 1) {
            System.out.println("usage: java AlignText file_name line_length");
        } else if (errorType == 2) {
            System.out.println("usage: java AlignText file_name line_length <align_mode>");
        }
    }

    /**
     * This method does the most important job of the program, which is to split the text up into the correct number of words for
     * a line (determined by the specified line length of characters) for ALL CASE SCENARIOS. Some parts may look
     * over-complicated, but have been implemented as safety features that allow the code to scale to (for example): (a) When the
     * max character line length is smaller that the first word. (b) When the max character line length is larger than the
     * paragraph length.
     * 
     * @param wrappingNumber - This is the line length (in characters) that the line must not exceed (bar special cases, such as (a) above.
     * @param oldArray - This is the array that contains the entire text, each paragraph stored in its own String element.
     * @param lastLineTracker - This is an ArrayList that stores index values which indicate where the last line of each paragraph
     *                          is stored in the new ArrayList that is created to store the output line Strings.
     * @return the reference to the ArrayList created for storing Strings of the output lines.
     */
    public static List<String> wrapText(int wrappingNumber, String[] oldArray, List<Integer> lastLineTracker) {
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < oldArray.length; i++) {                                                 //This outside loop iterates through each paragraph.
            int index = 0;                                         //This is the leading index, used for locating the correct end-point of a given line.
            int index2 = 0;                                                    //This is the lagging index, used for 'saving' where the last line ended.
            while (index < oldArray[i].length()) {                                 //This loop iterates through each character in the current paragraph.
                while (oldArray[i].charAt(index2) == ' ') {      //This loop simply brings the lagging index to the next character (in case on a space).
                    index2++;
                }
                //This next "If" checks if the difference between the leading an lagging index has reached the max line character length.
                if (index - index2 == wrappingNumber && index2 < oldArray[i].lastIndexOf(' ', oldArray[i].length())) {

                    //The next "If" checks if there's a space between the two indices, as there are different steps when a word is longer than the line limit.
                    if (oldArray[i].substring(index2, index).contains(" ")) {

                        //The next line pulls a substring from the LAST space, trims it's front/end white-spaces and adds it in the new ArrayList.
                        strings.add(oldArray[i].substring(index2, oldArray[i].lastIndexOf(' ', index)).trim());
                        //The next line is to update the lagging index so the next line starts from the right place.
                        index2 = oldArray[i].lastIndexOf(' ', index);
                        //The next line makes the indices equal, so the leading index starts counting from the correct position.
                        index = index2;
                    } else {    //This is when a word is longer than the line limit.

                        //The next line puts the correct substring into the ArrayList, this time at the NEXT space (or end of the paragraph, if that's sooner).
                        strings.add(oldArray[i].substring(index2, Math.min(oldArray[i].indexOf(' ', index), oldArray[i].length())).trim());
                        index2 = Math.min(oldArray[i].indexOf(' ', index), oldArray[i].length());   //Updates lagging index.
                    }
                } else if (index + 1 == oldArray[i].length()) {     //This checks if the last character of the paragraph has been reached.

                    //The next line adds the remaining words in the paragraph as a new String line in the ArrayList.
                    strings.add(oldArray[i].substring(index2, oldArray[i].length()).trim());
                    break;  //This breaks the current while loop, allowing the next paragraph to begin.
                }
                index++;
            }
            lastLineTracker.add(strings.size() - 1);    //After the break each time, this adds the index of the last line in a paragraph to a separate ArrayList.
        }
        return strings;
    }

    /**
     * This method is invoked either when the "R" argument is given to main, or when none is supplied (all 'Basic' tests). It
     * aligns the text by adding a number of white-spaces, and prints the result to the screen (returning nothing).
     * 
     * @param output - This is the ArrayList containing all the output lines (wrapped correctly) in their respective Strings.
     * @param wrappingNumber - This is the line length (in characters) that the line must not exceed (bar special cases).
     */
    public static void rightAlign(List<String> output, int wrappingNumber) {
        for (int i = 0; i < output.size(); i++) {
            for (int j = 0; j < (wrappingNumber - output.get(i).length()); j++) {
                System.out.print(" ");
            }
            System.out.println(output.get(i));
        }
    }

    /**
     * This method is invoked either when the "L" argument is given to main('Enhancement' tests). It is very simple, as text is
     * automatically left aligned when printed. The result is printed to the screen.
     * 
     * @param output - This is the ArrayList containing all the output lines (wrapped correctly) in their respective Strings.
     */
    public static void leftAlign(List<String> output) {
        for (String i : output) {
            System.out.println(i);
        }
    }

    /**
     * This method is invoked either when the "C" argument is given to main('Enhancement' tests). It aligns the text by adding
     * a number of white-spaces (half that of the right alignment), and prints the result to the screen (returning nothing).
     * 
     * @param output - This is the ArrayList containing all the output lines (wrapped correctly) in their respective Strings.
     * @param wrappingNumber - This is the line length (in characters) that the line must not exceed (bar special cases).
     */
    public static void centreAlign(List<String> output, float wrappingNumber) {
        for (int i = 0; i < output.size(); i++) {
            for (int j = 0; j < (int) Math.ceil((wrappingNumber - output.get(i).length()) / 2); j++) {
                System.out.print(" ");
            }
            System.out.println(output.get(i));
        }
    }

    /**
     * This method is invoked either when the "J" argument is given to main('Enhancement' tests). It aligns the text by spreading the
     * additional white-spaces evenly between the words to produce lines that align with both the left and right edges, with evenly
     * spaced words. It then prints the result to the screen (returning nothing).
     * 
     * @param output - This is the ArrayList containing all the output lines (wrapped correctly) in their respective Strings.
     * @param lastLineIndex - This is an ArrayList that stores index values which indicate where the last line of each paragraph
     *                        is stored in the new ArrayList that is created to store the output line Strings.
     * @param wrappingNumber - This is the line length (in characters) that the line must not exceed (bar special cases).
     */
    public static void justifyAlign(List<String> output, List<Integer> lastLineIndex, int wrappingNumber) {
        for (int i = 0; i < output.size(); i++) {                  //This outside loop iterates through all the lines (doesn't stop for paragraphs).
            boolean isLastLine = false;
            for (int j = 0; j < lastLineIndex.size(); j++) {       //This loop iterates through all of the 'Last Line' index values that were saved.
                if (i == lastLineIndex.get(j)) {                                //This "If" checks if the current line is the last of its paragraph.
                    isLastLine = true;                                   //This sets a boolean to true, indicating the line IS the paragraph's last.
                }
            }
            if (isLastLine || !output.get(i).contains(" ")) {                      //This "If" checks if the line is a 'Last Line' OR a single word.
                System.out.println(output.get(i));                                              //In these cases, the output is simply left aligned.
            } else {                                                                           //Else, all other lines have the 'Justify' alignment.
                int numberOfSpareSpaces = (wrappingNumber - output.get(i).length());  //Calculates the number of extra spaces that need distributed.
                String currentLine = output.get(i);
                int spaceAddingIndex = Math.max(currentLine.lastIndexOf(' ', currentLine.length()), 0);                                  //Sets an index at the lines' last space.
                for (int k = 0; k < numberOfSpareSpaces; k++) {                                                                                  //Loops through all spare spaces.
                    currentLine = currentLine.substring(0, spaceAddingIndex) + " " + currentLine.substring(spaceAddingIndex, currentLine.length());        //Adds space to String.
                    spaceAddingIndex--;                                                                                       //Reduces the number of remaining spare spaces by 1.
                    spaceAddingIndex = Math.max(currentLine.lastIndexOf(' ', spaceAddingIndex), 0);              //Moves the space index to the next space (moving right to left).
                    if (spaceAddingIndex == 0) {                                                    //This "If" checks if the space adder index has reached the start of the line.
                        spaceAddingIndex = Math.max(currentLine.lastIndexOf(' ', currentLine.length()), 0);   //If so and there are still spaces left, it loops back to the right.
                    }
                }
                System.out.println(currentLine);
            }
        }
    }
}
