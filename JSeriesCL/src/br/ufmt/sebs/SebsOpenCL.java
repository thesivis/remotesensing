/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufmt.sebs;

import br.ufmt.jseriescl.JSeriesCL;
import br.ufmt.jseriescl.ParameterGPU;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author raphael
 */
public class SebsOpenCL {

    private String SEBS_kb_1_Kernel = "SEBS_kb_1.cl";
    private String SEBS_Daily_Evapotranspiration_Kernel = "SEBS_Daily_Evapotranspiration.cl";
    private String SEBS_EnergyBalance_Kernel = "SEBS_EnergyBalance.cl";
    private long[] workGroups;
    private long[] workItems;
    private boolean print = true;
    
    protected long time;

    protected void calcular(float[] fc, float[] LAI, float[] NDVI, float[] LST_K, float[] hc, float[] Zref, float[] Uref, float[] Pref, float[] P0, float[] Ps, float[] Tref_K, float[] qa_ref, float[] Albedo, float[] Emissivity, float[] SWd, float[] LWd, float[] hpbl, float[] SWd24, float[] lat_rad, float[] Ta_av_K, float[] Ns, int[] ComptMask, float[] d0, float[] z0h, float[] z0m, float[] Rn, float[] G0, float[] H, float[] LE, float[] EF, float[] re_i, float[] ustar, float[] H_DL, float[] H_WL, float[] Rndaily, float[] Edaily) {

        float day_angle = 194f;

        List<ParameterGPU> par = new ArrayList<>();

        par.add(new ParameterGPU(ComptMask,true,false,true));
        par.add(new ParameterGPU(fc,true));
        par.add(new ParameterGPU(LAI,true));
        par.add(new ParameterGPU(NDVI,true));
        par.add(new ParameterGPU(LST_K,true));
        par.add(new ParameterGPU(hc,true));
        par.add(new ParameterGPU(Zref,true));
        par.add(new ParameterGPU(Uref,true));
        par.add(new ParameterGPU(Pref,true));
        par.add(new ParameterGPU(P0,true));
        par.add(new ParameterGPU(Ps,true));
        par.add(new ParameterGPU(Tref_K,true));
        par.add(new ParameterGPU(qa_ref,true));

        /* OUTPUT */
        par.add(new ParameterGPU(z0m,true,true));
        par.add(new ParameterGPU(d0,true,true));
        par.add(new ParameterGPU(z0h,true,true));
        /* END OUTPUT */

        int NData = ComptMask.length;
        int[] N = new int[]{NData};
        par.add(new ParameterGPU(N,true));

        calcularGPUKB(par);

        par = new ArrayList<>();
        par.add(new ParameterGPU(ComptMask,true));
        par.add(new ParameterGPU(d0,true,false,true));
        par.add(new ParameterGPU(z0m,true));
        par.add(new ParameterGPU(z0h,true));
        par.add(new ParameterGPU(fc,true));
        par.add(new ParameterGPU(LAI,true));
        par.add(new ParameterGPU(hc,true));
        par.add(new ParameterGPU(Albedo,true));
        par.add(new ParameterGPU(Emissivity,true));
        par.add(new ParameterGPU(LST_K,true));
        par.add(new ParameterGPU(NDVI,true));
        par.add(new ParameterGPU(SWd,true));
        par.add(new ParameterGPU(LWd,true));
        par.add(new ParameterGPU(hpbl,true));
        par.add(new ParameterGPU(Zref,true));
        par.add(new ParameterGPU(Tref_K,true));
        par.add(new ParameterGPU(Uref,true));
        par.add(new ParameterGPU(qa_ref,true));
        par.add(new ParameterGPU(Pref,true));
        par.add(new ParameterGPU(Ps,true));
        par.add(new ParameterGPU(P0,true));

        /* OUTPUT */
        par.add(new ParameterGPU(Rn,true,true));;
        par.add(new ParameterGPU(G0,true,true));
        par.add(new ParameterGPU(H,true,true));
        par.add(new ParameterGPU(LE,true,true));
        par.add(new ParameterGPU(EF,true,true));
        par.add(new ParameterGPU(re_i,true,true));
        par.add(new ParameterGPU(ustar,true,true));
        par.add(new ParameterGPU(H_DL,true,true));
        par.add(new ParameterGPU(H_WL,true,true));
        /* END OUTPUT */

        par.add(new ParameterGPU(new int[]{NData},true));

        calcularGPUEnergyBalance(par);

        par = new ArrayList<>();
        par.add(new ParameterGPU(ComptMask,true));
        par.add(new ParameterGPU(new float[]{day_angle},true));
        par.add(new ParameterGPU(lat_rad,true,false,true));
        par.add(new ParameterGPU(Albedo,true));
        par.add(new ParameterGPU(SWd24,true));
        par.add(new ParameterGPU(Ta_av_K,true));
        par.add(new ParameterGPU(EF,true));
        par.add(new ParameterGPU(Ns,true));

        /* OUTPUT */
        par.add(new ParameterGPU(Rndaily,true,true));
        par.add(new ParameterGPU(Edaily,true,true));
        /* END OUTPUT */

        par.add(new ParameterGPU(new int[]{NData},true));

        calcularGPUDaily(par);

        System.out.println("Total:" + time);
        time = 0;


    }

