package me.dirolgaming.safebac;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;


/**
 * @author Mark Vainomaa
 */
final class Utils {
    public final LuckPermsApi api = LuckPerms.getApi();
    static String buildMessage(String[] args) {
        StringBuilder acb = new StringBuilder();
        for(String arg : args)
            acb.append(arg).append(" ");
        return acb.toString();
    }
        static String getPrefix(ProxiedPlayer p) {
            if(ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms") == null) {
                return "";
            }
        final LuckPermsApi lpAPI = LuckPerms.getApi();
            User user = lpAPI.getUser(p.getUniqueId());
            Contexts userCtx = lpAPI.getContextForUser(user).orElseThrow(() -> new IllegalStateException("Could not get LuckPerms context for player " + p));
            return user.getCachedData().getMetaData(userCtx).getPrefix();
        }
    static void broadcastAdminchatMessage(Main safeBAC, ProxiedPlayer p, String message) {
        TextComponent evac = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', safeBAC.getConfig().getString("adminchat-format")
                .replace("%player%", p.getName())
                .replace("%prefix%", getPrefix(p))
                .replace("%message%", message))));

        if (safeBAC.getConfig().getBoolean("adminchat-hover.enable")) {
            evac.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', safeBAC.getConfig().getString("adminchat-hover.message")
                    .replace("%server%", p.getServer().getInfo().getName())
                    .replace("%prefix%", getPrefix(p))
                    )).create()));
        }

        if (safeBAC.getConfig().getBoolean("adminchat-click.enable")) {
            if (safeBAC.getConfig().getBoolean("adminchat-click.url.enable-")) {
                evac.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, safeBAC.getConfig().getString("adminchat-click.url.url")));
            }

            if (safeBAC.getConfig().getBoolean("adminchat-click.command.enable")) {
                evac.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, safeBAC.getConfig().getString("adminchat-click.command.command")
                        .replace("%player%", p.getName())
                        .replace("%prefix%", getPrefix(p))
                        .replace("%server%", p.getServer().getInfo().getName())));
            }
        }

        for (ProxiedPlayer ev : safeBAC.getProxy().getPlayers()) {
            if (ev.hasPermission("sbac.ac")) {
                ev.sendMessage(evac);
            }
        }
    }
}
