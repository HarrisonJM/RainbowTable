package com.harrym.RainbowTable;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        // write your code here
        try {
            RainbowTable rt = new RainbowTable();
            rt.CreateRainbowTable();
        } catch (Exception E) {
            System.out.println(E.getMessage());
        }

    }
}

class RainbowTable {
    private BufferedReader br;

    private Set<String> _hashCollisions;
    private final int _rows = 5000;
    private final int _hashes = 20000;
    private HashMap<String, String> _RainbowTable;
    private BigInteger _mps;

    public RainbowTable() throws Exception {
//        File _file = new File(
//                "/home/hmarcks/IdeaProjects/RainbowTable/src/com/harrym/RainbowTable/10-million-password-list-top" +
//                        "-1000000.txt");
        File _file = new File("C:\\Users\\harrym\\IdeaProjects\\RainbowTable\\src\\com\\harrym\\RainbowTable\\10-million-password-list-top-1000000.txt");

        FileReader _fr = new FileReader(_file);
        br = new BufferedReader(_fr);

        _RainbowTable = new HashMap<>();
        _hashCollisions = new HashSet<>();
        int spacePowerLength = 36 ^ 10;
        _mps = new BigInteger(Integer.toString(spacePowerLength));
    }

    public void CreateRainbowTable() throws Exception {
        for (int i = 0; i < _rows; i++) {
            String line = br.readLine();
            GenerateRow(line);
            System.out.println("Row Number " + i);
//            _RainbowTable.add(row);
        }
        return;
    }

    public void GenerateRow(String password) throws Exception {
        String thing = password;
        boolean killFlag = false;
        for (int i = 0; i < _hashes; ++i) {
            // On even steps hash it
            if (i % 2 == 0) {
                thing = Hash(password);
            } else {
                // on odd steps reduce it
                thing = Reduce(thing, i);
            }
            if (CheckCollision(thing) == null) {
                killFlag = true;
                continue;
            }
        }
        if(!killFlag)
            _RainbowTable.put(password, thing);
    }

    public String Hash(String stringToHash) throws Exception {
        BigInteger bi = null;

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(stringToHash.getBytes(),
                0,
                stringToHash.length());
        bi = new BigInteger(1,
                md.digest());

        return bi.toString();
    }

    public String Reduce(BigInteger hash) throws Exception {

        var hashString = hash.toString().substring(0,
                8);
        return Hash(hashString);
    }

    public String Reduce(String hash, int position) {
        BigInteger posBi = new BigInteger(Integer.toString(Integer.parseInt(hash.toString().substring(0, 4)) * position));
        BigInteger red = posBi.mod(_mps);
        String password = "";
        while (red.compareTo(BigInteger.ZERO) == 1) {
            BigInteger index = red.mod(new BigInteger("36"));
            int index_num = Integer.parseInt(index.toString());
            String foo = hash.toString().substring(index_num, index_num + 1);
            char c = foo.charAt(0);
            password += c;
            red = red.divide(new BigInteger("36"));
        }

        return password;
    }

    public Object CheckCollision(String hash) {
        if (_hashCollisions.add(hash)) {
            return hash;
        } else {
            return null;
        }
    }
}

