#include "Constants.h"

    __device__ float X(float Zref_m, float L) {
        return (float) (sqrtf(sqrtf((1.0f - 16.0f * Zref_m / L))));
    }

    __device__ float Psim(float L) {
        if (L < 0.0f) {
            /* unstable */
            float x200 = X(200, L);
            return (float) (2.0f * logf((1.0f + x200) / 2.0f) + logf((1.0f + x200 * x200) / (2.0f)) - 2.0f * atanf(x200) + 0.5f * pi);
        } else if (L > 0.0f) {
            /* stable */
            return (-5 * (2 / L));
        } else {
            return (0);
        }
    }

    __device__ float Psih(float Zref_h, float L) {
        if (L < 0.0f) {
            /* unstable */
            float x = X(Zref_h, L);
            return (float) (2.0f * logf((1.0f + x * x) / 2.0f));
        } else if (L > 0.0f) {
            /* stable */
            return (-5 * (2 / L));
        } else {
            return (0);
        }
    }

extern "C"{

    #define K1 607.76f
    #define K2 1260.56f
    #define Kt 1.0f
    #define L 0.1f
    #define Uref 2.24f
    #define indexMin 203.69563f
    #define height 6797.0f
    #define P 99.9f
    #define Rg_24h 204.60323f
    #define S 1367.0f
    #define RnHot 499.16528f
    #define h_veg 28.0f
    #define GHot 89.82469f
    #define Z 50.4362f
    #define UR 78.79f
    #define Tao_24h 0.53428197f
    #define U_z 32.0f
    #define reflectanciaAtmosfera 0.03f
    #define StefanBoltzman 5.67E-8f
    #define width 7989.0f
    #define julianDay 101.0f
    #define Ta 29.2f
    #define SAVI_hot 0.097473025f
    #define latitude -16.56f
    #define indexMax 311.61884f

    #define dr 0.9944873f
    #define cosZ 0.7709158f
    #define declinacaoSolar 0.14475246f
    #define anguloHorarioNascerSol 1.527437f
    #define rad_solar_toa 33.07308f
    #define Rg_24h_mj 17.67772f
    #define transmissividade24h 0.5345048f
    #define ea 3.1922805f
    #define W 46.747234f
    #define transmissividade 0.702253f
    #define emissivityAtm 0.8510801f
    #define SWd 708.1516f
    #define LWdAtm 403.26764f

    __constant__ float parameterAlbedo[] = {0.2934178f,0.27377668f,0.23299503f,0.15533003f,0.032235477f,0.0f,0.012095051f};

    __constant__ float calibration1[] = {-1.52f,193.0f,1957.0f};
    __constant__ float calibration2[] = {-2.84f,365.0f,1826.0f};
    __constant__ float calibration3[] = {-1.17f,264.0f,1554.0f};
    __constant__ float calibration4[] = {-1.51f,221.0f,1036.0f};
    __constant__ float calibration5[] = {-0.37f,30.2f,215.0f};
    __constant__ float calibration6[] = {1.2378f,15.303f,1.0f};
    __constant__ float calibration7[] = {-0.15f,16.5f,80.67f};

    __device__ void execute_sub(
        short pixel1,
        short pixel2,
        short pixel3,
        short pixel4,
        short pixel5,
        short pixel6,
        short pixel7,

        float * ET_24h    ){

        float z0m = 0.0f;
        float U_star = 0.0f;
        float H = 0.0f;
        float r_ah = 0.0f;
        float albedo = 0.0f;
        float NDVI = 0.0f;
        float SAVI = 0.0f;
        float IAF = 0.0f;
        float emissividadeNB = 0.0f;
        float emissivity = 0.0f;
        float Ts = 0.0f;
        float LWd = 0.0f;
        float Rn = 0.0f;
        float G0 = 0.0f;
        float LE = 0.0f;
        float evap_fr = 0.0f;
        float Rn_24h = 0.0f;
        float LE_24h = 0.0f;
            z0m = 0.0f;

            U_star = 0.0f;

            H = 0.0f;

            r_ah = 0.0f;

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

                float constZ=0.12f;
                float z0=constZ*h_veg;
                
                float Ustar = (float) (Uref*k/logf(U_z/z0));
                float U200 = (float) (Ustar*logf(z200 / z0)/k);
                z0m = (float) expf(-5.809f + 5.62f * SAVI);
                U_star = (float) (k * U200 / logf(z200 / z0m));
                r_ah = (float) (logf(z2 / z1) / (U_star * k));

                float LHot = 0.0f;
                float tm_200Hot = 0.0f;
                float th_2Hot = 0.0f;
                float th_0_1Hot = 0.0f;

                float LPixel = 0.0f;
                float tm_200Pixel = 0.0f;
                float th_2Pixel = 0.0f;
                float th_0_1Pixel = 0.0f;

                float HHot = RnHot - GHot;
                float a = 0.0f;
                float b = 0.0f;
                float errorH = 10.0f;
                float r_ah_anteriorHot = 0.0f;
                int step = 1;
                float z0mHot = (float) expf(-5.809f + 5.62f * SAVI_hot);
                float U_starHot = (float) (k * U200 / logf(z200 / z0mHot));
                float r_ahHot = (float) (logf(z2 / z1) / (U_starHot * k));
                while (errorH > MaxAllowedError && step < 15) {

                    a = ((HHot) * r_ahHot) / (p * cp * (indexMax - indexMin));
                    b = -a * (indexMin - T0);

                    //PARTE DO PIXEL QUENTE
                    HHot = p * cp * (b + a * (indexMax - T0)) / r_ahHot;
                    LHot = (float) (-(p * cp * U_starHot * U_starHot * U_starHot * (indexMax)) / (k * g * HHot));

                    tm_200Hot = Psim(LHot);
                    th_2Hot = Psih(z2, LHot);
                    th_0_1Hot = Psih(z1, LHot);

                    U_starHot = (float) (k * U200 / (logf(z200 / z0mHot) - tm_200Hot));
                    r_ah_anteriorHot = r_ahHot;
                    r_ahHot = (float) ((logf(z2 / z1) - th_2Hot + th_0_1Hot) / (U_starHot * k));

                    //PARTE DE CADA PIXEL
                    H = p * cp * (b + a * (Ts - T0)) / r_ah;
                    LPixel = (float) (-(p * cp * U_star * U_star * U_star * (Ts)) / (k * g * H));

                    tm_200Pixel = Psim(LPixel);
                    th_2Pixel = Psih(z2, LPixel);
                    th_0_1Pixel = Psih(z1, LPixel);

                    U_star = (float) (k * U200 / (logf(z200 / z0m) - tm_200Pixel));
                    r_ah = (float) ((logf(z2 / z1) - th_2Pixel + th_0_1Pixel) / (U_star * k));

                    errorH = fabsf(((r_ahHot - r_ah_anteriorHot) * 100) / r_ahHot);

                    step++;
                }

            LWd = emissivity*StefanBoltzman*(pow(Ts,4.0f));

            Rn = ((1.0f-albedo)*SWd)+(emissivity*(LWdAtm)-LWd);

            G0 = Rn*(((Ts-T0)/albedo)*(0.0038f*albedo+0.0074f*albedo*albedo)*(1.0f-0.98f*pow(NDVI,4.0f)));

            LE = Rn-H-G0;

            evap_fr = LE/(Rn-G0);

            Rn_24h = Rg_24h*(1.0f-albedo)-110.0f*Tao_24h;

            LE_24h = evap_fr*Rn_24h;

            *ET_24h = (evap_fr*Rn_24h*86.4f)/2450.0f;

    }

    __global__ void execute(
        short * pixel1,
        short * pixel2,
        short * pixel3,
        short * pixel4,
        short * pixel5,
        short * pixel6,
        short * pixel7,

        float * ET_24h,
        int * parameters){
        int size = 54301233;
        int idx = blockIdx.x*blockDim.x + threadIdx.x;
        int ind = idx;
        if(idx < size){
            if(idx < 54301233 && !(pixel1[idx] == pixel2[idx] && pixel1[idx] == pixel3[idx] && pixel1[idx] == pixel4[idx] && pixel1[idx] == pixel5[idx] && pixel1[idx] == pixel6[idx] && pixel1[idx] == pixel7[idx])){
                execute_sub(
                    pixel1[idx],
                    pixel2[idx],
                    pixel3[idx],
                    pixel4[idx],
                    pixel5[idx],
                    pixel6[idx],
                    pixel7[idx],
                    (ET_24h+idx)                );
            }
        }
    }
}


