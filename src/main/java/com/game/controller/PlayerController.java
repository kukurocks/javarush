package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Player> createPlayer(@RequestBody Player player){
        if(!playerService.isPlayerValid(player)){
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(player.getBanned()==null)player.setBanned(false);
        player.setLevel();
        player.setUntilNextLevel();
        // needs to get entity.getBody?and compare with nothing
        final Player savePlayer = playerService.savePlayer(player);

        return new ResponseEntity<>(savePlayer, HttpStatus.OK);

    }
   @GetMapping("/players")
    public List<Player> getAllPlayers(
            @RequestParam(value = "name", required = false)String name,
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value ="banned", required = false) Boolean banned,
            @RequestParam(value="order",required = false) PlayerOrder order
   ){
        List<Player> list = playerService.getPlayers(name,title,race,profession,minExperience,
                maxExperience,minLevel,maxLevel,after,before,banned);
        List<Player> sortedList = playerService.sortPlayers(list,order);


        return playerService.getPage(sortedList,pageNumber, pageSize);
       /*List<Player> result = playerService.findAll();
       List<Player> sortedResult;
       Date dateAfter;
       Date dateBefore;

       if(after!=null&&before!=null){
         dateAfter =  new Date(after);
         dateBefore = new Date(before);
       }
       else {
           dateAfter =new Date();
           dateBefore =new Date();
       }
       if(after!=null&&before!=null){
           sortedResult = result.stream().filter(p->p.getBirthday().after(dateAfter)
                           &&p.getBirthday().before(dateBefore))
                           .collect(Collectors.toList());
           result = sortedResult;

           if(race!=null||profession!=null){
               result = result.stream().filter(p->p.getRace().equals(race)||
                       p.getProfession().equals(profession)).collect(Collectors.toList());
               return new ResponseEntity<>(result, HttpStatus.OK);
           }
           else if (minLevel!=null&&maxLevel!=null) {
               result = result.stream().filter(p->p.getLevel()>minLevel&&p.getLevel()<maxLevel)
                       .collect(Collectors.toList());
               return new ResponseEntity<>(result, HttpStatus.OK);
           }
           else if (minExperience!=null&&maxExperience!=null) {
               result = result.stream().filter(
                       p->p.getExperience()>minExperience&&p.getExperience()<maxExperience)
                       .collect(Collectors.toList());
               return new ResponseEntity<>(result, HttpStatus.OK);
           }

       }
       else if(race!=null||profession!=null){
           sortedResult = result.stream().filter(p->p.getRace().equals(race)||
                   p.getProfession().equals(profession)).collect(Collectors.toList());
           result =sortedResult;
       }
       else if(minLevel!=null&&maxLevel!=null){
           sortedResult = result.stream().filter(p->
           p.getLevel()>minLevel&&p.getLevel()<maxLevel)
                   .collect(Collectors.toList());
           result = sortedResult;
       }
       else if(minExperience!=null&&maxExperience!=null) {
           sortedResult = result.stream().filter(
                   p->p.getExperience()>minExperience&&p.getExperience()<maxExperience)
           .collect(Collectors.toList());
           result = sortedResult;
       }
       else if(name!=null){
           result = playerService.findByName(name);
       }

        return new ResponseEntity<>(result, HttpStatus.OK);*/
    }
    @GetMapping("/players/count")
    public Integer count(@RequestParam(value = "name", required = false)String name,
                         @RequestParam(value = "title",required = false) String title,
                         @RequestParam(value = "race", required = false) Race race,
                         @RequestParam(value = "profession", required = false) Profession profession,
                         @RequestParam(value = "after", required = false) Long after,
                         @RequestParam(value = "before", required = false) Long before,
                         @RequestParam(value = "minExperience", required = false) Integer minExperience,
                         @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                         @RequestParam(value = "minLevel", required = false) Integer minLevel,
                         @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                         @RequestParam(value = "pageSize", required = false) Integer pageSize,
                         @RequestParam(value ="banned", required = false) Boolean banned

    ){
        return playerService.getPlayers(name,title,race,profession,minExperience,
                maxExperience,minLevel,maxLevel,after,before,banned).size();
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable(value="id")Long id){
        if(id==null|| id<=0){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        Player player = playerService.getPlayer(id);
        if(player==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player,HttpStatus.OK);
    }
    @RequestMapping(value = "/players/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Player> updatePlayer(@PathVariable(value = "id")Long id,
                                               @RequestBody Player player) throws IllegalArgumentException {
        final ResponseEntity<Player> playerResponseEntity = getPlayer(id);
       final Player savedPlayer = playerResponseEntity.getBody();
        if(savedPlayer==null) {return playerResponseEntity;}
        Player result=null;
        try{
              result = playerService.updatePlayer(savedPlayer,player);
              result.setLevel();
              result.setUntilNextLevel();

        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity<>(result,HttpStatus.OK);
    }
    @RequestMapping(value="/players/{id}",method = RequestMethod.DELETE)
    public ResponseEntity<Player> deletePlayer(@PathVariable(value="id")Long id){
        if(id==0) {return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        ResponseEntity<Player> playerResponseEntity = getPlayer(id);
        Player deletePlayer = playerResponseEntity.getBody();
        if(deletePlayer==null) {return playerResponseEntity;}
        playerService.deletePlayer(deletePlayer);
        return new ResponseEntity<>(HttpStatus.OK);
    }








}
