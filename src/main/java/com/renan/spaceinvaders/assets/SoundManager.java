package com.renan.spaceinvaders.assets;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class SoundManager {

    private static final class Sample {
        final AudioFormat format;
        final byte[] data;

        Sample(AudioFormat format, byte[] data) {
            this.format = format;
            this.data = data;
        }
    }

    private final Map<String, Sample> samples = new HashMap<>();
    private Clip musicClip;
    private boolean muted;

    public SoundManager() {
        load("music", "Sound/metroid.wav");
        load("shot", "Sound/laser2.wav");
        load("alien_died", "Sound/died.wav");
        load("alien_shot", "Sound/alienShot.wav");
        load("player_died", "Sound/shot.wav");
    }

    private void load(String name, String resourcePath) {
        try (InputStream raw = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (raw == null) {
                System.err.println("Missing sound resource: " + resourcePath);
                return;
            }
            try (AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(raw))) {
                AudioFormat format = stream.getFormat();
                byte[] data = stream.readAllBytes();
                samples.put(name, new Sample(format, data));
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("Failed loading sound " + name + ": " + e.getMessage());
        }
    }

    public void play(String name) {
        if (muted) return;
        Sample sample = samples.get(name);
        if (sample == null) return;
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(sample.format, sample.data, 0, sample.data.length);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.start();
        } catch (Exception ignored) {
            // No audio line available (headless / busy). Drop the sound silently.
        }
    }

    public void playMusic(String name) {
        if (muted) return;
        Sample sample = samples.get(name);
        if (sample == null) return;
        stopMusic();
        try {
            musicClip = AudioSystem.getClip();
            musicClip.open(sample.format, sample.data, 0, sample.data.length);
            applyGain(musicClip, -6f);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            musicClip = null;
        }
    }

    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void toggleMute() {
        muted = !muted;
        if (muted) {
            stopMusic();
        } else {
            playMusic("music");
        }
    }

    private static void applyGain(Clip clip, float db) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
        }
    }
}
