/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufmt.sebs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raphael
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        List<File> arqs = new ArrayList<>();
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sfc.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sLAI.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sNDVI.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sTs.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/shc.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/Zref.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sUref.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sPressure.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sprmsl.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sPressure.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sTa_ref.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sqa_ref.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sAlbedo.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sEmissivity.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sSWd.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sLWd.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/shpbl.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sSWd24.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/slat.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sTa_avg.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sNs.nc"));
        arqs.add(new File(System.getProperty("user.dir") + "/InputData/sComptMask.nc"));
        
        SebsRun sebs = new SebsRun();
        sebs.calculate(arqs);
        
        Fractal fractal = new Fractal();
        fractal.calculate(System.getProperty("user.dir") + "/lorenz.txt", -2.0, 2.0, 0.5, 60, 10, 2);
        
    }
}
