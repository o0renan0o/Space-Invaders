package Sonda;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sounds {
    private AudioClip music = Applet.newAudioClip(getClass().getClassLoader().getResource("Sound/metroid.wav"));
    private AudioClip shot = Applet.newAudioClip(getClass().getClassLoader().getResource("Sound/laser2.wav"));
    private AudioClip alienDied = Applet.newAudioClip(getClass().getClassLoader().getResource("Sound/died.wav"));
    private AudioClip alienShot = Applet.newAudioClip(getClass().getClassLoader().getResource("Sound/alienShot.wav"));


    public void playMusic() {
        music.loop();
    }

    public void shot() {
        shot.play();
    }

    public void alienDied() {
        alienDied.play();
    }

    public void alienShot() {
        alienShot.play();
    }

}
