package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.*;


public interface PlayerService {

    Player savePlayer(Player player);
    Player getPlayer(Long id);
    Player updatePlayer(Player oldPlayer, Player newPlayer) throws IllegalArgumentException;
    void deletePlayer (Player player);

    List<Player> getPlayers(String name,
                            String title,
                            Race race,
                            Profession profession,
                            Integer minExperience,
                            Integer maxExperience,
                            Integer minLevel,
                            Integer maxLevel,
                            Long after,
                            Long before,
                            Boolean banned);
    List<Player> sortPlayers(List<Player> players, PlayerOrder playerOrder);
    List<Player> getPage (List<Player> players, Integer pageNumber, Integer pageSize);
    boolean isPlayerValid (Player player);
}
