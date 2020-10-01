import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InvalidMidiDataException, MidiUnavailableException {
        //TODO MAKE USER FRIENDLY, REVIEW OTHER TODO
        //TODO MAYBE THINK OF A WAY TO MAKE MULTITHREADED?
        //TODO COULD SPLIT CLIP INTO SEGMENTS 30 SEC LONG. EACH THREAD COULD USE ONE CLIP.
        ArrayList<Integer> noteList = new ArrayList();
        ArrayList<Integer> intensityList = new ArrayList();
        ArrayList<Double> durationList = new ArrayList();
        // NEW STUFF

        ArrayList<Integer> pixelList = new ArrayList<>();
        int tick = 0;

        String filePath = "UntitledProject-short.m4v";
        FFmpegFrameGrabber g = new FFmpegFrameGrabber(filePath);
        g.start();
        int i = 0;
        int length = g.getLengthInFrames();
        ArrayList<BufferedImage> bufferedImageArrayList = new ArrayList<>();
        ArrayList<Frame> frameArrayList = new ArrayList<>();
        double frameRate = g.getFrameRate();
        double frameTime = 1.0 / frameRate;
        double PPQ = (60000.0 / ((1.0 / (frameRate * .001)) * 120.0)); // sets PPQ to what it needs to be to ensure that 1 tick is 1 frame

        double tickTime = 60000 / (120 * PPQ); // length of one tick in ms. Assumes 120 BPM and 200 PPQ
        double ticksPerFrame = (frameTime * 1000.0) / tickTime; //how many ticks per frame of video
        // Stopgap fix Math.round
        ticksPerFrame = Math.round(ticksPerFrame);

        pixelList.add(600);
        pixelList.add(1000);
        pixelList.add(2000);
        pixelList.add(1356);
        pixelList.add(200);


        ArrayList<Integer> rgbList = new ArrayList<>();
        ArrayList<Color> colorList = new ArrayList<>();

        Sequence sequence = new Sequence(Sequence.PPQ, (int) PPQ);
        Track track1 = sequence.createTrack();
        int[] allowedTypes = MidiSystem.getMidiFileTypes(sequence);

        int channel = 0;

        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();

//                    Sequence sequence = new Sequence(Sequence.PPQ, (int) PPQ);
//                    Track track1 = sequence.createTrack();
        ShortMessage sm = new ShortMessage();
        sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, channel, 0);
        track1.add(new MidiEvent(sm, 0));
        Java2DFrameConverter cc = new Java2DFrameConverter();
        BufferedImage bi;

        // TODO SPLIT CLIP INTO SUBCLIPS

        int numOfClips = 4;

        Thread[] threadArray = new Thread[numOfClips];

        for (int s = 0; s < numOfClips; s++) {
            if (s != 0) {
                int place = length / numOfClips;
                g.setFrameNumber(place * s);
            }

            threadArray[s] = new MyThread(s, length, numOfClips, g, pixelList, (int) ticksPerFrame, filePath);
            threadArray[s].start();
        }

        for (int l = 0; l < threadArray.length; l++) {
            try {
                threadArray[l].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int dddd = 0;
        //TODO COMBINE CLIPS

//            if (true) {
//                i = 0;
//                while (i < length / numOfClips) {
//                    try {
//                        rgbList.clear();
//                        noteList.clear();
//                        intensityList.clear();
//                        durationList.clear();
//                        // TODO in a fixed framerate context a list of durations for each note is wasted memory. replace with just ticksPerFrame
////                    Java2DFrameConverter cc = new Java2DFrameConverter();
//                        File f = new File("~/Documents/IdeaProjects/TimelapseSoundtrack/" + System.currentTimeMillis() + ".jpeg");
//
//                        bi = cc.getBufferedImage(g.grab());
//
//                        int debug = g.getFrameNumber();
//
//                        if (bi == null) {
//                            i = 0; // debug catch for null frame
//                        }
//
//                        for (int p = 0; p < pixelList.size(); p++) {
//                            try {
//                                Color myColor = new Color(bi.getRGB(pixelList.get(p), pixelList.get(p)));
//                                int a = myColor.getGreen();
//                                int b = myColor.getBlue();
//                                int c = myColor.getRed();
//                                int d = myColor.getAlpha();
//                                int e = (a + b + c + d) / 4;
//
//                                if (e > 127) {
//                                    e = e % 128;
//                                }
//
//                                rgbList.add(e);
//
//                            } catch (NullPointerException e) {
//                                e.printStackTrace();
//                                i = 0;
//                            }
//                        }
//
////////////////////////////////////////////////////////////////////////
//
//
//                        for (int ii = 0; ii < rgbList.size(); ii++) {
//                            noteList.add(rgbList.get(ii));
//                            durationList.add(ticksPerFrame);
//                            intensityList.add(64);
//                        }
//
//
//                        int noteListLength = noteList.size();
//                        int intensityListLength = intensityList.size();
//                        int durationListLength = durationList.size();
//                        int loopLength;
//
//                        loopLength = (Math.min(noteListLength, Math.min(intensityListLength, durationListLength)));
//
//
//                        noteListLength = noteList.size();
//                        intensityListLength = intensityList.size();
//                        durationListLength = durationList.size();
//
//                        loopLength = (Math.min(noteListLength, Math.min(intensityListLength, durationListLength)));
//
//
//                        try { // CHORD VERSION
//                            // Loop to set midi on/off message locations in the track
//
//                            if ((noteListLength % 4) != 0) {
//                                int z = 0;
//                            }
//
////                        for (int p = 0; p < noteListLength; p += 4) { // TODO NEED TO CHECK FOR NOTELISTLENGTH NOT DIVISIBLE BY NOTESPERCHORD
//                            for (int iiii = 0; iiii < pixelList.size(); iiii++) { //TODO COULD HAVE MINIMALISM IDEA OF REPEATED SEQUENCE PER PHOTO OR AS CHORDS
//
//                                ShortMessage on = new ShortMessage();
//                                on.setMessage(ShortMessage.NOTE_ON, 0, noteList.get(iiii), intensityList.get(iiii));
//                                MidiEvent me1 = new MidiEvent(on, tick);
//                                track1.add(me1);
//                            }
//
//                            tick += (long) ticksPerFrame; // advances time to next frame start
//
//                            for (int z = 0; z < noteListLength; z++) {
//                                ShortMessage off = new ShortMessage();
////                tick += (long) scaleRange.scaleRange(durationList.get(i)); // NEW
////                    tick += durationList.get(i); // out because duration is constant
//                                off.setMessage(ShortMessage.NOTE_OFF, 0, noteList.get(z), intensityList.get(z));
//                                MidiEvent me1 = new MidiEvent(off, tick);
//                                me1 = new MidiEvent(off, tick);
//                                track1.add(me1);
//                            }
////                        }
//                        } catch (InvalidMidiDataException e) {
//                            // JOption Pane
//                            JOptionPane.showMessageDialog(null, "Error! Invalid Midi Data Exception!");
//                            e.printStackTrace();
//                        }
//////////////////////////////////////////////
//                        System.out.println(((((double) i) / (double) length) * 100) + "%");
//                        i++;
//                    } catch (OutOfMemoryError e) {
//                        e.printStackTrace();
//                    }
//                    //System.gc(); //TODO FIGURE OUT GARBAGE COLLECTION FOR MEMORY ISSUE
//
//                }
//
//            } else {
//
//            }

//        MidiSystem.write(sequence, allowedTypes[0], new File("hope this works2 " + "chord.mid"));

//        g.stop();


        // TODO Add Frames to an ArrayList so that we dont have to save images to disk first
        // TODO Could present memory issues
        // TODO Next iteration save frameArrayList object to disk

        ArrayList<File> listOfFiles = new ArrayList<>();
        ArrayList<Sequence> sequenceList = new ArrayList<>();
        ArrayList<Track> trackList = new ArrayList<>();
        ArrayList<MidiEvent> meList = new ArrayList<>();

        for (int f = 0; f < numOfClips; f++) {
            Sequence mySeq = MidiSystem.getSequence(new File("hope this works2 " + f + " chord.mid"));
            sequenceList.add(mySeq);
            Track[] trackArray = sequenceList.get(f).getTracks();
            trackList.add(trackArray[0]);
        }

        for (int y = 0; y < trackList.size(); y++) {
            for (int k = 0; k < trackList.get(y).size(); k++) {
                meList.add(trackList.get(y).get(k));
            }
        }

        Sequence sequence1 = new Sequence(javax.sound.midi.Sequence.PPQ, (int) PPQ);
        Track track2 = sequence1.createTrack();

        sm = new ShortMessage();
        sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, channel, 0);
        track2.add(new MidiEvent(sm, 0));


        int t = 0;
        boolean b = true;
        int counter = -4;
        for (int u = 0; u < meList.size(); u++) { //TODO Might have to look at writing midi events to a file within the threads
            if (counter == 0) {
                t++;
            }
            if ((counter > 0) && (counter % 8 == 0)) {
                t++;
            }
            meList.get(u).setTick(t);
            track2.add(meList.get(u));
            counter++;
        }
        allowedTypes = MidiSystem.getMidiFileTypes(sequence1);
        MidiSystem.write(sequence1, allowedTypes[0], new File("hellYeahLetsSeeIfThisWorked.mid"));

    }
}




