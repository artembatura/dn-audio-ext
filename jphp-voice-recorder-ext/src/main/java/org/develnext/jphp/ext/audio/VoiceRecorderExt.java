package org.develnext.jphp.ext.audio;

import org.develnext.jphp.ext.audio.classes.VoiceRecorder;
import php.runtime.ext.support.Extension;
import php.runtime.env.CompileScope;

import java.awt.*;

public class VoiceRecorderExt extends Extension {
    public static final String NS = "php\\audio";

    public VoiceRecorderExt() {
    }

    @Override
    public Status getStatus() {
        return Status.EXPERIMENTAL;
    }

    @Override
    public String[] getPackageNames() {
        return new String[] { "audio" };
    }

    @Override
    public void onRegister(CompileScope scope) {
        registerClass(scope, VoiceRecorder.class);
    }
}