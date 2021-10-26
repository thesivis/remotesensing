import static br.ufmt.genericseb.Constants.*;
import br.ufmt.genericseb.Constants;
import java.util.HashMap;
import java.util.Map;
import br.ufmt.genericseb.GenericSEB;
import br.ufmt.genericlexerseb.Maths;
import java.util.List;

public class Equation{
    public Map<String, float[]> execute(short[]pixel1,short[]pixel2,short[]pixel3,short[]pixel4,short[]pixel5,short[]pixel6,short[]pixel7){

        Map<String, float[]> ret = new HashMap<String, float[]>();

        float StefanBoltzman = 5.67E-8f;
        float K1 = 607.76f;
        float latitude = -16.56f;
        float K2 = 1260.56f;
        float Kt = 1.0f;
        float L = 0.1f;
        float Ta = 20.53f;
        float julianDay = 157.0f;
        float P = 99.3f;
        float Tao_24h = 0.59930485f;
        float S = 1367.0f;
        float Uref = 2.24f;
        float Rg_24h = 181.61319f;
        float Z = 39.8911f;
        float reflectanciaAtmosfera = 0.03f;
        float UR = 68.59f;

        float[] parameterAlbedo = new float[]{0.2934178f,0.27377668f,0.23299503f,0.15533003f,0.032235477f,0.0f,0.012095051f};

        float[][] calibration = new float[][]{{-1.52f,193.0f,1957.0f}
            ,{-2.84f,365.0f,1826.0f}
            ,{-1.17f,264.0f,1554.0f}
            ,{-1.51f,221.0f,1036.0f}
            ,{-0.37f,30.2f,215.0f}
            ,{1.2378f,15.303f,1.0f}
            ,{-0.15f,16.5f,80.67f}
            };

        float dr = (float)(1.0+0.033*Math.cos(julianDay*2.0*Math.PI/365.0));

        float cosZ = (float)(Math.cos(((90.0-Z)*Math.PI)/180.0));

        float declinacaoSolar = (float)(Math.toRadians(23.45*Math.sin(Math.toRadians(360.0*(julianDay-80.0)/365.0))));

        float anguloHorarioNascerSol = (float)(Math.acos(-Math.tan(Math.PI*latitude/180.0)*Math.tan(declinacaoSolar)));

        float rad_solar_toa = (float)(24.0*60.0*0.082*dr*(anguloHorarioNascerSol*Math.sin(Math.PI*latitude/180.0)*Math.sin(declinacaoSolar)+Math.cos(Math.PI*latitude/180.0)*Math.cos(declinacaoSolar)*Math.sin(anguloHorarioNascerSol))/Math.PI);

        float Rg_24h_mj = (float)(0.0864*Rg_24h);

        float transmissividade24h = (float)(Rg_24h_mj/rad_solar_toa);

        float ea = (float)((0.61078*Math.exp(17.269*Ta/(237.3+Ta)))*UR/100.0);

        float W = (float)(0.14*ea*P+2.1);

        float transmissividade = (float)(0.35+0.627*Math.exp((-0.00146*P/(Kt*cosZ))-0.075*Math.pow((W/cosZ),0.4)));

        float emissivityAtm = (float)(0.625*Math.pow((1000.0*ea/(Ta+T0)),0.131));

        float SWd = (float)((S*cosZ*cosZ)/(1.085*cosZ+10.0*ea*(2.7+cosZ)*0.001+0.2));

        float LWdAtm = (float)(emissivityAtm*StefanBoltzman*(Math.pow(Ta+T0,4.0)));


        float sumBandas = 0.0f;
        float banda1 = 0.0f;
        float banda2 = 0.0f;
        float banda3 = 0.0f;
        float banda4 = 0.0f;
        float banda5 = 0.0f;
        float banda6 = 0.0f;
        float banda7 = 0.0f;
        float bandaRefletida1 = 0.0f;
        float bandaRefletida2 = 0.0f;
        float bandaRefletida3 = 0.0f;
        float bandaRefletida4 = 0.0f;
        float bandaRefletida5 = 0.0f;
        float bandaRefletida6 = 0.0f;
        float bandaRefletida7 = 0.0f;
        float indexMax, indexMin;
        float RnHot = 0.0f, GHot = 0.0f;
        float SAVI_hot = 0.0f;

        indexMax = 0.0f;

        indexMin = Float.MAX_VALUE;
        float[] albedo = new float[pixel7.length];
        ret.put("albedo",albedo);

        float[] NDVI = new float[pixel7.length];
        ret.put("NDVI",NDVI);

        float[] SAVI = new float[pixel7.length];
        ret.put("SAVI",SAVI);

        float[] IAF = new float[pixel7.length];
        ret.put("IAF",IAF);

        float[] emissividadeNB = new float[pixel7.length];
        ret.put("emissividadeNB",emissividadeNB);

        float[] emissivity = new float[pixel7.length];
        ret.put("emissivity",emissivity);

        float[] Ts = new float[pixel7.length];
        ret.put("Ts",Ts);

        float[] LWd = new float[pixel7.length];
        ret.put("LWd",LWd);

        float[] Rn = new float[pixel7.length];
        ret.put("Rn",Rn);

        float[] G0 = new float[pixel7.length];
        ret.put("G0",G0);

        float sebal = 0;
        for(int i = 0;i < pixel7.length;i++){
            if(!(pixel1[i] == pixel2[i] && pixel1[i] == pixel3[i] && pixel1[i] == pixel4[i] && pixel1[i] == pixel5[i] && pixel1[i] == pixel6[i] && pixel1[i] == pixel7[i])){
                banda1= (float)(calibration[0][0]+((calibration[0][1]-calibration[0][0])/255.0)*pixel1[i]);

                banda2= (float)(calibration[1][0]+((calibration[1][1]-calibration[1][0])/255.0)*pixel2[i]);

                banda3= (float)(calibration[2][0]+((calibration[2][1]-calibration[2][0])/255.0)*pixel3[i]);

                banda4= (float)(calibration[3][0]+((calibration[3][1]-calibration[3][0])/255.0)*pixel4[i]);

                banda5= (float)(calibration[4][0]+((calibration[4][1]-calibration[4][0])/255.0)*pixel5[i]);

                banda6= (float)(calibration[5][0]+((calibration[5][1]-calibration[5][0])/255.0)*pixel6[i]);

                banda7= (float)(calibration[6][0]+((calibration[6][1]-calibration[6][0])/255.0)*pixel7[i]);

                sumBandas = 0.0f;
                bandaRefletida1= (float)((Math.PI*banda1)/(calibration[0][2]*cosZ*dr));

                sumBandas += parameterAlbedo[0]*bandaRefletida1;
                bandaRefletida2= (float)((Math.PI*banda2)/(calibration[1][2]*cosZ*dr));

                sumBandas += parameterAlbedo[1]*bandaRefletida2;
                bandaRefletida3= (float)((Math.PI*banda3)/(calibration[2][2]*cosZ*dr));

                sumBandas += parameterAlbedo[2]*bandaRefletida3;
                bandaRefletida4= (float)((Math.PI*banda4)/(calibration[3][2]*cosZ*dr));

                sumBandas += parameterAlbedo[3]*bandaRefletida4;
                bandaRefletida5= (float)((Math.PI*banda5)/(calibration[4][2]*cosZ*dr));

                sumBandas += parameterAlbedo[4]*bandaRefletida5;
                bandaRefletida6= (float)((Math.PI*banda6)/(calibration[5][2]*cosZ*dr));

                sumBandas += parameterAlbedo[5]*bandaRefletida6;
                bandaRefletida7= (float)((Math.PI*banda7)/(calibration[6][2]*cosZ*dr));

                sumBandas += parameterAlbedo[6]*bandaRefletida7;
                albedo[i] = (float)((sumBandas-reflectanciaAtmosfera)/(transmissividade*transmissividade));

                NDVI[i] = (float)((bandaRefletida4-bandaRefletida3)/(bandaRefletida4+bandaRefletida3));

                SAVI[i] = (float)(((1.0+L)*(bandaRefletida4-bandaRefletida3))/(L+bandaRefletida4+bandaRefletida3));

                IAF[i] = (float)((-Math.log((0.69-SAVI[i])/0.59)/0.91));

            if(SAVI[i] <= 0.1f ){
                    IAF[i] = (float)(0.0);

            }

            if(SAVI[i] >= 0.687f ){
                    IAF[i] = (float)(6.0);

            }

                emissividadeNB[i] = (float)(0.97+0.0033*IAF[i]);

            if(IAF[i] >= 3.0f ){
                    emissividadeNB[i] = (float)(0.98);

            }

            if(NDVI[i] <= 0.0f ){
                    emissividadeNB[i] = (float)(0.99);

            }

                emissivity[i] = (float)(0.95+0.01*IAF[i]);

            if(IAF[i] >= 3.0f ){
                    emissivity[i] = (float)(0.98);

            }

            if(NDVI[i] <= 0.0f ){
                    emissivity[i] = (float)(0.985);

            }

                Ts[i] = (float)(K2/Math.log(((emissividadeNB[i]*K1)/banda6)+1.0));

                LWd[i] = (float)(emissivity[i]*StefanBoltzman*(Math.pow(Ts[i],4.0)));

                Rn[i] = (float)(((1.0-albedo[i])*SWd)+(emissivity[i]*(LWdAtm)-LWd[i]));

                G0[i] = (float)(Rn[i]*(((Ts[i]-T0)/albedo[i])*(0.0038*albedo[i]+0.0074*albedo[i]*albedo[i])*(1.0-0.98*Math.pow(NDVI[i],4.0))));

                sebal = (float)((0.5)*((2.0*bandaRefletida4+1.0)-Math.sqrt((Math.pow((2.0*bandaRefletida4+1.0),2.0)-8.0*(bandaRefletida4-bandaRefletida3)))));

            if (sebal >= indexMax) {
                indexMax = sebal;
                RnHot = Rn[i];
                SAVI_hot = SAVI[i];
                GHot = G0[i];
            } else if (sebal <= indexMin) {
                indexMin = sebal;
            }
            }
        }
        float[] coef = new float[2];
        GenericSEB.calculaAB(coef, RnHot, GHot, Uref, SAVI_hot, indexMax, indexMin);
        ret.put("coef",coef);

        return ret;
    }
}

