package jviz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author David Stefan
 */
public class Dot {

    public Dot() {

    }

    public void png(String dot, String outPath) throws JvizException {

        Process process;

        try {
            // Start dot process
            process = new ProcessBuilder("dot", "-Tpng").start();
        } catch (IOException e) {
            throw new JvizException("Dot process could not be started.");
        }

        InputStream in = process.getInputStream();
        BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        OutputStream out = process.getOutputStream();

        try {
            // Write dot code into the stream
            out.write(dot.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new JvizException("Writing into dot caused the following exception: " + e.getMessage());
        }

        // Save stdout into given filepath
        FileOutputStream fileOut;

        try {
            fileOut = new FileOutputStream(new File(outPath));
            int c;
            while ((c = in.read()) != -1) {
                fileOut.write(c);
            }
            fileOut.close();
            in.close();
        } catch (IOException e) {
            throw new JvizException(e.getMessage());
        }

        // ...or throw exception with thrown error
        String errLine;
        String error = null;

        try {
            while ((errLine = err.readLine()) != null) {
                error = errLine;
            }
            if (error != null) {
                in.close();
                err.close();
                throw new JvizException(error);
            }
        } catch (IOException e) {
            throw new JvizException(e.getMessage());
        }
    }
}
