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
    public Map<String, float[]> execute(short[] pixel1,short[] pixel2,short[] pixel3,short[] pixel4,short[] pixel5,short[] pixel6,short[] pixel7){

        Map<String, float[]> ret = new HashMap<String, float[]>();

        List<ParameterGPU> par = new ArrayList<ParameterGPU>();

        int[] N = new int[]{pixel1.length};

        par.add(new ParameterGPU(pixel1,true,false,true));
        par.add(new ParameterGPU(pixel2,true));
        par.add(new ParameterGPU(pixel3,true));
        par.add(new ParameterGPU(pixel4,true));
        par.add(new ParameterGPU(pixel5,true));
        par.add(new ParameterGPU(pixel6,true));
        par.add(new ParameterGPU(pixel7,true));

        float[] ET_24h = new float[pixel1.length];
        par.add(new ParameterGPU(ET_24h,true,true));
        ret.put("ET_24h",ET_24h);

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
        return ret;
    }
}

