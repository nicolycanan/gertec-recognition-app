package com.gertec.recognition;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Classe auxiliar para carregar e usar modelos TensorFlow Lite.
 */
public class TFLiteHelper {
    private static final String TAG = "TFLiteHelper";
    private Interpreter interpreter;

    public TFLiteHelper(Context context, String modelPath) throws Exception {
        interpreter = new Interpreter(loadModelFile(context, modelPath));
        Log.d(TAG, "Modelo TFLite carregado: " + modelPath);
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
            Log.d(TAG, "Interpreter fechado");
        }
    }
}