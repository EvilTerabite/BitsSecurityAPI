package me.evilterabite.bitssecurityapi.security;

import me.evilterabite.bitssecurityapi.BitsSecurityAPI;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.jvm.hotspot.utilities.Bits;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class User {

    private String username;
    private String password;

    private static FileWriter file;
    private static FileReader reader;
    private static JSONArray users;
    private static JSONParser parser;
    private static final Plugin plugin = BitsSecurityAPI.getPlugin(BitsSecurityAPI.class);
    private static final String userFilePath = plugin.getDataFolder() + "users.json";

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void save(User user) {
        parser = new JSONParser();
        Object obj = null;
        try {
            reader = new FileReader(userFilePath);
            obj = parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        users = (JSONArray) obj;
        if(users != null) {
            JSONObject userJSON = new JSONObject();
            userJSON.put(user.username, user.password);
            users.add(userJSON.toString());
            try {
                file = new FileWriter(userFilePath);
                file.write(users.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    file.flush();
                    file.close();
                    System.out.println(user.username + " saved!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            users = new JSONArray();
        }
    }

    public static boolean register(String username, String password) {
        List<User> userList = getUserList();
        for(User user : userList) {
            if(user.username.equalsIgnoreCase(username)) {
                BitsSecurityAPI.logger.log(Level.INFO, "User Attempt: User already exists: " + user.username);
                return false;
            }
        }
        String encodedPass = PasswordEncoder.encode(password);
        User user = new User(username, encodedPass);
        User.save(user);
        return true;
    }

    public static boolean login(String username, String password) {
        User user = findUsername(username);
        if(user == null) return false;
        return PasswordEncoder.check(password, user.password);
    }


    public static List<User> getUserList() {
        Object obj = null;
        try {
            reader = new FileReader(userFilePath);
            parser = new JSONParser();
            obj = parser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        users = (JSONArray) obj;
        ArrayList<User> userArray = new ArrayList<>();
        for (Object user : users) {
            Object userOBJ = null;
            try {
                userOBJ = parser.parse(user.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONObject userJSON = (JSONObject) userOBJ;
            for (int j = 0; j < userJSON.keySet().size(); j++) {
                String username = (String) userJSON.keySet().toArray()[j];
                String password = (String) userJSON.get(username);
                userArray.add(new User(username, password));
            }
        }

        return userArray;
    }

    public static User findUsername(String username) {
        List<User> userList = getUserList();
        for(User user : userList) {
            if(user.username.equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public static User placeholderUser() {
        return new User("Not logged in", "");
    }
}
