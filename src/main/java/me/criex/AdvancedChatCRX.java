// Created by Criex just for free use :3 //
package me.criex;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AdvancedChatCRX extends PluginBase implements Listener {

    private final Map<Player, Long> messageCooldown = new HashMap<>();
    private final Map<Player, Integer> spamCounter = new HashMap<>();
    private final Map<Player, Integer> commandSpamCounter = new HashMap<>();

    private int maxMessageLength;
    private long chatCooldown;
    private int maxSpamCount;
    private int maxCommandSpamCount;

    private boolean replaceAtSymbol;

    private String noPermissionMessage;
    private String maxLengthMessage;
    private String advertisementProhibitedMessage;
    private String spamWarningMessage;
    private String cooldownMessage;
    private String commandSpamWarningMessage;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        loadConfigValues();
        loadMessages();

        this.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AdvancedChatCRX plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        messageCooldown.clear();
        spamCounter.clear();
        commandSpamCounter.clear();
        getLogger().info("AdvancedChatCRX plugin has been disabled!");
    }

    private void loadConfigValues() {
        maxMessageLength = getConfig().getInt("max-message-length", 180);
        chatCooldown = getConfig().getLong("chat-cooldown-ms", 3000);
        maxSpamCount = getConfig().getInt("max-spam-count", 3);
        maxCommandSpamCount = getConfig().getInt("max-command-spam-count", 2);
        replaceAtSymbol = getConfig().getBoolean("replace-at-symbol", true);
    }

    private void loadMessages() {
        noPermissionMessage = getConfig().getString("messages.no-permission", "You do not have permission to send messages.");
        maxLengthMessage = getConfig().getString("messages.max-length", "Maximum message length is {length} characters!");
        advertisementProhibitedMessage = getConfig().getString("messages.advertisement-prohibited", "Advertising is prohibited!");
        spamWarningMessage = getConfig().getString("messages.spam-warning", "Please, do not spam.");
        cooldownMessage = getConfig().getString("messages.cooldown", "Wait {seconds} sec.");
        commandSpamWarningMessage = getConfig().getString("messages.command-spam-warning", "Please, do not spam!");
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!player.hasPermission("criex.nocommandchat") && !player.isOp()) {
            player.sendMessage(TextFormat.colorize(noPermissionMessage));
            event.setCancelled(true);
            return;
        }

        String filteredMessage = filterSymbols(message);

        if (message.startsWith("/")) {
            handleCommandSpam(player);
            event.setMessage(filteredMessage);
            return;
        }

        if (filteredMessage.length() > maxMessageLength) {
            player.sendMessage(TextFormat.colorize(maxLengthMessage.replace("{length}", String.valueOf(maxMessageLength))));
            event.setCancelled(true);
            return;
        }

        if (isAdvertisement(filteredMessage)) {
            player.sendMessage(TextFormat.colorize(advertisementProhibitedMessage));
            event.setCancelled(true);
            return;
        }

        event.setMessage(filteredMessage);

        if (isSpamming(player)) {
            player.sendMessage(TextFormat.colorize(spamWarningMessage));
            event.setCancelled(true);
            return;
        }

        if (!checkCooldown(player)) {
            long remaining = getRemainingCooldown(player);
            player.sendMessage(TextFormat.colorize(cooldownMessage.replace("{seconds}", String.valueOf(remaining))));
            event.setCancelled(true);
            return;
        }

        spamCounter.put(player, spamCounter.getOrDefault(player, 0) + 1);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!player.hasPermission("criex.nochat") && !player.isOp()) {
            player.sendMessage(TextFormat.colorize(noPermissionMessage));
            event.setCancelled(true);
            return;
        }

        String filteredMessage = filterSymbols(message);
        event.setMessage(filteredMessage);

        handleCommandSpam(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        messageCooldown.remove(player);
        spamCounter.remove(player);
        commandSpamCounter.remove(player);
    }

    private boolean isAdvertisement(String message) {
        Pattern pattern = Pattern.compile("(https?://|www\\.|[0-9]{1,3}(\\.[0-9]{1,3}){3})");
        return pattern.matcher(message).find();
    }

    private String filterSymbols(String message) {
        StringBuilder result = new StringBuilder();
        for (char c : message.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                    (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') ||
                    (c >= '0' && c <= '9') ||
                    "!#$&*()|[] ,./~<>-:?^ё+=".indexOf(c) != -1) {
                result.append(c);
            } else if (c == '@' && replaceAtSymbol) { 
                result.append('?');
            } else if (c == '{') {
                result.append('_');
            } else if (c == '§') {
                // skip
            } else {
                result.append('_');
            }
        }
        return result.toString();
    }

    private boolean isSpamming(Player player) {
        int count = spamCounter.getOrDefault(player, 0);
        return count >= maxSpamCount;
    }

    private boolean checkCooldown(Player player) {
        long lastMessageTime = messageCooldown.getOrDefault(player, 0L);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime < chatCooldown) {
            return false;
        }
        messageCooldown.put(player, currentTime);
        spamCounter.put(player, 0);
        return true;
    }

    private long getRemainingCooldown(Player player) {
        long lastMessageTime = messageCooldown.getOrDefault(player, 0L);
        long currentTime = System.currentTimeMillis();
        long remaining = chatCooldown - (currentTime - lastMessageTime);
        return Math.max(remaining / 1000, 1);
    }

    private void handleCommandSpam(Player player) {
        int count = commandSpamCounter.getOrDefault(player, 0);
        if (count >= maxCommandSpamCount) {
            player.sendMessage(TextFormat.colorize(commandSpamWarningMessage));
            return;
        }
        commandSpamCounter.put(player, count + 1);

        this.getServer().getScheduler().scheduleDelayedTask(this, () -> {
            commandSpamCounter.put(player, 0);
        }, 20);
    }
}
