package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ranking {
    private final List<Player> players;

    public Ranking() {
        this.players = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (player == null) {
            return;
        }

        players.add(player);
        players.sort(Comparator.comparingLong(Player::getBestTime).thenComparingInt(Player::getAttempts));
    }
}
