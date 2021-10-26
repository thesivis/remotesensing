
#ifdef cl_khr_fp64
	#pragma OPENCL EXTENSION cl_khr_fp64: enable
#else
	#pragma OPENCL EXTENSION cl_amd_fp64: enable
#endif


#include "./source/Constants.h"

    #define StefanBoltzman 5.67E-8f
    #define K1 607.76f
    #define K2 1260.56f
    #define latitude -16.56f
    #define Kt 1.0f
    #define L 0.1f
    #define Ta 20.53f
    #define julianDay 157.0f
    #define P 99.3f
    #define Tao_24h 0.59930485f
    #define S 1367.0f
    #define Uref 2.24f
    #define width 7947.0f
    #define Rg_24h 181.61319f
    #define Z 39.8911f
    #define reflectanciaAtmosfera 0.03f
    #define UR 68.59f
    #define height 6799.0f

    #define dr 0.9701286f
    #define cosZ 0.6413305f
    #define declinacaoSolar 0.3970275f
    #define anguloHorarioNascerSol 1.4457928f
    #define rad_solar_toa 26.170544f
    #define Rg_24h_mj 15.69138f
    #define transmissividade24h 0.5995817f
    #define ea 1.657021f
    #define W 25.135906f
    #define transmissividade 0.7112397f
    #define emissivityAtm 0.7840079f
    #define SWd 591.093f
    #define LWdAtm 330.6747f

    __constant float parameterAlbedo[] = {0.293f,0.274f,0.233f,0.157f,0.033f,0.0f,0.011f};

    __constant float calibration1[] = {-1.52f,193.0f,1957.0f};
    __constant float calibration2[] = {-2.84f,365.0f,1826.0f};
    __constant float calibration3[] = {-1.17f,264.0f,1554.0f};
    __constant float calibration4[] = {-1.51f,221.0f,1036.0f};
    __constant float calibration5[] = {-0.37f,30.2f,215.0f};
    __constant float calibration6[] = {1.2378f,15.303f,1.0f};
    __constant float calibration7[] = {-0.15f,16.5f,80.67f};

    void execute_sub(
        short pixel1,
        short pixel2,
        short pixel3,
        short pixel4,
        short pixel5,
        short pixel6,
        short pixel7,


        __global float * maxTsVet,
        __global float * minIndexVet,
        __global float * minTsVet,
        __global float * maxIndexVet,
        __global float * rnHotVet,
        __global float * gHotVet,
        __global float * saviHotVet,

        int ind){

        float albedo = 0.0f;
        float NDVI = 0.0f;
        float SAVI = 0.0f;
        float mSAVI = 0.0f;
        float IAF = 0.0f;
        float emissividadeNB = 0.0f;
        float emissivity = 0.0f;
        float Ts = 0.0f;
        float LWd = 0.0f;
        float Rn = 0.0f;
        float G0 = 0.0f;
        float sebta = 0.0f;
        float sumBandas = 0.0f;
        float banda1=calibration1[0]+((calibration1[1]-calibration1[0])/255.0f)*pixel1;

        float banda2=calibration2[0]+((calibration2[1]-calibration2[0])/255.0f)*pixel2;

        float banda3=calibration3[0]+((calibration3[1]-calibration3[0])/255.0f)*pixel3;

        float banda4=calibration4[0]+((calibration4[1]-calibration4[0])/255.0f)*pixel4;

        float banda5=calibration5[0]+((calibration5[1]-calibration5[0])/255.0f)*pixel5;

        float banda6=calibration6[0]+((calibration6[1]-calibration6[0])/255.0f)*pixel6;

        float banda7=calibration7[0]+((calibration7[1]-calibration7[0])/255.0f)*pixel7;

        sumBandas = 0.0f;
        float bandaRefletida1=(pi*banda1)/(calibration1[2]*cosZ*dr);

        sumBandas += parameterAlbedo[0]*bandaRefletida1;
        float bandaRefletida2=(pi*banda2)/(calibration2[2]*cosZ*dr);

        sumBandas += parameterAlbedo[1]*bandaRefletida2;
        float bandaRefletida3=(pi*banda3)/(calibration3[2]*cosZ*dr);

        sumBandas += parameterAlbedo[2]*bandaRefletida3;
        float bandaRefletida4=(pi*banda4)/(calibration4[2]*cosZ*dr);

        sumBandas += parameterAlbedo[3]*bandaRefletida4;
        float bandaRefletida5=(pi*banda5)/(calibration5[2]*cosZ*dr);

        sumBandas += parameterAlbedo[4]*bandaRefletida5;
        float bandaRefletida6=(pi*banda6)/(calibration6[2]*cosZ*dr);

        sumBandas += parameterAlbedo[5]*bandaRefletida6;
        float bandaRefletida7=(pi*banda7)/(calibration7[2]*cosZ*dr);

        sumBandas += parameterAlbedo[6]*bandaRefletida7;
        albedo = (sumBandas-reflectanciaAtmosfera)/(transmissividade*transmissividade);

        NDVI = (bandaRefletida4-bandaRefletida3)/(bandaRefletida4+bandaRefletida3);

        SAVI = ((1.0f+L)*(bandaRefletida4-bandaRefletida3))/(L+bandaRefletida4+bandaRefletida3);

        mSAVI = (0.5f)*((2.0f*bandaRefletida4+1.0f)-sqrt((pow((2.0f*bandaRefletida4+1.0f),2.0f)-8.0f*(bandaRefletida4-bandaRefletida3))));

        IAF = (-log((0.69f-SAVI)/0.59f)/0.91f);

        if(SAVI <= 0.1f ){
            IAF = 0.0f;

        }

        if(SAVI >= 0.687f ){
            IAF = 6.0f;

        }

        emissividadeNB = 0.97f+0.0033f*IAF;

        if(IAF >= 3.0f ){
            emissividadeNB = 0.98f;

        }

        if(NDVI <= 0.0f ){
            emissividadeNB = 0.99f;

        }

        emissivity = 0.95f+0.01f*IAF;

        if(IAF >= 3.0f ){
            emissivity = 0.98f;

        }

        if(NDVI <= 0.0f ){
            emissivity = 0.985f;

        }

        Ts = K2/log(((emissividadeNB*K1)/banda6)+1.0f);

        LWd = emissivity*StefanBoltzman*(pow(Ts,4.0f));

        Rn = ((1.0f-albedo)*SWd)+(emissivity*(LWdAtm)-LWd);

        G0 = Rn*(((Ts-T0)/albedo)*(0.0038f*albedo+0.0074f*albedo*albedo)*(1.0f-0.98f*NDVI*NDVI*NDVI*NDVI));

        sebta = mSAVI;

        if(sebta <= minIndexVet[ind]){
            if(Ts >= maxTsVet[ind]){
                maxTsVet[ind]=Ts;
                minIndexVet[ind]=sebta;
                rnHotVet[ind]=Rn;
                gHotVet[ind]=G0;
                saviHotVet[ind]=SAVI;
            }
        }
        if(sebta >= maxIndexVet[ind]){
            if(Ts <= minTsVet[ind]){
                minTsVet[ind]=Ts;
                maxIndexVet[ind]=sebta;
            }
        }

    }

    __kernel void execute(
        __global short * pixel1,
        __global short * pixel2,
        __global short * pixel3,
        __global short * pixel4,
        __global short * pixel5,
        __global short * pixel6,
        __global short * pixel7,

        __global float * maxTsVet,
        __global float * minIndexVet,
        __global float * minTsVet,
        __global float * maxIndexVet,
        __global float * rnHotVet,
        __global float * gHotVet,
        __global float * saviHotVet,
        __global int * parameters){
        int size = parameters[1];
        int idx = get_global_id(0);
        int ind = idx;
        if(idx < size){
            maxTsVet[ind]=-99999.0f;
            minIndexVet[ind]=99999.0f;
            minTsVet[ind]=99999.0f;
            maxIndexVet[ind]=-99999.0f;
            rnHotVet[ind]=-99999.0f;
            gHotVet[ind]=-99999.0f;
            saviHotVet[ind]=-99999.0f;
            for(int i=0;i<parameters[2];i++){
                idx = ind*parameters[2]+i;
        if(idx < parameters[0] && !(pixel1[idx] == pixel2[idx] && pixel1[idx] == pixel3[idx] && pixel1[idx] == pixel4[idx] && pixel1[idx] == pixel5[idx] && pixel1[idx] == pixel6[idx] && pixel1[idx] == pixel7[idx])){
            execute_sub(
                pixel1[idx],
                pixel2[idx],
                pixel3[idx],
                pixel4[idx],
                pixel5[idx],
                pixel6[idx],
                pixel7[idx],
                maxTsVet,
                minIndexVet,
                minTsVet,
                maxIndexVet,
                rnHotVet,
                gHotVet,
                saviHotVet,
                ind);
        }
            }
        }
    }

