/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fca;

import db.SQLiteJDBC;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import muzdima.parser.MParser;

/**
 *
 * @author neophron
 */
public class FCA {

    private FCL fcl = new FCL();
    private MParser mp = new MParser();

    private void processSentence(String s) {
        //mp = new MParser();
        List l = mp.Parse(s);
        Sentence sen = new Sentence(l);
        fcl.addSentence(sen);
//        fcl.addSentenceToDB(l);
    }

    public static void main(String[] args) {
        FCA fca = new FCA();
        Scanner in = null;
        try {
            in = new Scanner(new File("/home/neophron/Documents/C03"));
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            return;
        }
        int ok = 0, er = 0;
        while (in.hasNext()) {
            try {
                fca.processSentence(in.nextLine());
                ok++;
            } catch (Exception ex) {
                er++;
            }
        }
        System.out.println(ok);
        System.out.println(er);
        List l = fca.fcl.getEdges();
//        List l = fca.fcl.getEdgesFromDB(1, 1);
//        try {
//            fca.fcl.finish();
//
//        } catch (SQLException ex) {
//            Logger.getLogger(FCA.class.getName()).log(Level.SEVERE, null, ex);
//        }
        for (Object p : l) {
            System.out.println(p.toString());
        }
        SQLiteJDBC.db.dropAll();
    }

}
// -XX:+PrintGC
