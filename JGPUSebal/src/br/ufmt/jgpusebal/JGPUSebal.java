/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufmt.jgpusebal;

import br.ufmt.jgpusebal.landsat.LandSat;
import br.ufmt.jgpusebal.sebal.SebalRun;
import br.ufmt.jgpusebal.utils.DataFile;
import br.ufmt.jgpusebal.utils.DataStructure;
import br.ufmt.jgpusebal.utils.ExecutionEnum;
import br.ufmt.jgpusebal.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author raphael
 */
public class JGPUSebal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

//        List<File> arqs = new ArrayList<File>();
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/NDVI.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/Ts.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/Uref.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/SAVI.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/a.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/b.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/Albedo.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/Emissivity.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/SWd.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/LWd.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/Rg_24h.dat"));
//        arqs.add(new File(System.getProperty("user.dir") + "/InputSebal/Tao_24h.dat"));
//
//        SebalRun sebal = new SebalRun();
//        List<DataStructure> datas;
//
//        System.out.println("CUDA");
//        datas = sebal.calculate(arqs, ExecutionEnum.CUDA);
//
//        System.out.println("OpenCL");
//        datas = sebal.calculate(arqs, ExecutionEnum.OPENCL);
//
//        System.out.println("Java");
//        datas = sebal.calculate(arqs, ExecutionEnum.NORMAL);
//
//        Utilities.exportTXT(System.getProperty("user.dir") + "/OutputSebal", datas);
        
        LandSat land = new LandSat();
        String path = "/media/raphael/DISK/Faculdade/Doutorado/Artigos/EnviromentModelingSoftware/GPUSensoriamento/TIFF/rppn.tif";
        int julianDay = 248;
        float Z = 50.24f;
        float P = 99.3f;
        float UR = 36.46f;
        float Ta = 32.74f;
        float latitude = -16.56f;
        float Rg_24h = 243.949997f;
        float Uref = 0.92071358f;
        float Tao_24h = 0.63f;

        List<DataFile> datas2 = land.preprocessingLandSat5(path, julianDay, Z, P, UR, Ta, latitude, Rg_24h, Uref);


    }
}
