package network;

import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

public class REPLTools {
    static public void handleHelp(AuthData auth, GameData game, boolean showExtra) {

        System.out.println();

        if (game != null) {
            System.out.println("redraw - redraws the board");
            System.out.println("legal [row-column] - highlight legal moves for a piece");
            System.out.print("leave - exit the game");

            if (showExtra) {
                System.out.println(" (does not forfeit)");
                System.out.println("move [from row-column] [to row-column] - move a chess piece");
                System.out.println("resign - forfeit the game");
            } else {
                System.out.println();
            }

            System.out.println("\nUse the letter of the row and the number of the column for commands");
            System.out.println("for instance, 'move a2 a4' or 'legal g1'");
        } else {
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
            System.out.println("quit - kinda does what you'd expect");
            System.out.println("help - looks like you figured this one out already!");

            if (auth == null) {
                System.out.println("\nof course, there are a bunch more things you can do once you're logged in!");
            }
        }

        System.out.println();
    }

    static public ArrayList<GameData> handleList(AuthData auth, ServerFacade http)
            throws URISyntaxException, IOException, InterruptedException {
        if (auth == null) {
            System.out.println("You gotta log in first!");
            return new ArrayList<>();
        }

        Collection<GameData> games = http.listGames(auth);

        if (games.isEmpty()) {
            System.out.println("No games have been created yet!");
            System.out.println("Use the \"create\" command to make one of your own!");
        } else {
            System.out.println("Here are all the current games:");
        }

        var gamesList = new ArrayList<GameData>();

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

        return gamesList;
    }
}
