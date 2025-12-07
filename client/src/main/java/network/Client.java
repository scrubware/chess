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

import static network.REPLTools.handleHelp;
import static network.REPLTools.handleList;

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
    private boolean gameLocked = false;

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
            outputUserCursor();

            String input = scanner.nextLine();
            var tokens = input.split(" ");
            try {
                switch (tokens[0].toLowerCase()) {
                    case "h", "help" -> handleHelp(auth, game, !(isObserver() || gameLocked));
                    case "fq", "force", "forcequit" -> closing = true;
                    case "q", "quit", "e", "exit" -> handleExit();
                    case "n", "no" -> handleNo();
                    case "y", "yes" -> handleYes();
                    case "r", "register" -> handleRegister(tokens);
                    case "login" -> handleLogin(tokens);

                    // Once-Authed Commands
                    case "logout" -> handleLogout();
                    case "c", "create" -> handleCreate(tokens);
                    case "list" -> gamesList = handleList(auth,http);
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
            } catch (LockedGameException e) {
                System.out.println("This game has already been completed");
                teamColor = null;
                game = null;
            }

            if (!skipResetExit) {
                exiting = 0;
            }
        } while (!closing);
    }

    public boolean isObserver() {
        if (game.whiteUsername() != null && game.whiteUsername().equals(auth.username())) {
            return false;
        }
        if (game.blackUsername() != null && game.blackUsername().equals(auth.username())) {
            return false;
        }
        return true;
    }

    public void outputUserCursor() {
        String userLabel = "Guest";
        if (auth != null) {
            userLabel = auth.username();
            if (game != null) {
                userLabel = auth.username() + ": OBSERVER";
                if (teamColor != null) {
                    userLabel = auth.username() + ": " + teamColor;
                }
            }
        }
        System.out.print("[" + userLabel + "] >>> ");
    }

    public void updateGame(GameData game) {
        this.game = game;
    }

    public void markGameLocked() {
        gameLocked = true;
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

        if (isObserver()) {
            System.out.println("You can't run this command while just observing.");
            return;
        }

        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        if (gameLocked) {
            System.out.println("This game has already ended.");
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

    private void handleResign() throws IOException {
        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        if (isObserver()) {
            System.out.println("You can't run this command while just observing.");
            return;
        }

        if (gameLocked) {
            System.out.println("This game has already ended.");
            return;
        }

        if (!tryingToResign) {
            System.out.println("Are you sure? This will forfeit the game.");
            System.out.println("Type 'resign' again to confirm or 'no' to cancel.");
            tryingToResign = true;
            return;
        }

        tryingToResign = false;

        ws.sendResign(auth.authToken(), game.gameID());
    }

    private void handleLeave() throws IOException {
        if (game == null) {
            System.out.println("You need to be in a game to run this command.");
            return;
        }

        ws.sendLeave(auth.authToken(),game.gameID());
        game = null;
        teamColor = null;
        tryingToResign = false;
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

    private void handleObserve(String[] tokens) throws DeploymentException, IOException, URISyntaxException {
        if (auth == null) {
            System.out.println("You gotta log in first!");
            return;
        }

        if (tokens.length == 1) {
            System.out.println("You must provide a game #");
            return;
        }

        int observeNum;
        try {
            observeNum = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            System.out.println("Your game # must be a number with no decimal places.");
            return;
        }

        if (gamesList == null) {
            System.out.println("Use the 'list' command to see the options first!");
            return;
        }

        if (observeNum > gamesList.size() || observeNum < 0) {
            System.out.println("That's not a real game #. Nice try bucko.");
            return;
        }

        game = gamesList.get(observeNum);
        ws.connect(auth.authToken(),game.gameID());

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