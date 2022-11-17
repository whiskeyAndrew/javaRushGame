package com.game.playerDAO;

import com.game.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerDAO {

    Connection con;
    List<Player> players;
    {
        players = new ArrayList<>();
    }

    @Autowired
    public PlayerDAO(DataSource dataSource) {
        try {
            con = dataSource.getConnection();
            fillListWithPlayers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void fillListWithPlayers() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet set =  stmt.executeQuery("SELECT * FROM player");

        while(set.next()){
            players.add(new Player(
                    set.getLong("id"),
                    set.getString("name"),
                    set.getString("title"),
                    set.getString("race"),
                    set.getString("profession"),
                    set.getDate("birthday"),
                    set.getBoolean("banned"),
                    set.getLong("experience"),
                    set.getLong("level"),
                    set.getLong("untilNextLevel")));
        }
    }

    public List<Player> index(){
        return players;
    }
}
