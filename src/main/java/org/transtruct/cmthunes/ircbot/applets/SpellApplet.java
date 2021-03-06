package org.transtruct.cmthunes.ircbot.applets;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.transtruct.cmthunes.irc.*;
import org.transtruct.cmthunes.util.*;

public class SpellApplet implements BotApplet {
    Process ispellProcess;
    PrintWriter ispellOut;
    BufferedReader ispellIn;
    
    public SpellApplet() {
        try {
            this.ispellProcess = (new ProcessBuilder("ispell", "-a")).start();
        } catch(IOException e) {
            throw new RuntimeException("Could start ispell process");
        }

        this.ispellIn = new BufferedReader(new InputStreamReader(this.ispellProcess.getInputStream()));
        this.ispellOut = new PrintWriter(this.ispellProcess.getOutputStream(), true);
        
        /* Discard version line */
        try {
            this.ispellIn.readLine();
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading from ispell process");
        }
    }

    public void run(IRCChannel channel, IRCUser from, String command, String[] args, String unparsed) {
        String line;
        int misspelledWordsCount = 0;

        if(unparsed.trim().equals("")) {
            return;
        }

        /* Write out string */
        this.ispellOut.println("^" + unparsed.trim());

        /* Read each returned line until a new line. One word is spell checked per line */
        while(true) {
            try {
                line = this.ispellIn.readLine().trim();
            } catch(IOException e) {
                e.printStackTrace();
                return;
            }

            if(line.equals("")) {
                break;
            } else if(line.startsWith("&") || line.startsWith("?")) {
                String original = line.split(" ", 3)[1];
                String guesses = line.split(":")[1].trim();

                channel.write(String.format("%s: %s", original, guesses));
                misspelledWordsCount++;
            } else if(line.startsWith("#")) {
                String original = line.split(" ", 3)[1];

                channel.write(String.format("%s: No suggestsions", original));
                misspelledWordsCount++;
            }
        }

        if(misspelledWordsCount == 0) {
            channel.write("No misspelled words");
        }
    }
}
