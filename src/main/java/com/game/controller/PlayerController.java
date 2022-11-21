package com.game.controller;

import com.game.entity.Player;
import com.game.playerDAO.PlayerDAO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerDAO playerDAO;

    @Autowired
    public PlayerController(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }


    @GetMapping("")
    public ResponseEntity<String> listAllHeaders(
            @RequestHeader Map<String, String> headers) {
        headers.forEach((key, value) -> {
            System.out.printf("Header '%s' = %s%n", key, value);
        });

        return new ResponseEntity<String>(
                String.format(headers.toString()), HttpStatus.OK);
    }


    //AllPlayers
    @GetMapping("/players")
    @ResponseBody
    public List<Player> GetAllPlayers(HttpServletRequest request) {

        Enumeration enumeration = request.getParameterNames();
        Map<String, Object> modelMap = new HashMap<>();
        while(enumeration.hasMoreElements()){
            String parameterName = (String) enumeration.nextElement();
            modelMap.put(parameterName, request.getParameter(parameterName));
        }


        return playerDAO.GetPlayersFromDB(modelMap);
    }

    //PlayerCount
    @GetMapping("/players/count")
    @ResponseBody
    public Integer GetPlayersCount() {
        return playerDAO.GetPlayersCount();
    }

    //AddPlayer
    @PostMapping("/players")
    @ResponseBody
    public ResponseEntity<Player> AddPlayer(@RequestBody Player player) {
        if(playerDAO.AddPlayer(player)!=null){
            return new ResponseEntity<>(player,HttpStatus.OK);
        } else{
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/players/{id}")
    @ResponseBody
    public Player GetPlayer(@PathVariable("id") Integer id) {
        return playerDAO.GetPlayer(id);
    }

    @PostMapping("/players/{id}")
    @ResponseBody
    public ResponseEntity<Player> UpdatePlayer(@RequestBody Player player, @PathVariable("id") Integer id) {

        ResponseEntity<Player> responseEntity = null;

        try {
            int idInt = id;
            responseEntity = playerDAO.UpdatePlayer(id, player);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(player, HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    @DeleteMapping("/players/{id}")
    @ResponseBody
    public ResponseEntity DeletePlayer(@PathVariable("id") Integer id) {
        return playerDAO.DeletePlayer(id);
    }

    //Запилить фильтрацию по данным снизу, по количеству страниц и по другому
}