    public void calcularGPUDaily(List<ParameterGPU> par) {
        try {
            JSeriesCL opencl = new JSeriesCL();
            opencl.setMeasure(true);
            opencl.setPrint(print);
            if (workGroups != null) {
                opencl.setManual(true);
                opencl.setWorkGroups(workGroups);
                opencl.setWorkItems(workItems);
            }
            BufferedReader bur = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/opencl/" + SEBS_Daily_Evapotranspiration_Kernel));
            String line = bur.readLine();
            StringBuilder source = new StringBuilder(line + "\n");
            while (line != null) {
                source.append(line + "\n");
                line = bur.readLine();
            }

            System.out.println("Arq:" + SEBS_Daily_Evapotranspiration_Kernel);
            opencl.setMeasure(true);
            opencl.execute(par, source.toString(), "SEBS_Daily_Evapotranspiration_Kernel");

            time = time + opencl.getMeasures().get(4).getDiference().getTime();
        } catch (IOException ex) {
            Logger.getLogger(SebsOpenCL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void calcularGPUEnergyBalance(List<ParameterGPU> par) {
        try {
            JSeriesCL opencl = new JSeriesCL();
            opencl.setMeasure(true);
            opencl.setPrint(print);
            if (workGroups != null) {
                opencl.setManual(true);
                opencl.setWorkGroups(workGroups);
                opencl.setWorkItems(workItems);
            }
            BufferedReader bur = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/opencl/" + SEBS_EnergyBalance_Kernel));
            String line = bur.readLine();
            StringBuilder source = new StringBuilder(line + "\n");
            while (line != null) {
                source.append(line + "\n");
                line = bur.readLine();
            }

            System.out.println("Arq:" + SEBS_EnergyBalance_Kernel);
            opencl.setMeasure(true);
            opencl.execute(par, source.toString(), "SEBS_EnergyBalance_Kernel");

            time = time + opencl.getMeasures().get(4).getDiference().getTime();
        } catch (IOException ex) {
            Logger.getLogger(SebsOpenCL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void calcularGPUKB(List<ParameterGPU> par) {
        try {
            JSeriesCL opencl = new JSeriesCL();
            opencl.setPrint(print);
            opencl.setMeasure(true);
            if (workGroups != null) {
                opencl.setManual(true);
                opencl.setWorkGroups(workGroups);
                opencl.setWorkItems(workItems);
            }
            BufferedReader bur = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/opencl/" + SEBS_kb_1_Kernel));
            String line = bur.readLine();
            StringBuilder source = new StringBuilder(line + "\n");
            while (line != null) {
                source.append(line + "\n");
                line = bur.readLine();
            }

            System.out.println("Arq:" + SEBS_kb_1_Kernel);
            opencl.setMeasure(true);
            opencl.execute(par, source.toString(), "SEBS_kb_1_Kernel");

            time = time + opencl.getMeasures().get(4).getDiference().getTime();
        } catch (IOException ex) {
            Logger.getLogger(SebsOpenCL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setWorkGroups(long[] workGroups) {
        this.workGroups = workGroups;
    }

    public void setWorkItems(long[] workItems) {
        this.workItems = workItems;
    }
}

