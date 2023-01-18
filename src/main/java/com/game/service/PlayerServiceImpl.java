package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional

public class PlayerServiceImpl implements PlayerService{

    private PlayerRepository playerRepository;

    public PlayerServiceImpl(){}

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        super();
        this.playerRepository = playerRepository;
    }


    @Override
    public Player savePlayer(Player player)  {
        return playerRepository.save(player);
    }

    @Override
    public Player getPlayer(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    public Player updatePlayer(Player oldPlayer, Player newPlayer) throws IllegalArgumentException {
        String name=newPlayer.getName();
        if(name !=null){
                if(name.length()<12){
                    oldPlayer.setName(name);
                }
                else {
            throw new IllegalArgumentException();
                    }
        }
        String title = newPlayer.getTitle();
        if(title !=null){
            if(title.length()<30){
                oldPlayer.setTitle(title);
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        Date birthday = newPlayer.getBirthday();
        if(birthday!=null){
            if(isDateValid(birthday)){
                oldPlayer.setBirthday(birthday);
            } else {
                throw new IllegalArgumentException();
            }
        }
            Integer experience = newPlayer.getExperience();
            if(experience!=null){
                if(experience>0&&experience<10000000){
                    oldPlayer.setExperience(experience);
                }
                else {
                    throw new IllegalArgumentException();
                }
            }
            Race race = newPlayer.getRace();
            if(race!=null){
                oldPlayer.setRace(race);
            }
            Profession profession = newPlayer.getProfession();
            if(profession!=null){
                oldPlayer.setProfession(profession);
            }
            if(newPlayer.getBanned()!=null){
                oldPlayer.setBanned(newPlayer.getBanned());
            }

        playerRepository.save(oldPlayer);
        return oldPlayer;
    }

    @Override
    public void deletePlayer(Player player) {
        playerRepository.delete(player);

    }

    @Override
    public List<Player> getPlayers(String name,
                                   String title,
                                   Race race,
                                   Profession profession,
                                   Integer minExperience,
                                   Integer maxExperience,
                                   Integer minLevel,
                                   Integer maxLevel,
                                   Long after,
                                   Long before,
                                   Boolean banned) {



        final Date afterDate = after==null ? new Date(1) :new Date(after);
         final Date beforeDate = before == null ? new Date() :new Date(before);
        final List<Player> list = new ArrayList<>();

        for(Player player: playerRepository.findAll()){

            if(!(name==null)&&!(player.getName().toLowerCase().contains(name.toLowerCase()))) continue;
            if(!(title==null)&&!(player.getTitle().toLowerCase().contains(title.toLowerCase())))continue;
            if(!(before==null)&&player.getBirthday().after(beforeDate)) continue;
            if(!(after==null)&&player.getBirthday().before(afterDate)) continue;
            if(!(minExperience==null)&&player.getExperience()<minExperience) continue;
            if(!(maxExperience==null)&&player.getExperience()>maxExperience) continue;
            if(!(minLevel==null)&&player.getLevel()<minLevel)continue;
            if(!(maxLevel==null)&&player.getLevel()>maxLevel)continue;
            if(!(race==null)&&!player.getRace().equals(race))continue;
            if(!(profession==null)&&!player.getProfession().equals(profession))continue;
            if((banned!=null)&&!player.getBanned().equals(banned))continue;


            list.add(player);

        }
        return list;
    }

    @Override
    public List<Player> sortPlayers(List<Player> players, PlayerOrder playerOrder) {
        if (playerOrder!= null) {
            players.sort((player1, player2) -> {
                switch (playerOrder) {
                    case ID: return player1.getId().compareTo(player2.getId());
                    case NAME: return player1.getName().compareTo(player2.getName());
                    case EXPERIENCE: return player1.getExperience().compareTo(player2.getExperience());
                    case BIRTHDAY: return player1.getBirthday().compareTo(player2.getBirthday());
                    default: return 0;
                }
            });
        }
        return players;
    }

    @Override
    public List<Player> getPage(List<Player> players, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > players.size()) to = players.size();
        return players.subList(from, to);
    }

    @Override
    public boolean isPlayerValid(Player player) {
        return player!=null&&player.getName()!=null&&
                !player.getName().isEmpty()&&
                player.getName().length()<12&&!player.getTitle().isEmpty()&&
                player.getTitle().length()<30&&
                player.getBirthday().getTime()>0&&
                player.getRace()!=null&&
                player.getProfession()!=null&&
                (player.getExperience()>0&&
                player.getExperience()<10000000);
    }

    private boolean isDateValid(Date birthday) {
        final Date start = getDateForYear(2000);
        final Date end = getDateForYear(3000);
        return birthday!= null&& birthday.getTime()>0 && birthday.after(start) && birthday.before(end);
    }

    private Date getDateForYear(int year) {
       Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }





}
