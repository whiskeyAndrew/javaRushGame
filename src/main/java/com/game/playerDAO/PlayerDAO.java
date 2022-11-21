package com.game.playerDAO;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

@Component
public class PlayerDAO {

    Connection con;


    @Autowired
    public PlayerDAO(DataSource dataSource) {
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Player> GetPlayersFromDB(Map<String, Object> modelMap) {
        List<Player> players = new ArrayList<>();

        StringBuilder sqlQuery = new StringBuilder("SELECT * FROM player ");

        if (!modelMap.isEmpty()) {
            sqlQuery.append("WHERE ");
        }

        if (modelMap.containsKey("profession")) {
            sqlQuery.append("profession = '").append(Profession.valueOf((String) modelMap.get("profession"))).append("' AND ");
        }

        if (modelMap.containsKey("race")) {
            sqlQuery.append("race = '").append(Race.valueOf((String) modelMap.get("race"))).append("' AND ");

        }

        if (modelMap.containsKey("maxLevel")) {
            sqlQuery.append("level < " + modelMap.get("maxLevel") + " AND ");

        }

        if (modelMap.containsKey("minLevel")) {
            sqlQuery.append("level > " + modelMap.get("minLevel") + " AND ");

        }

        if (modelMap.containsKey("before")) {
            sqlQuery.append("birthday <" + new java.sql.Date((Long) modelMap.get("before")) + " AND ");
        }

        if (modelMap.containsKey("after")) {
            sqlQuery.append("birthday >" + new java.sql.Date((Long) modelMap.get("after")) + " AND ");
        }

        if (modelMap.containsKey("title")) {
            sqlQuery.append("title = '" + modelMap.get("title") + "' AND ");
        }

        if (modelMap.containsKey("minExperience")) {
            sqlQuery.append("experience>" + modelMap.get("minExperience") + " AND ");
        }

        if (modelMap.containsKey("maxExperience")) {
            sqlQuery.append("experience<" + modelMap.get("maxExperience") + " AND ");
        }

        if (modelMap.containsKey("name")) {
            sqlQuery.append("name LIKE '%" + modelMap.get("name") + "%' AND ");
        }

        if (modelMap.containsKey("banned")) {
            sqlQuery.append("banned =" + modelMap.get("banned") + " AND ");
        }

        if (sqlQuery.indexOf("AND") != -1) {
            sqlQuery.delete(sqlQuery.lastIndexOf("AND"), sqlQuery.lastIndexOf("AND") + 3);
        }

        sqlQuery.trimToSize();
        if (sqlQuery.indexOf("WHERE") == sqlQuery.length() - 6) {
            sqlQuery.delete(sqlQuery.indexOf("WHERE"), sqlQuery.indexOf("WHERE") + 6);
        }

        if (modelMap.containsKey("order")) {
            String result = "";
            switch ((String) modelMap.get("order")) {
                case "ID": {
                    result = "id";
                    break;
                }
                case "NAME": {
                    result = "name";
                    break;
                }
                case "EXPERIENCE": {
                    result = "experience";
                    break;
                }
                case "BIRTHDAY": {
                    result = "birthday";
                    break;
                }
                default: {
                    System.out.println("я обосрался");
                    break;
                }
            }
            sqlQuery.append("ORDER BY " + result + " ");
        } else{
            sqlQuery.append("ORDER BY id ");
        }

        if(modelMap.containsKey("pageSize")){
            int offset = Integer.parseInt(modelMap.get("pageNumber").toString()) * Integer.parseInt(modelMap.get("pageSize").toString());
            sqlQuery.append("LIMIT "+modelMap.get("pageSize")+" OFFSET "+offset);
        } else {
            int offset = 0*3;
            sqlQuery.append("LIMIT "+3+" OFFSET "+offset);
        }

        System.out.println(sqlQuery);
        try {
            Statement stmt = con.createStatement();
            ResultSet set = stmt.executeQuery(sqlQuery.toString());

            while (set.next()) {
                players.add(new Player(
                        set.getLong("id"),
                        set.getString("name"),
                        set.getString("title"),
                        Race.valueOf(set.getString("race")),
                        Profession.valueOf(set.getString("profession")),
                        set.getDate("birthday"),
                        set.getBoolean("banned"),
                        set.getLong("experience"),
                        set.getLong("level"),
                        set.getLong("untilNextLevel")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }

    public Integer GetPlayersCount() {
        Integer playersCount = null;
        try {
            Statement stmt = con.createStatement();
            ResultSet set = stmt.executeQuery("SELECT COUNT(1) FROM player");
            set.next();
            playersCount = set.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playersCount;
    }

    public Player AddPlayer(Player player) {
        if(player.getBanned()==null){
            player.setBanned(false);
        }

        if(player.getName()==null || player.getTitle()==null || player.getRace()==null || player.getProfession()==null || player.getBirthday()==null || player.getExperience()==null){
            return null;
        }

        if(player.getName().equals("") || player.getTitle().equals("")){
            return null;
        }

        if(player.getName().length()>12 || player.getTitle().length()>30){
            return null;
        }

        if((player.getBirthday().getTime()< new GregorianCalendar(2000, 0, 1).getTimeInMillis())){
            return null;
        }

        if(player.getBirthday().getTime()<0){
            return null;
        }
        if((player.getBirthday().getTime()> new GregorianCalendar(3000, 0, 1).getTimeInMillis())){
            return null;
        }

        if(player.getExperience()<0 || player.getExperience()>10000000){
            return null;
        }


        player.recountExp();
        try {
            PreparedStatement prstmt = con.prepareStatement("INSERT INTO player (name,title,race,profession,birthday,banned,experience,level,untilNextLevel) VALUES (?,?,?,?,?,?,?,?,?)");
            prstmt.setString(1, player.getName());
            prstmt.setString(2, player.getTitle());
            prstmt.setString(3, player.getRace().toString());
            prstmt.setString(4, player.getProfession().toString());
            prstmt.setDate(5, new java.sql.Date(player.getBirthday().getTime()));
            prstmt.setBoolean(6, player.getBanned());
            prstmt.setLong(7, player.getExperience());
            prstmt.setLong(8, player.getLvl());
            prstmt.setLong(9, player.getUntilNextLevel());
            prstmt.executeUpdate();
            prstmt = con.prepareStatement("SELECT id FROM player ORDER BY id DESC LIMIT 1;");
            ResultSet set = prstmt.executeQuery();
            set.next();
            int id = set.getInt(1);
            player.setId(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public Player GetPlayer(Integer id) {
        Player player = null;
        try {
            Statement stmt = con.createStatement();
            ResultSet set = stmt.executeQuery("SELECT * FROM PLAYER WHERE id = " + id);
            set.next();
            player = new Player(set.getLong("id"),
                    set.getString("name"),
                    set.getString("title"),
                    Race.valueOf(set.getString("race")),
                    Profession.valueOf(set.getString("profession")),
                    set.getDate("birthday"),
                    set.getBoolean("banned"),
                    set.getLong("experience"),
                    set.getLong("level"),
                    set.getLong("untilNextLevel"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    public ResponseEntity<Player> UpdatePlayer(Integer id, Player player) {
        ResponseEntity<Player> responseEntity = null;
        player.recountExp();
        try {
            PreparedStatement prstmt = con.prepareStatement("UPDATE player SET name = ?, title = ?, race = ?, profession = ?, birthday = ?, banned = ?, experience = ?, level = ?, untilNextLevel = ? WHERE id = " + id);
            prstmt.setString(1, player.getName());
            prstmt.setString(2, player.getTitle());
            prstmt.setString(3, player.getRace().toString());
            prstmt.setString(4, player.getProfession().toString());
            prstmt.setDate(5, new java.sql.Date(player.getBirthday().getTime()));
            prstmt.setBoolean(6, player.getBanned());
            prstmt.setLong(7, player.getExperience());
            prstmt.setLong(8, player.getLvl());
            prstmt.setLong(9, player.getUntilNextLevel());
            int result = prstmt.executeUpdate();
            if (result == 0) {
                responseEntity = new ResponseEntity<>(player, HttpStatus.NOT_FOUND);
            } else responseEntity = new ResponseEntity<>(player, HttpStatus.OK);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return responseEntity;
    }

    public ResponseEntity DeletePlayer(Integer id) {
        ResponseEntity responseEntity = null;
        try {
            PreparedStatement prstmt = con.prepareStatement("DELETE FROM player WHERE id = " + id);
            Integer result = prstmt.executeUpdate();
            if (result == 0) {
                responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
            } else {
                responseEntity = new ResponseEntity(HttpStatus.OK);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return responseEntity;
    }
}
