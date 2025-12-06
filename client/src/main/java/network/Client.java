package network;

import chess.*;
import exceptions.*;
import jakarta.websocket.DeploymentException;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
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
    private boolean tryingToResign = false;

    // Network Components
    private final ServerFacade http;
    private final WebSocketFacade ws;

    public Client(int port) {
        http = new ServerFacade(port);
        ws = new WebSocketFacade(port, this);
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

                    // Once-Authed Commands
                    case "logout" -> handleLogout();
                    case "c", "create" -> handleCreate(tokens);
                    case "list" -> handleList();
                    case "j", "join", "p", "play" -> handleJoin(tokens);
                    case "o", "observe" -> handleObserve(tokens);

                    // Game Commands
                    case "redraw" -> handleRedraw();
                    case "leave" -> handleLeave();
                    case "resign" -> handleResign();
                    case "legal" -> handleLegal(tokens);
                    case "move" -> handleMove(tokens);
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

    public void updateGame(GameData game) {
        this.game = game;
    }

    private ChessPosition stringToPosition(String string) {

        String lower = string.toLowerCase();

        if (lower.charAt(0) >= 'a' && lower.charAt(0) <= 'h') {
            if (lower.charAt(1) >= '1' && lower.charAt(1) <= '8') {
                int rank = lower.charAt(0) - 'a' + 1;
                int file = lower.charAt(1) - '1' + 1;

                return new ChessPosition(file,rank);
            }
        }

        return null;
    }

    private void handleRedraw() {
        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        drawBoard(null);
    }

    private void handleLegal(String[] tokens) {
        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        if (tokens.length < 2) {
            System.out.println("You need to supply a position to check.");
            return;
        }

        var position = stringToPosition(tokens[1]);

        if (position == null) {
            System.out.println("Make sure you're formatting the position right! e.g. 'a1'");
            return;
        }

        drawBoard(position);
    }

    private void handleMove(String[] tokens) throws IOException {
        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        if (tokens.length < 3) {
            System.out.println("You need to supply a start and end position for your move.");
            return;
        }

        var startPosition = stringToPosition(tokens[1]);
        var endPosition = stringToPosition(tokens[2]);

        if (startPosition == null) {
            if (endPosition == null) {
                System.out.println("Make sure you're formatting both the positions right! e.g. 'a1'");
            } else {
                System.out.println("Make sure you're formatting the start position right! e.g. 'a1'");
            }
            return;
        } else {
            if (endPosition == null) {
                System.out.println("Make sure you're formatting the end position right! e.g. 'a1'");
                return;
            }
        }

        ws.sendMove(auth.authToken(), game.gameID(), new ChessMove(startPosition, endPosition, null));
    }

    private void handleResign() {
        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        if (!tryingToResign) {
            System.out.println("Are you sure? This will forfeit the game.");
            System.out.println("Type 'resign' again to confirm or 'no' to cancel.");
            tryingToResign = true;
            return;
        }

        ws.sendResign();
    }

    private void handleLeave() {
        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        ws.sendLeave();
    }

    public void drawBoard(ChessPosition validMovesPosition) {
        System.out.println();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            System.out.println(game.game().toStringBlack(validMovesPosition));
        } else { // This covers also the null case where the user is observing.
            System.out.println(game.game().toStringWhite(validMovesPosition));
        }
    }

    private void handleNo() {
        if (exiting == 1) {
            System.out.println("yay!");
        }

        if (tryingToResign) {
            System.out.println("Cancelling resignation.");
            tryingToResign = false;
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

        System.out.println();

        if (game != null) {
            System.out.println("redraw - redraws the board");
            System.out.println("leave - exit the game (does not forfeit)");
            System.out.println("move [from row-column] [to row-column] - move a chess piece");
            System.out.println("resign - forfeit the game");
            System.out.println("legal [row-column] - highlight legal moves for a piece");

            System.out.println("\nUse the letter of the row and the number of the column for commands");
            System.out.println("for instance, 'move a2 a4' or 'legal g1'");
            return;
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

        drawBoard(null);
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
        ws.connect(auth.authToken(),game.gameID());
        System.out.println("Joined!\n");

        drawBoard(null);
    }
}