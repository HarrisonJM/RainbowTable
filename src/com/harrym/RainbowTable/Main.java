package com.harrym.RainbowTable;

import javafx.util.Pair;

import java.io.*;
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

    private int _collisionCounter = 0;

    private BufferedReader br;
    private HashSet<String> _hashCollisions;
    private HashMap<String, String> _RainbowTable;
    private BigInteger _mps;

    private FileOutputStream _outFile;
    private ByteArrayOutputStream _outbs;
    private ObjectOutputStream _outos;
    private FileInputStream _inFile;
    private ByteArrayInputStream _inbs;
    private ObjectInputStream _inos;

    public RainbowTable() throws Exception {
//        File _file = new File(
//                "/home/hmarcks/IdeaProjects/RainbowTable/src/com/harrym/RainbowTable/10-million-password-list-top" +
//                        "-1000000.txt");
        File _file = new File("C:\\Users\\harrym\\IdeaProjects\\RainbowTable\\src\\com\\harrym\\RainbowTable\\10" +
                "-million-password-list-top-1000000.txt");
        FileReader _fr = new FileReader(_file);
        br = new BufferedReader(_fr);

        SetupWriteFile();

        _RainbowTable = new HashMap<>();
        _hashCollisions = new HashSet<>();
        BigInteger spacePowerLength = new BigInteger("36").pow(10);
        _mps = spacePowerLength.nextProbablePrime();
    }

    public void CreateRainbowTable() throws Exception {
        for (int i = 0; i < _rows; i++) {
            String line = br.readLine();
            GenerateRow(line);
            System.out.println("Row Number: " + i + " Collisions: " + _collisionCounter);
            if (i % 100 == 0)
                _outFile.flush();
        }
    }

    private void GenerateRow(String password) throws Exception {
        String thing = password;
        boolean killFlag = false;
        for (int i = 0; i < _hashes; ++i) {
            // On even steps hash it
            if (i % 2 == 0) {
                thing = Hash(thing);
            } else {
                    thing = Reduce(thing,
                            i);
            }
            if (CheckCollision(thing) == null) {
                killFlag = true;
                break;
            }
        }
        if (!killFlag) {
            WriteToFile(new Pair<>(password, thing));
        }
    }

    private String Hash(String stringToHash) throws Exception {
        BigInteger bi;

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(stringToHash.getBytes(),
                0,
                stringToHash.length());
        bi = new BigInteger(1,
                md.digest());
        return bi.toString();
    }

    private String ReduceFront(String hash) throws Exception {
        var hashString = hash.toString().substring(0,
                8);
        return Hash(hashString);
    }

    private String ReduceBack(String hash) throws Exception {
        var hashString = hash.substring(hash.length()-8, hash.length()+1);
        return Hash(hashString);
    }

    private String Reduce(String hash,
                               int position) {
        BigInteger hash_bi = new BigInteger(Integer.toString(Integer.parseInt(hash.substring(0,
                4)) * position));
        hash_bi = hash_bi.add(new BigInteger(hash));
        hash_bi = hash_bi.mod(_mps);

        StringBuilder password_builder = new StringBuilder();
        while (hash_bi.compareTo(BigInteger.ZERO) != -1) {
            BigInteger index = hash_bi.mod(new BigInteger("36"));
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

    private Object CheckCollision(String hash) {
        if (_hashCollisions.add(hash)) {
            return hash;
        } else {
            _collisionCounter++;
            return null;
        }
    }

    public String CrackPassword(String password ) throws Exception
    {
        // dasn35zcosq
        // uyhr3b3igld
        boolean matchFound = false;
        Pair<String, String> row;
        do
        {
            row = ReadPair();
            if (row == null)
                throw new Exception("No matches!");

            if (row.getValue() == password)
                matchFound = true;
        } while(!matchFound);

        var key = row.getKey();
    
        return "";
    }

    private void SetupReadFile() throws Exception {
        String path = ".\\table.dat";
        _inFile = new FileInputStream(path);
        _inbs = new ByteArrayInputStream(_inFile.readAllBytes());
        _inos = new ObjectInputStream(_inbs);
    }

    private void SetupWriteFile() throws Exception {
        String path = ".\\table.dat";
        _outFile = new FileOutputStream(path);
        _outbs = new ByteArrayOutputStream();
        _outos = new ObjectOutputStream(_outbs);
    }

    private void WriteToFile(Pair<String, String> row) throws Exception {
        _outos.writeObject(row);
        _outFile.write(_outbs.toByteArray());
    }

    public void ReadTable() throws Exception {
        SetupReadFile();
        boolean _stillReading = true;
        while (_stillReading) {
            Object foo = _inos.readObject();
            if (foo != null) {
                var p = (Pair<String, String>) foo;
                _RainbowTable.put(p.getKey(), p.getValue());
            } else
                _stillReading = false;
        }
    }

    public Pair<String, String> ReadPair() throws Exception {
        SetupReadFile();
        Object foo = _inos.readObject();
        return (Pair<String, String>)foo;
    }
}
