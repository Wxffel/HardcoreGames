package de.hglabor.plugins.hardcoregames.util;

import org.bukkit.plugin.messaging.StandardMessenger;

public interface ChannelIdentifier {
    String HG_QUEUE = StandardMessenger.validateAndCorrectChannel("hglabor:hgqueue");
}
