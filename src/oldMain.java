import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.Buffer;
import java.util.ArrayList;

public class oldMain{
    public static void main(String []args) throws IOException {

        ArrayList<Integer> pixelList = new ArrayList<>();
        int tick = 0;
//        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber("D:/video.mp4");
//        frameGrabber.start();
//        IplImage i;
//        try {
//
//            i = frameGrabber.grab();
//            BufferedImage  bi = i.getBufferedImage();
//            ImageIO.write(bi,"png", new File("D:/Img.png"));
//            frameGrabber.stop();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }


        FFmpegFrameGrabber g = new FFmpegFrameGrabber("UntitledProject-medium.m4v");
        g.start();

//        Java2DFrameConverter c = new Java2DFrameConverter();
//        c.convert(g.grab());

//        for (int i = 0 ; i < 5000000 ; i++) {
        int i = 0;
        int length = g.getLengthInFrames();
        ArrayList<BufferedImage> bufferedImageArrayList = new ArrayList<>();
        ArrayList<Frame> frameArrayList = new ArrayList<>();
        double frameRate = g.getFrameRate();
        double frameTime = 1.0 / frameRate;


        if (true) {
            while (i < length) {
                try {
                    Java2DFrameConverter c = new Java2DFrameConverter();
                    File f = new File("~/Documents/IdeaProjects/TimelapseSoundtrack/" + System.currentTimeMillis() + ".jpeg");
                    BufferedImage bi = c.getBufferedImage(g.grab());
                    if (bi == null) {
                        i = 0; // debug catch for null frame
                    }

//                    testZone(bi, frameRate, frameTime, tick);
//                    tick++;

                    bufferedImageArrayList.add(bi); //TODO MAYBE REWORK SO THAT THIS LIST ISNT NEEDED
                    System.out.println(((((double) i) / (double) length) * 100) + "%");
                    i++;
                } catch (java.lang.OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

        } else {

        }


        int test = 0;

        //File file = new File("arrayList.arl");
//        FileOutputStream fileOutputStream = new FileOutputStream("tmp/storedArray");
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//        objectOutputStream.writeObject(bufferedImageArrayList);
//        objectOutputStream.close();
//        fileOutputStream.close();

//        BufferedImage bi2;
//        ArrayList<BufferedImage> imageList = new ArrayList<>();
//        i = 0;
//        while (i <= length) {
//            File f = new File("tmp/" + i + ".jpeg");
//            bi2 = ImageIO.read(f);
//            imageList.add(bi2);
//            i++;
//        }

        pixelList.add(600);
        pixelList.add(1000);
        pixelList.add(2000);
        pixelList.add(1356);


        ArrayList<Integer> rgbList = new ArrayList<>();
        ArrayList<Color> colorList = new ArrayList<>();


        for (int q = 0; q < bufferedImageArrayList.size(); q++) {
            for (int p = 0; p < pixelList.size(); p++) {
                try {
                    Color myColor = new Color(bufferedImageArrayList.get(q).getRGB(pixelList.get(p), pixelList.get(p)));
                    int a = myColor.getGreen();
                    int b = myColor.getBlue();
                    int c = myColor.getRed();
                    int d = myColor.getAlpha();
                    int e = (a + b + c + d) / 4;

                    if (e > 127) {
                        e = e % 128;
                    }
                    rgbList.add(e);
                } catch (java.lang.NullPointerException e) {
                    e.printStackTrace();
                    i = 0;
                }
            }
        }

        int notesPerChord = pixelList.size();


        try {
            RecordSynth.Synthesizer(rgbList, frameRate, frameTime, notesPerChord);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }


        g.stop();
//        g.stop();


        // TODO Add Frames to an ArrayList so that we dont have to save images to disk first
        // TODO Could present memory issues
        // TODO Next iteration save frameArrayList object to disk

    }
}




