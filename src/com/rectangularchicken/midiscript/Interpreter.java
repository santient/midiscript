/*
 * MIDIscript: a programming language based on MIDI events
 * By Santiago Benoit
 */
package com.rectangularchicken.midiscript;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * The interpreter.
 * @author Santiago Benoit
 * @version 1.0
 */
public class Interpreter {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("MIDIscript (Version " + VERSION + ")");
            System.out.println("By Santiago Benoit");
            System.out.println("Usage: java -jar MIDIscript.jar filename.midi");
        } else {
            Sequence sequence = MidiSystem.getSequence(new File(args[0]));
            for (Track track : sequence.getTracks()) {
                ArrayList<Integer> code = new ArrayList<>();
                for (int i = 0; i < track.size(); i++) {
                    MidiMessage message = track.get(i).getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if (sm.getCommand() == NOTE_ON) {
                            code.add(sm.getData1() % 12);
                        }
                    }
                }
                new Thread(() -> {
                    ArrayList<Byte> data = new ArrayList<>();
                    int dataPtr = 0;
                    Scanner scanner = new Scanner(System.in);
                    String charInput = "";
                    for (int codePtr = 0; codePtr < code.size(); codePtr++) {
                        switch (code.get(codePtr)) {
                            case 0: // halt entire program (x)
                                System.exit(0);
                                break;
                            case 1: // increment data pointer (>)
                                dataPtr++;
                                break;
                            case 2: // decrement data pointer (<)
                                if (dataPtr == 0) {
                                    data.add(0, (byte) 0);
                                } else {
                                    dataPtr--;
                                }
                                break;
                            case 3: // increment byte at data pointer (+)
                                grow(data, dataPtr);
                                data.set(dataPtr, (byte) (data.get(dataPtr) + 1));
                                break;
                            case 4: // decrement byte at data pointer (-)
                                grow(data, dataPtr);
                                data.set(dataPtr, (byte) (data.get(dataPtr) - 1));
                                break;
                            case 5: // output byte at data pointer as integer (:)
                                grow(data, dataPtr);
                                System.out.print((byte) data.get(dataPtr));
                                break;
                            case 6: // accept byte of input as integer (;)
                                grow(data, dataPtr);
                                data.set(dataPtr, scanner.nextByte());
                                break;
                            case 7: // output byte at data pointer as character (.)
                                grow(data, dataPtr);
                                System.out.print((char) (byte) data.get(dataPtr));
                                break;
                            case 8: // accept byte of input as character (,)
                                grow(data, dataPtr);
                                if (charInput.length() == 0) {
                                    charInput = scanner.next();
                                }
                                data.set(dataPtr, (byte) charInput.charAt(0));
                                charInput = charInput.substring(1);
                                break;
                            case 9: // jump code pointer to matching 10 if byte at data pointer is zero ([)
                                grow(data, dataPtr);
                                if (data.get(dataPtr) == (byte) 0) {
                                    int count = 1;
                                    while (count > 0) {
                                        codePtr++;
                                        if (code.get(codePtr) == 9) {
                                            count++;
                                        } else if (code.get(codePtr) == 10) {
                                            count--;
                                        }
                                    }
                                }
                                break;
                            case 10: // jump code pointer to matching 9 if byte at data pointer is nonzero (])
                                grow(data, dataPtr);
                                if (data.get(dataPtr) != (byte) 0) {
                                    int count = 1;
                                    while (count > 0) {
                                        codePtr--;
                                        if (code.get(codePtr) == 9) {
                                            count--;
                                        } else if (code.get(codePtr) == 10) {
                                            count++;
                                        }
                                    }
                                }
                                break;
                            case 11: // halt just this thread (#)
                                return;
                            default: // ignore
                                break;
                        }
                    }
                }).start();
            }
        }
    }
    
    private static void grow(ArrayList data, int dataPtr) {
        int size = data.size();
        for (int i = 0; i <= dataPtr - size; i++) {
            data.add((byte) 0);
        }
    }
    
    public static final String VERSION = "1.0";
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
}
