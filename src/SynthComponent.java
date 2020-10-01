/**
 * Author: Alex Worland
 * Date: 2/26/16
 * Description: CS111 Project 2
 */
import javax.sound.midi.*;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SynthComponent {

    private static boolean isPaused;
    private static boolean isCancelled;

    public static void Synthesizer(String fileName, double noteDurationMultiplier, JProgressBar progressBar) throws MidiUnavailableException {

        File file = new File(fileName);

        ArrayList<Integer> noteList = new ArrayList();
        ArrayList<Integer> intensityList = new ArrayList();
        ArrayList<Integer> durationList = new ArrayList();

        try {
            Scanner fileScan = new Scanner(file);
            // default is note, intensity, duration
            for (int i = 0; fileScan.hasNext(); i++) {
                if (fileScan.hasNext()) {
                    noteList.add(i, Math.abs(fileScan.nextInt()));
                }

                if (fileScan.hasNext()) {
                    intensityList.add(i, Math.abs(fileScan.nextInt()));
                }

                if (fileScan.hasNext()) {
                    durationList.add(i, Math.abs(fileScan.nextInt()));
                }
            }
            fileScan.close();
        } catch (NoSuchElementException e) {
            // optionpane window
            JOptionPane.showMessageDialog(null, "Error! NoSuchElementException!");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // optionpane window
            JOptionPane.showMessageDialog(null, "Error! File not found!");
            e.printStackTrace();
        }

        int channel = 0;

            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel[] channels = synth.getChannels();
            Instrument[] instr = synth.getDefaultSoundbank().getInstruments();

            synth.loadInstrument(instr[channel]);


            // Midi Playing Code

            int noteListLength = noteList.size();
            int intensityListLength = intensityList.size();
            int durationListLength = durationList.size();
            int loopLength;

            loopLength = (Math.min(noteListLength,Math.min(intensityListLength, durationListLength)));


            for (int i = 0; i < loopLength; i++) {
                try {
                    if (isCancelled) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    if (!isPaused) {
                        channels[channel].noteOn(noteList.get(i), intensityList.get(i));
                        Thread.sleep((long) ((durationList.get(i)) * noteDurationMultiplier));
                        channels[channel].noteOff(noteList.get(i), intensityList.get(i));
                    } else {
                        while (isPaused) {
                            Thread.sleep(1);
                        }
                    }

                } catch (InterruptedException e) {
                    // Interrupt The Thread
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    break;
                }
                // Update progress bar
                progressBar.setValue(100 * (i+1)/loopLength);
            }
            synth.close();
    }

    public static void setPaused(boolean b) {
        isPaused = b;
    }

    public static void setIsCancelled(boolean b) {
        isCancelled = b;
    }
    public static boolean getPaused() {
        return isPaused;
    }
}