package com.harrym.RainbowTable;

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
    private final String _alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
    private final int _rows = 5000;
    private final int _hashes = 20000;

    private BufferedReader br;
    private Set<String> _hashCollisions;
    private HashMap<String, String> _RainbowTable;
    private BigInteger _mps;


    public RainbowTable() throws Exception {
        File _file = new File(
                "/home/hmarcks/IdeaProjects/RainbowTable/src/com/harrym/RainbowTable/10-million-password-list-top" +
                        "-1000000.txt");
//        File _file = new File("C:\\Users\\harrym\\IdeaProjects\\RainbowTable\\src\\com\\harrym\\RainbowTable\\10" +
//                                      "-million-password-list-top-1000000.txt");

        FileReader _fr = new FileReader(_file);
        br = new BufferedReader(_fr);

        _RainbowTable = new HashMap<>();
        _hashCollisions = new HashSet<>();
        BigInteger spacePowerLength = new BigInteger("36").pow(10);
        _mps = spacePowerLength.nextProbablePrime();
    }

    public void CreateRainbowTable() throws Exception {
        for (int i = 0; i < _rows; i++) {
            String line = br.readLine();
            GenerateRow(line);
            System.out.println("Row Number " + i);
        }
    }

    public void GenerateRow(String password) throws Exception {
        String thing = password;
        boolean killFlag = false;
        for (int i = 0; i < _hashes; ++i) {
            // On even steps hash it
            if (i % 2 == 0) {
                thing = Hash(thing);
            } else {
                // on odd steps reduce it
                thing = Reduce(thing,
                               i);
            }
            if (CheckCollision(thing) == null) {
                killFlag = true;
                break;
            }
        }
        if (!killFlag) {
            _RainbowTable.put(password,
                              thing);
        }
    }

    public String Hash(String stringToHash) throws Exception {
        BigInteger bi;

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

    public String Reduce(String hash,
                         int position) {
        BigInteger hash_bi = new BigInteger(Integer.toString(Integer.parseInt(hash.substring(0,
                                                                                             4)) * position));
        hash_bi = hash_bi.add(new BigInteger(hash));
        hash_bi = hash_bi.mod(_mps);

        String password = "";
        StringBuilder password_builder = new StringBuilder();

        while (hash_bi.compareTo(BigInteger.ZERO) != -1) {
            BigInteger index = hash_bi.mod(new BigInteger( "36" ));
            hash_bi = hash_bi.divide(new BigInteger("36"));

            {
                int index_num = Integer.parseInt(index.toString());
                String foo = _alphabet.substring(index_num,
                                                 index_num + 1);
                char c = foo.charAt(0);
                password_builder.append(c);
            }
            hash_bi = hash_bi.subtract(BigInteger.ONE);
        }

        return password_builder.toString();
    }

    public Object CheckCollision(String hash) {
        if (_hashCollisions.add(hash)) {
            return hash;
        } else {
            return null;
        }
    }
}
