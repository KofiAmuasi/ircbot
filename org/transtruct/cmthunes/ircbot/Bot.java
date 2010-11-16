package org.transtruct.cmthunes.ircbot;

import java.net.*;

import org.transtruct.cmthunes.irc.*;
import org.transtruct.cmthunes.irc.messages.*;
import org.transtruct.cmthunes.irc.messages.filter.*;

public class Bot {
    public static void main(String[] args) {
        InetSocketAddress addr = new InetSocketAddress("irc.brewtab.com", 6667);

        /* Create IRC client */
        IRCClient client = new IRCClient(addr);

        /* Will block until connection process is complete */
        client.connect("bot", "bot", "kitimat", "Mr. Bot");

        /*
         * We can add a message handler for the client to print all messages
         * from the server if we needed to for debugging
         */
        client.addHandler(IRCMessageFilters.PASS, new IRCMessageHandler() {
            @Override
            public void handleMessage(IRCMessage message) {
                System.out.println("root>>> " + message.toString().trim());
            }
        });

        /*
         * Join a channel. Channels can also be directly instantiated and
         * separately joined
         */
        IRCChannel c = client.join("#main");

        /* We add a handler for channel messages */
        c.addListener(new BotChannelListener());

        /* Wait for client object's connection to exit and close */
        client.getConnection().awaitClosed();
        System.out.println("Exiting");
    }
}
