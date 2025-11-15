import chess.*;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnknownException;
import model.AuthData;
import network.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
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
        var facade = new ServerFacade(8080);

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

            switch (tokens[0].toLowerCase()) {
                case "h":
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
                case "n":
                case "no":
                    if (exiting == 1) {
                        System.out.println("yay!");
                    }
                    break;
                case "y":
                case "yes":
                    switch (exiting) {
                        case 1:
                            exitOne();
                            break;
                        case 2:
                            exitTwo();
                            break;
                    }
                    break;
                case "r":
                case "register":

                    if (tokens.length == 1) {
                        System.out.println("You need a username, password, and email!");
                        break;
                    } else if (tokens.length == 2) {
                        System.out.println("You're missing a username, password, and/or email!");
                        break;
                    } else if (tokens.length == 3) {
                        System.out.println("You're missing a username, password, or email!");
                        break;
                    }

                    var username = tokens[1];
                    var password = tokens[2];
                    var email = tokens[3];

                    System.out.println("Trying to register...");

                    try {
                        auth = facade.register(username, password, email);
                        System.out.println("Welcome, " + auth.username() + "!");
                    } catch (URISyntaxException e) {
                        System.out.println("Looks like something's wrong with this client :/");
                    } catch (IOException e) {
                        System.out.println("We're having trouble connecting to the server :/");
                    } catch (InterruptedException e) {
                        System.out.println("The request got interrupted :/");
                    } catch (BadRequestException e) {
                        System.out.println("Seems like your username or password is malformed!");
                    } catch (AlreadyTakenException e) {
                        System.out.println("This username is already taken!");
                    } catch (IllegalStateException e) {
                        System.out.println("Something went wrong :/");
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