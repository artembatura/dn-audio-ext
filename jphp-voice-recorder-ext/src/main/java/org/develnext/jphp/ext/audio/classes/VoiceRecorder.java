package org.develnext.jphp.ext.audio.classes;


import org.develnext.jphp.ext.audio.VoiceRecorderExt;
import php.runtime.Memory;
import php.runtime.annotation.Reflection;
import php.runtime.annotation.Reflection.*;
import php.runtime.env.Environment;
import php.runtime.ext.core.classes.lib.ItemsUtils;
import php.runtime.ext.core.classes.stream.FileObject;
import php.runtime.ext.core.classes.stream.MiscStream;
import php.runtime.ext.core.classes.stream.Stream;
import php.runtime.invoke.Invoker;
import php.runtime.lang.BaseObject;
import php.runtime.lang.ForeachIterator;
import php.runtime.memory.ArrayMemory;
import php.runtime.memory.ReferenceMemory;
import php.runtime.memory.StringMemory;
import php.runtime.reflection.ClassEntity;

import java.awt.*;
import java.util.*;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Objects;

@Name("VoiceRecorder")
@Namespace(VoiceRecorderExt.NS)
public class VoiceRecorder extends BaseObject {
    // настройки записи
    AudioFormat.Encoding encoding;
    float sampleRate, frameRate;
    int sampleSizeInBits, channels, frameSize;
    boolean bigEndian;
    // максимальное записываемое время(-1 без ограничений)
    int maxRecordTime = -1;
    Thread audioSystemThreadWriter, breaker;
    // тип сохраняемого файла
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    private AudioInputStream aInputStream;
    private boolean started = false;
    private TargetDataLine line;
    private Callback onStop;

    public VoiceRecorder(Environment env, ClassEntity clazz) {
        super(env, clazz);
    }

    @Signature
    public void __construct() {
        encoding = AudioFormat.Encoding.PCM_SIGNED;
        sampleRate = 44100.0F;
        sampleSizeInBits = 16;
        channels = 2;
        frameSize = 4;
        frameRate = 44100.0F;
        bigEndian = false;
    }

    @Signature
    public void start(String pathToSaveingFile) {
        try {
            File savingFile = new File(pathToSaveingFile);
            File finalSavingFile = Objects.equals(getFileExtension(savingFile), "")
                    ? new File(savingFile.getPath() + ".wav") : savingFile;
            AudioFormat aFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits,
                    channels, frameSize, frameRate, bigEndian);
            DataLine.Info dLInfo = new DataLine.Info(TargetDataLine.class, aFormat);

            if (!AudioSystem.isLineSupported(dLInfo)) {
                System.out.println("Line not supported");
                System.exit(0);
            }

            line = (TargetDataLine) AudioSystem.getLine(dLInfo);
            line.open(aFormat);
            line.start();

            audioSystemThreadWriter = new Thread(() -> {
                try {
                    aInputStream = new AudioInputStream(line);
                    System.out.println("Recording started");
                    AudioSystem.write(aInputStream, fileType, finalSavingFile);
                    System.out.println("Recording stopped");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            audioSystemThreadWriter.start();
            started = true;
			if (this.maxRecordTime == -1) {
				return;
			}
            breaker = new Thread(() -> {
                try {
                    Thread.sleep(maxRecordTime);
                    if (!Thread.interrupted() && started) {
                        System.out.println("Out of max time(" + maxRecordTime + "ms)");
                        stop();
                    }
                } catch (InterruptedException e) {
                    System.out.println("Breaker sleeping aborted");
                }
            });
            breaker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Signature
    public void stop() {
        try {
            if (started) {
                line.stop();
                line.close();
                aInputStream.close();
                if (breaker != null) {
                    breaker.interrupt();
                }
                if (onStop != null) {
                    onStop.call();
                }
                started = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Getter
	public int getMaxRecordTime() {
		return this.maxRecordTime;
	}
	
	@Setter
	public void setMaxRecordTime(int ms) {
		this.maxRecordTime = ms;
	}

    private String getFileExtension(File file) {
        String fileName = file.getName();
        // если в имени файла есть точка и она не является первым символом в названии файла
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            // то вырезаем все знаки после последней точки в названии файла, то есть ХХХХХ.txt -> txt
            return fileName.substring(fileName.lastIndexOf(".") + 1);
            // в противном случае возвращаем заглушку, то есть расширение не найдено
        else return "";
    }

    @FunctionalInterface
    public interface Callback {
        void call();
    }

    public void onStop(Callback callback)
    {
        onStop = callback;
    }
}
