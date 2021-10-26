/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufmt.runs;

import br.ufmt.genericlexerseb.LanguageType;
import br.ufmt.preprocessing.ProcessorTiff;
import br.ufmt.preprocessing.exceptions.TiffErrorBandsException;
import br.ufmt.preprocessing.exceptions.TiffNotFoundException;
import br.ufmt.preprocessing.utils.DataFile;
import br.ufmt.screen.SEBALLandSat5JPanel;
import br.ufmt.util.Util;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author raphael
 */
public class SEBALRun implements Runnable {

    private File file;
    private java.awt.Frame parent;
    private String forVariable;
    private String forEachValue;

    @Override
    public void run() {

        String path = file.getPath();
        File tiff = file;
        if (tiff.exists() && tiff.getName().endsWith(".tif")) {
            long time = System.currentTimeMillis();

//                SeekableStream s = null;
            try {
                List<DataFile> ret = new ArrayList<DataFile>();
//                    System.out.println("Arq:" + tiff.getName());
                SeekableStream s = new FileSeekableStream(tiff);
                TIFFDecodeParam param = null;
                ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
                // Which of the multiple images in the TIFF file do we want to load
                // 0 refers to the first, 1 to the second and so on.
                int bands;
                Raster raster = dec.decodeAsRaster(0);
                bands = raster.getNumBands();
                int width = raster.getWidth();
                int height = raster.getHeight();
                int tam = width * height;

                if (bands == 7) {

                    float[][] calibration = new float[][]{
                        {-1.52f, 193.0f, 1957.0f},
                        {-2.84f, 365.0f, 1826.0f},
                        {-1.17f, 264.0f, 1554.0f},
                        {-1.51f, 221.0f, 1036.0f},
                        {-0.37f, 30.2f, 215.0f},
                        {1.2378f, 15.303f, 1.0f},
                        {-0.15f, 16.5f, 80.67f}};

//                        float[] parameterAlbedo = new float[]{0.293f, 0.274f, 0.233f, 0.157f, 0.033f, 0.0f, 0.011f};
                    float[] parameterAlbedo = new float[7];

                    float sum = 0;
                    for (int i = 0; i < calibration.length; i++) {
                        sum += calibration[i][2];
                    }
                    for (int i = 0; i < parameterAlbedo.length; i++) {
                        parameterAlbedo[i] = calibration[i][2] / sum;
                    }
                    parameterAlbedo[5] = 0;

                    String[] nameParameters = new String[]{"pixel1", "pixel2", "pixel3", "pixel4", "pixel5", "pixel6", "pixel7"};

                    Map<String, float[][]> constMatrix = new HashMap<String, float[][]>();
                    constMatrix.put("calibration", calibration);

                    Map<String, float[]> constVetor = new HashMap<String, float[]>();
                    constVetor.put("parameterAlbedo", parameterAlbedo);

                    try {
                        ProcessorTiff processorTiff = new ProcessorTiff(LanguageType.JAVA);
//                        ret = processorTiff.execute(forVariable, forEachValue, path, nameParameters, variables, constVetor, constMatrix);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(parent, ex.getMessage());
                    }

                    System.out.println("End");
                } else {
                    JOptionPane.showMessageDialog(parent, new TiffErrorBandsException().getMessage());
                }

                s.close();
                time = System.currentTimeMillis() - time;
                System.out.println("Tempo:" + time);
                JOptionPane.showMessageDialog(parent, Util.getMessage("execution.sucess"));

            } catch (IOException ex) {
                Logger.getLogger(SEBALRun.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {

            JOptionPane.showMessageDialog(parent, new TiffNotFoundException().getMessage());
        }

    }

}
