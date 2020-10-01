import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyThread extends Thread {
    int s;
    int numOfClips;
    int overallLength;
    int clipLength;
    int ticksPerFrame;
    FFmpegFrameGrabber g;
    ArrayList<Integer> noteList = new ArrayList();
    ArrayList<Integer> intensityList = new ArrayList();
    ArrayList<Double> durationList = new ArrayList();
    Java2DFrameConverter cc = new Java2DFrameConverter();
    BufferedImage bi;
    ArrayList<Integer> rgbList = new ArrayList<>();
    ArrayList<Integer> pixelList = new ArrayList<>();
    String path;

    public MyThread(int i, int p, int q, FFmpegFrameGrabber h, ArrayList<Integer> listOfPixels, int ticksFrame, String filePath) {
        s = i;
        numOfClips = q;
        overallLength = p;
        clipLength = p / q;
        pixelList = listOfPixels;
        ticksPerFrame = ticksFrame;
        path = filePath;
    }

    public void run() {

        g = new FFmpegFrameGrabber(path);
        try {
            g.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        try {
            g.setFrameNumber(clipLength * s);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        int tick = 0;
        int i = 0;
        double frameRate = g.getFrameRate();
        double frameTime = 1.0 / frameRate;
        double PPQ = (60000.0 / ((1.0 / (frameRate * .001)) * 120.0)); // sets PPQ to what it needs to be to ensure that 1 tick is 1 frame

        double tickTime = 60000 / (120 * PPQ); // length of one tick in ms. Assumes 120 BPM and 200 PPQ
        double ticksPerFrame = (frameTime * 1000.0) / tickTime; //how many ticks per frame of video
        // Stopgap fix Math.round
        ticksPerFrame = Math.round(ticksPerFrame);
        Sequence sequence = null;
        try {
            sequence = new Sequence(Sequence.PPQ, (int) PPQ);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        Track track1 = sequence.createTrack();
        int[] allowedTypes = MidiSystem.getMidiFileTypes(sequence);

        int channel = 0;

        Synthesizer synth = null;
        try {
            synth = MidiSystem.getSynthesizer();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        try {
            synth.open();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }

//                    Sequence sequence = new Sequence(Sequence.PPQ, (int) PPQ);
//                    Track track1 = sequence.createTrack();
        ShortMessage sm = new ShortMessage();
        try {
            sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, channel, 0);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        track1.add(new MidiEvent(sm, 0));
        while (i < clipLength) {
            try {
                rgbList.clear();
                noteList.clear();
                intensityList.clear();
                durationList.clear();
                // TODO in a fixed framerate context a list of durations for each note is wasted memory. replace with just ticksPerFrame
//                    Java2DFrameConverter cc = new Java2DFrameConverter();
                File f = new File("~/Documents/IdeaProjects/TimelapseSoundtrack/" + System.currentTimeMillis() + ".jpeg");

                bi = cc.getBufferedImage(g.grab());

                int debug = g.getFrameNumber();

                if (bi == null) {
                    i = 0; // debug catch for null frame
                }

                for (int p = 0; p < pixelList.size(); p++) {
                    try {
                        Color myColor = new Color(bi.getRGB(pixelList.get(p), pixelList.get(p)));
                        int a = myColor.getGreen();
                        int b = myColor.getBlue();
                        int c = myColor.getRed();
                        int d = myColor.getAlpha();
                        int e = (a + b + c + d) / 4;

                        if (e > 127) {
                            e = e % 128;
                        }

                        rgbList.add(e);

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        i = 0;
                    }
                }

//////////////////////////////////////////////////////////////////////


                for (int ii = 0; ii < rgbList.size(); ii++) {
                    noteList.add(rgbList.get(ii));
                    durationList.add((double) ticksPerFrame);
                    intensityList.add(64);
                }


                int noteListLength = noteList.size();
                int intensityListLength = intensityList.size();
                int durationListLength = durationList.size();
                int loopLength;

                loopLength = (Math.min(noteListLength, Math.min(intensityListLength, durationListLength)));


                noteListLength = noteList.size();
                intensityListLength = intensityList.size();
                durationListLength = durationList.size();

                loopLength = (Math.min(noteListLength, Math.min(intensityListLength, durationListLength)));


                try { // CHORD VERSION
                    // Loop to set midi on/off message locations in the track

                    if ((noteListLength % 4) != 0) {
                        int z = 0;
                    }

//                        for (int p = 0; p < noteListLength; p += 4) { // TODO NEED TO CHECK FOR NOTELISTLENGTH NOT DIVISIBLE BY NOTESPERCHORD
                    for (int iiii = 0; iiii < pixelList.size(); iiii++) { //TODO COULD HAVE MINIMALISM IDEA OF REPEATED SEQUENCE PER PHOTO OR AS CHORDS

                        ShortMessage on = new ShortMessage();
                        on.setMessage(ShortMessage.NOTE_ON, 0, noteList.get(iiii), intensityList.get(iiii));
                        MidiEvent me1 = new MidiEvent(on, tick);
                        track1.add(me1);
                    }

                    tick += (long) ticksPerFrame; // advances time to next frame start

                    for (int z = 0; z < noteListLength; z++) {
                        ShortMessage off = new ShortMessage();
//                tick += (long) scaleRange.scaleRange(durationList.get(i)); // NEW
//                    tick += durationList.get(i); // out because duration is constant
                        off.setMessage(ShortMessage.NOTE_OFF, 0, noteList.get(z), intensityList.get(z));
                        MidiEvent me1 = new MidiEvent(off, tick);
                        me1 = new MidiEvent(off, tick);
                        track1.add(me1);
                    }
//                        }
                } catch (InvalidMidiDataException e) {
                    // JOption Pane
                    JOptionPane.showMessageDialog(null, "Error! Invalid Midi Data Exception!");
                    e.printStackTrace();
                }
////////////////////////////////////////////
                i++;
            } catch (OutOfMemoryError | FrameGrabber.Exception e) {
                e.printStackTrace();
            }
            //System.gc(); //TODO FIGURE OUT GARBAGE COLLECTION FOR MEMORY ISSUE

        }
        try {
            MidiSystem.write(sequence, allowedTypes[0], new File("hope this works2 " + s + " chord.mid"));
            System.out.println(s + " is done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

