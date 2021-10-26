import static br.ufmt.genericseb.Constants.*;
import br.ufmt.genericseb.Constants;
import java.util.HashMap;
import java.util.Map;
import br.ufmt.jedigpu.ParameterGPU;
import java.util.ArrayList;
import br.ufmt.jedigpu.JSeriesCUDA;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.ufmt.genericseb.GenericSEB;
import br.ufmt.genericlexerseb.Maths;
import java.util.List;

public class Equation{
    public Map<String, float[]> execute(float[] pixel1,float[] pixel2,float[] pixel3,float[] pixel4,float[] pixel5,float[] pixel6,float[] pixel7){

        float Uref = 1.63f;
        int width = (int)1000.0f;
        int height = (int)1000.0f;
        int col = 3;
        int line = 5000000/col;
        int w = 40*col;

        int total = width * height;

        for (int i = w; i >= 1; i--) {
            if (total % i == 0 && (total / i) < line) {
                height = i;
                width = total / i;
                break;
            }
        }
        Map<String, float[]> ret = new HashMap<String, float[]>();

        List<ParameterGPU> par = new ArrayList<ParameterGPU>();

        int[] N = new int[]{pixel1.length, width, height};

        par.add(new ParameterGPU(pixel1,true));
        par.add(new ParameterGPU(pixel2,true));
        par.add(new ParameterGPU(pixel3,true));
        par.add(new ParameterGPU(pixel4,true));
        par.add(new ParameterGPU(pixel5,true));
        par.add(new ParameterGPU(pixel6,true));
        par.add(new ParameterGPU(pixel7,true));

        float[] maxTsVet = new float[width];

        par.add(new ParameterGPU(maxTsVet, true, true, true));

        float[] minIndexVet = new float[width];
        par.add(new ParameterGPU(minIndexVet, true, true));

        float[] minTsVet = new float[width];
        par.add(new ParameterGPU(minTsVet, true, true));

        float[] maxIndexVet = new float[width];
        par.add(new ParameterGPU(maxIndexVet, true, true));

        float[] rnHotVet = new float[width];
        par.add(new ParameterGPU(rnHotVet, true, true));

        float[] gHotVet = new float[width];
        par.add(new ParameterGPU(gHotVet, true, true));

        float[] saviHotVet = new float[width];
        par.add(new ParameterGPU(saviHotVet, true, true));

        par.add(new ParameterGPU(N,true,false,false,false));

        String pathNvcc = "/usr/local/cuda/bin/";
        String source = "code.cu";
        try {
            JSeriesCUDA cuda = new JSeriesCUDA();
            cuda.setPathNvcc(pathNvcc);
            cuda.execute(par, System.getProperty("user.dir") + "/source/" + source, "execute");
            File newFile = new File(System.getProperty("user.dir") + "/source/" + source);
            //newFile.delete();
        } catch (IOException ex) {
            Logger.getLogger(Equation.class.getName()).log(Level.SEVERE, null, ex);
        }
        float maxIndex = maxIndexVet[0];
        float minIndex = minIndexVet[0];
        float rnHot = rnHotVet[0];
        float gHot = gHotVet[0];
        float saviHot = saviHotVet[0];
        float maxTs = maxTsVet[0];
        float minTs = minTsVet[0];
        for (int i = 1; i < maxTsVet.length; i++) {
            if (minIndexVet[i] <= minIndex) {
                if (maxTsVet[i] >= maxTs) {
                    maxTs = maxTsVet[i];
                    minIndex = minIndexVet[i];
                    rnHot = rnHotVet[i];
                    gHot = gHotVet[i];
                    saviHot = saviHotVet[i];
                }
            }

            if (maxIndexVet[i] >= maxIndex) {
                if (minTsVet[i] <= minTs) {
                    minTs = minTsVet[i];
                    maxIndex = maxIndexVet[i];
                }
            }
        }
        ret.put("RnHot",new float[]{rnHot});

        ret.put("GHot",new float[]{gHot});

        ret.put("SAVI_hot",new float[]{saviHot});

        ret.put("indexMax",new float[]{maxTs});

        ret.put("indexMin",new float[]{minTs});

        return ret;
    }
}

