package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameDAOTests {

    @BeforeEach
    public void clearBefore() {
        var clearDAO = new DatabaseClearDAO();

        try {
            clearDAO.clear();
        } catch(Exception _) {}
    }

    @Test
    @DisplayName("Get Game")
    public void getGame() {
        var gameDAO = new DatabaseGameDAO();
        var emptyGame = new GameData(1,null, null, "game", new ChessGame());
        int id = gameDAO.createGame("game");
        try {
            Assertions.assertEquals(emptyGame,gameDAO.getGame(id));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Get Game Negative")
    public void getGameNegative() {
        var gameDAO = new DatabaseGameDAO();
        try {
            Assertions.assertNull(gameDAO.getGame(0));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update Game")
    public void updateGame() {
        var gameDAO = new DatabaseGameDAO();
        var updatedGame = new GameData(1,"changed", null, "game", new ChessGame());
        int id = gameDAO.createGame("game");
        Assertions.assertTrue(gameDAO.updateGame(id,updatedGame));
        try {
            Assertions.assertEquals(updatedGame,gameDAO.getGame(id));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Update Game Negative")
    public void updateGameNegative() {
        var gameDAO = new DatabaseGameDAO();
        var updatedGame = new GameData(1,"changed", null, "game", new ChessGame());
        Assertions.assertFalse(gameDAO.updateGame(0,updatedGame));
    }

    @Test
    @DisplayName("Create Game")
    public void createGame() {
        var gameDAO = new DatabaseGameDAO();
        int id = gameDAO.createGame("game");
        Assertions.assertTrue(id >= 0);
    }

    @Test
    @DisplayName("Create Game Negative")
    public void createGameNegative() {
        var gameDAO = new DatabaseGameDAO();
        Assertions.assertFalse(gameDAO.createGame(null) >= 0);
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    @DisplayName("List Games")
    public void listGames() {
        var gameDAO = new DatabaseGameDAO();
        var emptyGame = new GameData(1,null, null, "game", new ChessGame());
        var gameList = new ArrayList<GameData>();

        Assertions.assertTrue(gameDAO.listGames().isEmpty());

        gameDAO.createGame("game");
        Assertions.assertEquals(1,gameDAO.listGames().size());

        gameList.add(emptyGame);
        Assertions.assertEquals(gameList,gameDAO.listGames());

        gameDAO.createGame("game2");
        Assertions.assertEquals(2,gameDAO.listGames().size());
    }

    @Test
    @DisplayName("List Games Negative")
    public void listGamesNegative() {
        var gameDAO = new DatabaseGameDAO();
        gameDAO.createGame(null);
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }
}
