import chess.*;
import exceptions.AlreadyTakenException;
import exceptions.AuthException;
import exceptions.BadRequestException;
import exceptions.InvalidAuthTokenException;
import model.AuthData;
import model.GameData;
import network.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
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
            System.out.println("i guess we'll just shut everything down.");
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

        ArrayList<GameData> gamesList = null;

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
                        System.out.println("join [game #] [\"white\" or \"black\"] - joins an existing game");
                        System.out.println("observe [game #] - stalk someone else's game");
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

                    if (auth == null) {
                        System.out.println("of course, there are a bunch more things you can do once you're logged in!");
                    }
                    break;
                case "fq":
                case "force":
                case "forcequit":
                    closing = true;
                    break;
                case "q":
                case "quit":
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

                    var register_username = tokens[1];
                    var register_password = tokens[2];
                    var register_email = tokens[3];

                    System.out.println("Trying to register...");

                    try {
                        auth = facade.register(register_username, register_password, register_email);
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
                case "l":
                case "login":

                    if (tokens.length == 1) {
                        System.out.println("You need a username and password!");
                        break;
                    } else if (tokens.length == 2) {
                        System.out.println("You're missing a username or password!");
                        break;
                    }

                    if (auth != null) {
                        System.out.println("You are already logged in, actually!");
                        System.out.println("Please log out first if you're desperate to login again.");
                        break;
                    }

                    var login_username = tokens[1];
                    var login_password = tokens[2];

                    try {
                        auth = facade.login(login_username,login_password);
                        System.out.println("Welcome, " + auth.username() + "!");
                    } catch (URISyntaxException e) {
                        System.out.println("Looks like something's wrong with this client :/");
                    } catch (IOException e) {
                        System.out.println("We're having trouble connecting to the server :/");
                    } catch (InterruptedException e) {
                        System.out.println("The request got interrupted :/");
                    } catch (AuthException e) {
                        System.out.println(e.getMessage());
                    } catch (IllegalStateException e) {
                        System.out.println("Something went wrong :/");
                    } catch (BadRequestException e) {
                        System.out.println("Seems like your username or password is malformed!");
                    }
                    break;
                case "logout":

                    if (auth == null) {
                        System.out.println("No need! You're aren't logged in yet.");
                        break;
                    }

                    try {
                        facade.logout(auth);
                        auth = null;
                    } catch (URISyntaxException e) {
                        System.out.println("Looks like something's wrong with this client :/");
                    } catch (IOException e) {
                        System.out.println("We're having trouble connecting to the server :/");
                    } catch (InterruptedException e) {
                        System.out.println("The request got interrupted :/");
                    }
                    break;
                case "c":
                case "create":

                    if (auth == null) {
                        System.out.println("You gotta log in first!");
                        break;
                    }

                    if (tokens.length == 1) {
                        System.out.println("You must provide a game name!");
                    }

                    var gameName = tokens[1];

                    try {
                        facade.createGame(auth,gameName);
                        System.out.println("Game creation successful! Use the \"list\" command to find the game #!");
                    } catch (URISyntaxException e) {
                        System.out.println("Looks like something's wrong with this client :/");
                    } catch (IOException e) {
                        System.out.println("We're having trouble connecting to the server :/");
                    } catch (InterruptedException e) {
                        System.out.println("The request got interrupted :/");
                    } catch (InvalidAuthTokenException e) {
                        System.out.println("Your session is no longer valid :(");
                        System.out.println("I'll log you out so you can log back in!");
                        auth = null;
                    } catch (IllegalStateException e) {
                        System.out.println("Something went wrong :/");
                    }
                    break;
                case "list":

                    if (auth == null) {
                        System.out.println("You gotta log in first!");
                        break;
                    }

                    try {
                        Collection<GameData> games = facade.listGames(auth);
                        System.out.println("Here are all the current games:");

                        gamesList = new ArrayList<>();

                        int num = 0;
                        for (GameData entry : games) {
                            gamesList.add(entry);
                            String out = "  " + num + ". " + entry.gameName();

                            if (entry.whiteUsername() == null && entry.blackUsername() == null) {
                                out = out + "    no one has joined this game yet!";
                            } else {
                                if (entry.whiteUsername() != null) {
                                    out = out + "    playing as white: " + entry.whiteUsername();
                                }

                                if (entry.blackUsername() != null) {
                                    out = out + "    playing as black: " + entry.blackUsername();
                                }

                                if (entry.whiteUsername() == null) {
                                    out = out + "    WHITE is available!";
                                }

                                if (entry.blackUsername() == null) {
                                    out = out + "    BLACK is available!";
                                }
                            }

                            System.out.println(out);
                            num += 1;
                        }
                    } catch (URISyntaxException e) {
                        System.out.println("Looks like something's wrong with this client :/");
                    } catch (IOException e) {
                        System.out.println("We're having trouble connecting to the server :/");
                    } catch (InterruptedException e) {
                        System.out.println("The request got interrupted :/");
                    } catch (InvalidAuthTokenException e) {
                        System.out.println("Your session is no longer valid :(");
                        System.out.println("I'll log you out so you can log back in!");
                        auth = null;
                    } catch (IllegalStateException e) {
                        System.out.println("Something went wrong :/");
                    }
                    break;
                case "j":
                case "p":
                case "play":
                case "join":

                    if (auth == null) {
                        System.out.println("You gotta log in first!");
                        break;
                    }

                    if (tokens.length == 1) {
                        System.out.println("You must provide a game # and \"WHITE\" for white or \"BLACK\" for black");
                        break;
                    }

                    if (tokens.length == 2) {
                        System.out.println("You must specify \"WHITE\" for white or \"BLACK\" for black");
                        break;
                    }

                    if (gamesList == null) {
                        System.out.println("Use the 'list' command to see the options first! Jeez!");
                        break;
                    }


                    int num = Integer.parseInt(tokens[1]);
                    var colorString = tokens[2];

                    if (num > gamesList.size() || num < 0) {
                        System.out.println("That's not a real game #. Nice try bucko.");
                        break;
                    }

                    if (!Objects.equals(colorString, "WHITE") && !Objects.equals(colorString, "BLACK")) {
                        System.out.println("Make sure your team color is \"WHITE\" for white or \"BLACK\" for black");
                        break;
                    }

                    GameData game = gamesList.get(num);

                    if (colorString.equals("WHITE") && game.whiteUsername() != null) {
                        System.out.println("White is already taken in this game, sorry.");
                        break;
                    } else if (colorString.equals("BLACK") && game.blackUsername() != null) {
                        System.out.println("Black is already taken in this game, sorry.");
                        break;
                    }

                    try {
                        System.out.println("Trying to join...");
                        facade.joinGame(auth,colorString,game.gameID());
                        System.out.println("Joined!\n");

                        if (colorString.equals("WHITE")) {
                            System.out.println(game.game().toStringWhite());
                        } else {
                            System.out.println(game.game().toStringBlack());
                        }
                    } catch (URISyntaxException e) {
                        System.out.println("Looks like something's wrong with this client :/");
                    } catch (IOException e) {
                        System.out.println("We're having trouble connecting to the server :/");
                    } catch (InterruptedException e) {
                        System.out.println("The request got interrupted :/");
                    } catch (InvalidAuthTokenException e) {
                        System.out.println("Your session is no longer valid :(");
                        System.out.println("I'll log you out so you can log back in!");
                        auth = null;
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