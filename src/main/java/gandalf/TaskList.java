package gandalf;

import action.Deadline;
import action.Event;
import action.Task;
import action.ToDo;

import exception.InvalidKeywordException;
import exception.InvalidTaskDeletionException;
import exception.InvalidTaskIndexException;
import exception.MissingDescriptionException;

import java.util.ArrayList;

public class TaskList {
    // Constants
    public static final String LINE = "____________________________________________________________";
    public static int insertIndex = 0;

    public static void handleUserTasks(String userInput, ArrayList<Task> listTasks)  {
        try {
            if (hasSaidMarkOrUnmark(userInput)) {
                handleTasksMarkings(userInput, listTasks);
            } else if (hasSaidDelete(userInput)) {
                deleteUserTasks(userInput, listTasks);
            }
            else {
                insertUserTasks(userInput, listTasks);
                insertIndex += 1;
            }
        } catch (InvalidKeywordException e) {
            Ui.printInvalidKeywordMessage();
        } catch (ArrayIndexOutOfBoundsException e) {
            Ui.printListIsFullMessage();
        } catch (MissingDescriptionException e) {
            Ui.printMissingDescriptionMessage();
        } catch (InvalidTaskIndexException e) {
            Ui.printInvalidTaskIndexMessage();
        } catch (InvalidTaskDeletionException e) {
            Ui.printInvalidTaskDeletionMessage();
        }
    }

    private static void deleteUserTasks(String userInput, ArrayList<Task> listTasks)
            throws InvalidTaskDeletionException {
        if (listTasks.isEmpty() || userInput.trim().length() == 6) {
            throw new InvalidTaskDeletionException();
        }
        
        int indexToDelete = Integer.parseInt(userInput.substring(6).trim());

        if (indexToDelete > listTasks.size()) {
            throw new InvalidTaskDeletionException();
        }

        System.out.println(LINE);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + listTasks.get(indexToDelete - 1));
        listTasks.remove(indexToDelete - 1);
        insertIndex -= 1;
        System.out.println("Now you have " + insertIndex + " tasks in the list.");
        System.out.println(LINE);
    }

    public static void insertUserTasks (String userInput, ArrayList<Task> listTasks)
            throws InvalidKeywordException, MissingDescriptionException, InvalidTaskIndexException {
        if (userInput.startsWith("todo")) {
            handleToDoTasks(userInput, listTasks, insertIndex);
        } else if (userInput.startsWith("deadline")) {
            handleDeadlineTasks(userInput, listTasks, insertIndex);
        } else if (userInput.startsWith("event")) {
            handleEventTasks(userInput, listTasks, insertIndex);
        } else {
            throw new InvalidKeywordException();
        }
    }

    public static void handleEventTasks(String userInput, ArrayList<Task> listTasks, int insertIndex) {
        if (!userInput.contains("/from") || !userInput.contains("/to")) {
            System.out.println("Invalid command.");
            System.out.println(LINE);
        }
        else {
            Parser parser = new Parser(userInput);
            String eventItem = parser.getEventItem();
            String eventFrom = parser.getEventFrom();
            String eventTo = parser.getEventTo();

            listTasks.add(insertIndex, new Event(eventItem, eventFrom, eventTo));
            System.out.println(LINE);
            System.out.println("Got it. I've added this task:");
            System.out.println("  " + listTasks.get(insertIndex));
            insertIndex += 1;
            System.out.println("Now you have " + insertIndex + " tasks in the list.");
            System.out.println(LINE);
        }
    }

    public static void handleDeadlineTasks(String userInput, ArrayList<Task> listTasks, int insertIndex) {
        if (!userInput.contains("/by")) {
            System.out.println("Invalid command.");
            System.out.println(LINE);
        }
        else {
            Parser parser = new Parser(userInput);
            String deadlineItem = parser.getDeadlineItem();
            String deadlineDueBy = parser.getDeadlineDueBy();

            listTasks.add(insertIndex, new Deadline(deadlineItem, deadlineDueBy));
            System.out.println(LINE);
            System.out.println("Got it. I've added this task:");
            System.out.println("  " + listTasks.get(insertIndex));
            insertIndex += 1;
            System.out.println("Now you have " + insertIndex + " tasks in the list.");
            System.out.println(LINE);
        }
    }

    public static void handleToDoTasks(String userInput, ArrayList<Task> listTasks, int insertIndex)
            throws MissingDescriptionException {
        if (userInput.trim().length() == 4){
            throw new MissingDescriptionException();
        } else {
            Parser parser = new Parser(userInput);
            String toDoItem = parser.getToDoItem();

            listTasks.add(insertIndex, new ToDo(toDoItem));
            System.out.println(LINE);
            System.out.println("Got it. I've added this task:");
            System.out.println("  " + listTasks.get(insertIndex));
            insertIndex += 1;
            System.out.println("Now you have " + insertIndex + " tasks in the list.");
            System.out.println(LINE);
        }
    }

    public static void handleTasksMarkings(String userInput, ArrayList<Task> listTasks)
            throws InvalidTaskIndexException {
        if (userInput.startsWith("mark ")) {
            int indexToMark = Integer.parseInt(userInput.substring(5).trim());
            if (indexToMark >= 1 && indexToMark <= listTasks.size() && listTasks.get(indexToMark - 1) != null) {
                listTasks.get(indexToMark - 1).markAsDone();
                System.out.println(LINE);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + listTasks.get(indexToMark - 1));
                System.out.println(LINE);
            } else {
                throw new InvalidTaskIndexException();
            }
        } else {
            int indexToUnmark = Integer.parseInt(userInput.substring(7).trim());
            if (indexToUnmark >= 1 && indexToUnmark <= listTasks.size() && listTasks.get(indexToUnmark - 1) != null) {
                listTasks.get(indexToUnmark - 1).unmarkAsDone();
                System.out.println(LINE);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("  " + listTasks.get(indexToUnmark - 1));
                System.out.println(LINE);
            } else {
                throw new InvalidTaskIndexException();
            }
        }
    }

    public static void printList(ArrayList<Task> listTasks) {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i) != null) {
                System.out.println((i + 1) + ". " + listTasks.get(i));
            }
        }
        System.out.println(LINE);
    }

    private static boolean hasSaidMarkOrUnmark(String userInput) {
        return (userInput.startsWith("mark ")  || userInput.startsWith("unmark "));
    }

    private static boolean hasSaidDelete(String userInput) {
        return userInput.startsWith("delete");
    }

    private static void printListIsFullMessage() {
        System.out.println(LINE);
        System.out.println("List is full. Cannot add more items.");
        System.out.println(LINE);
    }

    private static void printInvalidKeywordMessage() {
        System.out.println(LINE);
        System.out.println("Invalid keyword, the available keywords are:"
                + "\n(todo), (deadline), (event)"
                + "\nPlease try again.");
        System.out.println(LINE);
    }

    private static void printMissingDescriptionMessage() {
        System.out.println(LINE);
        System.out.println("Please fill in the description of the task.");
        System.out.println(LINE);
    }

    private static void printInvalidTaskIndexMessage() {
        System.out.println(LINE);
        System.out.println("Invalid task index. Please try again.");
        System.out.println(LINE);
    }

    private static void printInvalidTaskDeletionMessage() {
        System.out.println(LINE);
        System.out.println("Deletion unsuccessful, please try again.");
        System.out.println(LINE);
    }
}
