package de.hglabor.plugins.hardcoregames.util;

import org.bukkit.plugin.messaging.StandardMessenger;

public interface ChannelIdentifier {
    String HG_QUEUE = StandardMessenger.validateAndCorrectChannel("hglabor:hgqueue");
    String HG_QUEUE_QUIT = StandardMessenger.validateAndCorrectChannel("hglabor:hgqueuequit");
    String HG_QUEUE_LEAVE = "hgqueue-leave";
    String HG_QUEUE_LEAVE_CUZ_JOIN = StandardMessenger.validateAndCorrectChannel("hglabor:hgqueueleavecuzjoin");
    String HG_QUEUE_JOIN = "hgqueue-join";
}
