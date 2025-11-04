package dataaccess;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GameDAOTests {

    @BeforeEach
    public void clear() {
        var clearDAO = new DatabaseClearDAO();
        clearDAO.clear();
    }

    @Test
    @DisplayName("Get Game")
    public void getGame() {
        var gameDAO = new DatabaseGameDAO();
        var emptyGame = new GameData(0,null, null, "game", new ChessGame());
        int id = gameDAO.createGame("game");
        Assertions.assertEquals(emptyGame,gameDAO.getGame(id));
    }

    @Test
    @DisplayName("Get Game Negative")
    public void getGameNegative() {
        var gameDAO = new DatabaseGameDAO();
        Assertions.assertNull(gameDAO.getGame(0));
    }

    @Test
    @DisplayName("Update Game")
    public void updateGame() {
        var gameDAO = new DatabaseGameDAO();
        var updatedGame = new GameData(0,"changed", null, "game", new ChessGame());
        int id = gameDAO.createGame("game");
        Assertions.assertTrue(gameDAO.updateGame(id,updatedGame));
        Assertions.assertEquals(updatedGame,gameDAO.getGame(id));
    }

    @Test
    @DisplayName("Update Game Negative")
    public void updateGameNegative() {
        var gameDAO = new DatabaseGameDAO();
        var updatedGame = new GameData(0,"changed", null, "game", new ChessGame());
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

    }

    @Test
    @DisplayName("List Games Negative")
    public void listGamesNegative() {

    }
}
