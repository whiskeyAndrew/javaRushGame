package com.game.entity;

import java.util.Date;

public class Player {
    private long id;
    private String name;
    private String title;
    private String race;
    private String profession;
    private Date birthday;

    private Boolean banned;
    private Long experience;
    private Long level;
    private Long untilNextLevel;

    //починить когда перестану выебываться на леху



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        banned = banned;
    }

    public Long getExperience() {
        return experience;
    }

    public void setExperience(Long experience) {
        this.experience = experience;
    }

    public Long getLvl() {
        return level;
    }

    public void setLvl(Long lvl) {
        this.level = lvl;
    }

    public Long getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Long untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Player(long id, String name, String title, String race, String profession, Date birthday, Boolean isBanned, Long experience, Long lvl, Long untilNextLevel) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.birthday = birthday;
        this.banned = isBanned;
        this.experience = experience;
        this.level = lvl;
        this.untilNextLevel = untilNextLevel;
    }
}
