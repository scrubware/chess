import chess.*;
import exceptions.*;
import jakarta.websocket.DeploymentException;
import model.AuthData;
import model.GameData;
import network.ServerFacade;
import network.WebSocketFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {

    // REPL state
    private int exiting = 0;
    private boolean closing = false;
    private boolean skipResetExit = false;

    // User state
    private AuthData auth = null;
    private GameData game = null;
    private ChessGame.TeamColor teamColor = null;
    private ArrayList<GameData> gamesList = null;

    // Network Components
    private final ServerFacade http;
    private final WebSocketFacade ws;

    public Client(int port) {
        http = new ServerFacade(port);
        ws = new WebSocketFacade(port);
    }

    private void exitOne() {
        System.out.println("we're all gonna miss you...");
        exiting ++;
        skipResetExit = true;
    }

    private void exitTwo() {
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

    public void run() {

        System.out.println("♕ Welcome to this super cool Chess program!\n\n");
        System.out.println("Type 'help' if you're just getting started!");

        Scanner scanner = new Scanner(System.in);

        do {
            String userLabel = "Guest";
            if (auth != null) {
                userLabel = auth.username();
                if (game != null) {
                    userLabel = auth.username() + ": " + teamColor;
                }
            }
            System.out.print("[" + userLabel + "] >>> ");

            String input = scanner.nextLine();
            var tokens = input.split(" ");
            try {
                switch (tokens[0].toLowerCase()) {
                    case "h", "help" -> handleHelp();
                    case "fq", "force", "forcequit" -> closing = true;
                    case "q", "quit", "e", "exit" -> handleExit();
                    case "n", "no" -> handleNo();
                    case "y", "yes" -> handleYes();
                    case "r", "register" -> handleRegister(tokens);
                    case "login" -> handleLogin(tokens);
                    case "logout" -> handleLogout();
                    case "c", "create" -> handleCreate(tokens);
                    case "list" -> handleList();
                    case "j", "join", "p", "play" -> handleJoin(tokens);
                    case "o", "observe" -> handleObserve(tokens);
                }
            } catch (URISyntaxException e) {
                System.out.println("Looks like something's wrong with this client :/");
            } catch (IOException e) {
                System.out.println("We're having trouble connecting to the server :/");
            } catch (InterruptedException e) {
                System.out.println("The request got interrupted :/");
            } catch (InvalidAuthTokenException e) {
                System.out.println("Your session is no longer valid :(");
                System.out.println("Let me log you out!");
                auth = null;
            } catch (IllegalStateException e) {
                System.out.println("Something went wrong :/");
            } catch (AuthException e) {
                System.out.println("That user isn't registered!");
            } catch (BadRequestException e) {
                System.out.println("Seems like your username or password is malformed!");
            } catch (AlreadyTakenException e) {
                System.out.println("This username is already taken!");
            } catch (InvalidGameIDException e) {
                System.out.println("This game is not available anymore :/");
                System.out.println("Try fetching the available games again with \"list\"!");
            } catch (DeploymentException e) {
                System.out.println("We couldn't reserve a connection to the server :/");
            }

            if (!skipResetExit) {
                exiting = 0;
            }
        } while (!closing);
    }

    private void handleNo() {
        if (exiting == 1) {
            System.out.println("yay!");
        }
    }

    private void handleYes() {
        switch (exiting) {
            case 1 -> exitOne();
            case 2 -> exitTwo();
        }
    }

    private void handleExit() {
        switch (exiting) {
            case 0 -> {
                System.out.println("you really want to go? :(");
                exiting ++;
                skipResetExit = true;
            }
            case 1 -> exitOne();
            case 2 -> exitTwo();
        }
    }

    private void handleList() throws URISyntaxException, IOException, InterruptedException {
        if (auth == null) {
            System.out.println("You gotta log in first!");
            return;
        }

        Collection<GameData> games = http.listGames(auth);

        if (games.isEmpty()) {
            System.out.println("No games have been created yet!");
            System.out.println("Use the \"create\" command to make one of your own!");
        } else {
            System.out.println("Here are all the current games:");
        }

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
    }

    private void handleRegister(String[] tokens) throws URISyntaxException, IOException, InterruptedException {
        if (tokens.length == 1) {
            System.out.println("You need a username, password, and email!");
            return;
        } else if (tokens.length == 2) {
            System.out.println("You're missing a username, password, and/or email!");
            return;
        } else if (tokens.length == 3) {
            System.out.println("You're missing a username, password, or email!");
            return;
        }

        var registerUsername = tokens[1];
        var registerPassword = tokens[2];
        var registerEmail = tokens[3];

        System.out.println("Trying to register...");

        auth = http.register(registerUsername, registerPassword, registerEmail);
        System.out.println("Welcome, " + auth.username() + "!");
    }

    private void handleLogout() throws URISyntaxException, IOException, InterruptedException {
        if (auth == null) {
            System.out.println("No need! You're aren't logged in yet.");
            return;
        }

        http.logout(auth);
        auth = null;
    }

    private void handleHelp() {
        if (game != null) {
            System.out.println("redraw - redraws the board");
            System.out.println("leave - exit the game (does not forfeit)");
            System.out.println("move [from row-column] [to row-column] - move a chess piece (ex. move a2 a4)");
            System.out.println("resign - forfeit the game");
            System.out.println("legal [row-column] - highlight legal moves for a piece");

            System.out.println("\nUse the letter of the row and the number of the column for your moves,");
            System.out.println("i.e. 'move a2 a4' or 'legal g1'");
            return;
        }

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
            System.out.println("of course, there are a bunch more things you can do once you're logged in!");
        }
    }

    private void handleLogin(String[] tokens) throws URISyntaxException, IOException, InterruptedException, AuthException {
        if (tokens.length == 1) {
            System.out.println("You need a username and password!");
            return;
        } else if (tokens.length == 2) {
            System.out.println("You're missing a username or password!");
            return;
        }

        if (auth != null) {
            System.out.println("You are already logged in, actually!");
            System.out.println("Please log out first if you're desperate to login again.");
            return;
        }

        var loginUsername = tokens[1];
        var loginPassword = tokens[2];

        auth = http.login(loginUsername,loginPassword);
        System.out.println("Welcome, " + auth.username() + "!");
    }

    private void handleCreate(String[] tokens) throws URISyntaxException, IOException, InterruptedException {
        if (auth == null) {
            System.out.println("You gotta log in first!");
            return;
        }

        if (tokens.length == 1) {
            System.out.println("You must provide a game name!");
            return;
        }

        var gameName = tokens[1];

        http.createGame(auth,gameName);
        System.out.println("Game creation successful! Use the \"list\" command to find the game #!");
    }

    private void handleObserve(String[] tokens) {
        if (auth == null) {
            System.out.println("You gotta log in first!");
            return;
        }

        if (tokens.length == 1) {
            System.out.println("You must provide a game #");
            return;
        }

        if (gamesList == null) {
            System.out.println("Use the 'list' command to see the options first!");
            return;
        }

        int observeNum;
        try {
            observeNum = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            System.out.println("Your game # must be a number with no decimal places.");
            return;
        }


        if (observeNum > gamesList.size() || observeNum < 0) {
            System.out.println("That's not a real game #. Nice try bucko.");
            return;
        }

        GameData observeGame = gamesList.get(observeNum);

        System.out.println(observeGame.game().toStringWhite());
    }

    private void handleJoin(String[] tokens) throws URISyntaxException, IOException, InterruptedException, DeploymentException {
        if (auth == null) {
            System.out.println("You gotta log in first!");
            return;
        }

        if (tokens.length == 1) {
            System.out.println("You must provide a game # and \"WHITE\" for white or \"BLACK\" for black");
            return;
        }

        if (tokens.length == 2) {
            System.out.println("You must specify \"WHITE\" for white or \"BLACK\" for black");
            return;
        }

        if (gamesList == null) {
            System.out.println("Use the 'list' command to see the options first!");
            return;
        }

        int num;
        try {
            num = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            System.out.println("Your game # must be a number with no decimal places.");
            return;
        }


        if (num >= gamesList.size() || num < 0) {
            System.out.println("That's not a real game #. Nice try bucko.");
            return;
        }

        teamColor = ChessGame.TeamColor.fromString(tokens[2]);

        if (teamColor == null) {
            System.out.println("Make sure your team color is \"WHITE\" for white or \"BLACK\" for black");
            return;
        }

        game = gamesList.get(num);

        if (teamColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null) {
            System.out.println("White is already taken in this game, sorry.");
            return;
        } else if (teamColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null) {
            System.out.println("Black is already taken in this game, sorry.");
            return;
        }

        System.out.println("Trying to join...");
        http.joinGame(auth,teamColor.toString(),game.gameID());
        ws.connect();
        System.out.println("Joined!\n");

        if (teamColor == ChessGame.TeamColor.WHITE) {
            System.out.println(game.game().toStringWhite());
        } else {
            System.out.println(game.game().toStringBlack());
        }
    }
}