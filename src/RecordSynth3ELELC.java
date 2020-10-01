/**
 * Author: Alex Worland
 * Date: 2/26/16
 * Description: CS111 Project 2
 */
import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecordSynth3ELELC {

    public static void Synthesizer(ArrayList<Integer> RGB, double frameRate, double frameTime, int notesPerChord) throws InvalidMidiDataException,
            IOException, MidiUnavailableException {

        ArrayList<Integer> noteList = new ArrayList();
        ArrayList<Integer> intensityList = new ArrayList();
        ArrayList<Double> durationList = new ArrayList(); // TODO in a fixed framerate context a list of durations for each note is wasted memory. replace with just ticksPerFrame
        // NEW STUFF

        double PPQ = (60000.0 / ((1.0/(frameRate*.001)) * 120.0)); // sets PPQ to what it needs to be to ensure that 1 tick is 1 frame

        double tickTime = 60000 / (120 * PPQ); // length of one tick in ms. Assumes 120 BPM and 200 PPQ
        double ticksPerFrame = (frameTime * 1000.0) / tickTime; //how many ticks per frame of video

        // TODO account for decimal ticksPerFrame and Ticks being of type long
        // TODO Revise this segment. ticksPerFrame no longer necessary but good to keep just in case
        // Stopgap fix Math.round
        ticksPerFrame = Math.round(ticksPerFrame);

        for (int i = 0; i < RGB.size(); i++) {
            noteList.add(RGB.get(i));
            durationList.add(ticksPerFrame); // TODO WILL NEED TO CHANGE TO SYNC VIDEO FRAMERATE
            intensityList.add(64);
        }

        // set midi instrument
        int channel = 0;

        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();

        Sequence sequence = new Sequence(Sequence.PPQ, (int) PPQ);
        Track track1 = sequence.createTrack();
        ShortMessage sm = new ShortMessage();
        sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, channel, 0);
        track1.add(new MidiEvent(sm, 0));

        long tick = 0;



        int noteListLength = noteList.size();
        int intensityListLength = intensityList.size();
        int durationListLength = durationList.size();
        int loopLength;

        loopLength = (Math.min(noteListLength,Math.min(intensityListLength, durationListLength)));


        try { // MINIMALISM VERSION
            // Loop to set midi on/off message locations in the track
            for (int i = 0; i < noteListLength; i++) { //TODO COULD HAVE MINIMALISM IDEA OF REPEATED SEQUENCE PER PHOTO OR AS CHORDS

                ShortMessage on = new ShortMessage();
                on.setMessage(ShortMessage.NOTE_ON, 0, noteList.get(i), intensityList.get(i));
                MidiEvent me1 = new MidiEvent(on, tick);
                track1.add(me1);

                ShortMessage off = new ShortMessage();
//                tick += (long) scaleRange.scaleRange(durationList.get(i)); // NEW
                tick += durationList.get(i);
                off.setMessage(ShortMessage.NOTE_OFF, 0, noteList.get(i), intensityList.get(i));
                me1 = new MidiEvent(off, tick);
                track1.add(me1);

            }
        } catch ( InvalidMidiDataException e) {
            // JOption Pane
            JOptionPane.showMessageDialog(null, "Error! Invalid Midi Data Exception!");
            e.printStackTrace();
        }

        int[] allowedTypes = MidiSystem.getMidiFileTypes(sequence);

        MidiSystem.write(sequence, allowedTypes[0], new File("hope this works " + "minimalism.mid"));
        synth.close();



        // set midi instrument
        channel = 0;

        synth = MidiSystem.getSynthesizer();
        synth.open();

        sequence = new Sequence(Sequence.PPQ, (int) PPQ);
        track1 = sequence.createTrack();

        sm = new ShortMessage();
        sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, channel, 0);
        track1.add(new MidiEvent(sm, 0));

        tick = 0;

        noteListLength = noteList.size();
        intensityListLength = intensityList.size();
        durationListLength = durationList.size();

        loopLength = (Math.min(noteListLength,Math.min(intensityListLength, durationListLength)));


        try { // CHORD VERSION
            // TODO NEED TO ADVANCE TIME AFTER EACH CHORD
            // Loop to set midi on/off message locations in the track

            if ((noteListLength % 4) != 0) {
                int z = 0;
            }

            for (int p = 0; p < noteListLength; p += 4) { // TODO NEED TO CHECK FOR NOTELISTLENGTH NOT DIVISIBLE BY NOTESPERCHORD
                for (int i = 0; i < notesPerChord; i++) { //TODO COULD HAVE MINIMALISM IDEA OF REPEATED SEQUENCE PER PHOTO OR AS CHORDS

                    ShortMessage on = new ShortMessage();
                    on.setMessage(ShortMessage.NOTE_ON, 0, noteList.get(i + p), intensityList.get(i + p));
                    MidiEvent me1 = new MidiEvent(on, tick);
                    track1.add(me1);
                }
                tick += (long) ticksPerFrame; // advances time to next frame start
                for (int i = 0; i < notesPerChord; i++) {
                    ShortMessage off = new ShortMessage();
//                tick += (long) scaleRange.scaleRange(durationList.get(i)); // NEW
//                    tick += durationList.get(i); // out because duration is constant
                    off.setMessage(ShortMessage.NOTE_OFF, 0, noteList.get(i + p), intensityList.get(i + p));
                    MidiEvent me1 = new MidiEvent(off, tick);
                    me1 = new MidiEvent(off, tick);
                    track1.add(me1);
                }
            }
        } catch ( InvalidMidiDataException e) {
            // JOption Pane
            JOptionPane.showMessageDialog(null, "Error! Invalid Midi Data Exception!");
            e.printStackTrace();
        }

        allowedTypes = MidiSystem.getMidiFileTypes(sequence);

        MidiSystem.write(sequence, allowedTypes[0], new File("hope this works "+ "chord.mid"));
    }
}