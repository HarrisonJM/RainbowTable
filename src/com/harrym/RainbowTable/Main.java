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
            System.out.println("Threw");
        }

    }
}

class RainbowTable {
    private BufferedReader br;

    private Set<BigInteger> _hashCollisions;
    private final int _rows = 50000;
    private final int _hashes = 20000;
    private List<List<BigInteger>> _RainbowTable;

    public RainbowTable() throws Exception {
        File _file = new File(
                "/home/hmarcks/IdeaProjects/RainbowTable/src/com/harrym/RainbowTable/10-million-password-list-top" +
                        "-1000000.txt");
        FileReader _fr = new FileReader(_file);
        br = new BufferedReader(_fr);

        _RainbowTable = new ArrayList<>();
        _hashCollisions = new HashSet<>();
    }

    public void CreateRainbowTable() {
        for (int i = 0; i < _rows; i++) {
            try {
                String line = br.readLine();
                var row = GenerateRow(line);
                System.out.println("Row Number " + i);
                _RainbowTable.add(row);
            } catch (Exception e) {
                // why are you like this
            }
        }
        return;
    }

    public List<BigInteger> GenerateRow(String password) {

        List<BigInteger> row = new ArrayList<>();
        row.add(Hash(password));
        for (int i = 1; i < _hashes; ++i) {
            var reduced = Reduce(row.get(i - 1),
                                 i);
            if (reduced == null) {
                row = null;
                continue;
            }
            row.add(reduced);
        }
        return row;
    }

    public BigInteger Hash(String stringToHash) {
        BigInteger bi = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(stringToHash.getBytes(),
                      0,
                      stringToHash.length());
            bi = new BigInteger(1,
                                md.digest());
        } catch (Exception e) {
            // Do nothing lol
        }

        return (BigInteger) CheckCollision(bi);
    }

    public BigInteger Reduce(BigInteger hash) {

        var hashString = hash.toString().substring(0,
                                                   8);

        return Hash(hashString);
    }

    public BigInteger Reduce(BigInteger hash, int position) {
        BigInteger npp = hash;
        BigInteger MPS = npp.nextProbablePrime();
        BigInteger posBi = new BigInteger(Integer.toString(Integer.parseInt(hash.toString().substring(0, 4))*position));
        BigInteger red = posBi.mod(MPS);

        while(red.compareTo(new BigInteger("0")) == 1)
        {
            BigInteger index = red.mod(new BigInteger("10"));
            int index_num = Integer.parseInt(index.toString());
            String c = hash.toString().substring(index_num);
            var lastStep = red.add(new BigInteger(c));
            hash = lastStep.divide(new BigInteger("10"));
        }

        return red;
    }

    public Object CheckCollision(BigInteger hash) {
        if (_hashCollisions.add(hash)) {
            return hash;
        } else {
            return hash;
        }
    }
}

