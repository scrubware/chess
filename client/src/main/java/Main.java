import chess.*;
import model.AuthData;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int exiting = 0;
    private static boolean closing = false;
    private static boolean skipResetExit = false;

    private static void exitOne() {
        System.out.println("we're all gonna miss you...");
        exiting ++;
        skipResetExit = true;
    }

    private static void exitTwo() {
        System.out.println("alright well... if that's what you want.");
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("everyone close down shop. the player's leaving");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("hope you come back soon!");
            TimeUnit.SECONDS.sleep(1);
            closing = true;
            skipResetExit = true;
        } catch (Exception e) {}
    }

    public static void main(String[] args) {
        System.out.println("♕ Welcome to this super cool Chess program!\n\n");

        AuthData auth = null;

        System.out.println("Type 'help' if you're just getting started!");

        Scanner scanner = new Scanner(System.in);

        while (true) {

            if (auth != null) {
                System.out.print("[" + auth.username() + "] >>> ");
            } else {
                System.out.print("[Unauthenticated] >>> ");
            }

            String input = scanner.nextLine();
            var tokens = input.split(" ");

            switch (tokens[0]) {
                case "H":
                case "h":
                case "Help":
                case "help":

                    if (auth != null) {
                        System.out.println("create [game name] - makes a new public game");
                        System.out.println("list - shows you all the public game IDs");
                        System.out.println("join [game id] [\"white\" or \"black\"] - joins an existing game");
                        System.out.println("observe [game id] - stalk someone else's game");
                        System.out.println("logout - this logs you out");
                    } else {
                        System.out.println("register [username] [password] [email] - makes an account");
                        System.out.println("login [username] [password] - logs into an existing account");
                    }
                    System.out.println("quit - kinda does what you'd expect, but why would you want to go?");

                    if (tokens.length > 1) {
                        System.out.println("help - looks like you figured this one out already! or, mostly...");
                        System.out.print("you threw ");
                        for (int i = 1; i < tokens.length; i ++) {
                            System.out.print(tokens[i] + " ");
                        }
                        System.out.println("in there. for fun i guess?");
                    } else {
                        System.out.println("help - looks like you figured this one out already!");
                    }
                    break;
                case "e":
                case "E":
                case "Exit":
                case "exit":

                    switch (exiting) {
                        case 0:
                            System.out.println("you really want to go? :(");
                            exiting ++;
                            skipResetExit = true;
                            break;
                        case 1:
                            exitOne();
                            break;
                        case 2:
                            exitTwo();
                            break;
                    }
                    break;
                case "N":
                case "n":
                case "NO":
                case "no":
                    if (exiting == 1) {
                        System.out.println("yay!");
                    }
                    break;
                case "Y":
                case "y":
                case "yes":
                case "YES":
                    switch (exiting) {
                        case 1:
                            exitOne();
                            break;
                        case 2:
                            exitTwo();
                            break;
                    }
                    break;
            }

            if (!skipResetExit) {
                exiting = 0;
            }

            if (closing) {
                break;
            }
        }
    }
}