package hu.montlikadani.tablist.bukkit;

import java.lang.reflect.Array;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import hu.montlikadani.tablist.bukkit.utils.ReflectionUtils;

public class HidePlayers {

	private GameProfile profile;
	private Object playerConst;
	private Class<?> enumPlayerInfoAction;
	private Object entityPlayerArray;

	private final Player player;

	public HidePlayers(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void addPlayerToTab() {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			addPlayerToTab(pl);
		}
	}

	public void removePlayerFromTab() {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			removePlayerFromTab(player, pl);
			removePlayerFromTab(pl, player);
		}
	}

	public void removePlayerFromTab(Player p, Player to) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(TabList.getInstance(), () -> r(p, to), 4L);
	}

	public void addPlayerToTab(Player to) {
		try {
			Object packetPlayOutPlayerInfo = ReflectionUtils.getNMSClass("PacketPlayOutPlayerInfo")
					.getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass()).newInstance(ReflectionUtils
							.getFieldObject(enumPlayerInfoAction, enumPlayerInfoAction.getDeclaredField("ADD_PLAYER")),
							entityPlayerArray);

			ReflectionUtils.sendPacket(to, packetPlayOutPlayerInfo);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void r(Player p, Player to) {
		try {
			profile = new GameProfile(p.getUniqueId(), p.getName());
			playerConst = ReflectionUtils.Classes.getPlayerContructor(p, profile);

			enumPlayerInfoAction = ReflectionUtils.Classes.getEnumPlayerInfoAction();

			ReflectionUtils.setField(playerConst, "listName", ReflectionUtils.getAsIChatBaseComponent(profile.getName()));

			entityPlayerArray = Array.newInstance(playerConst.getClass(), 1);
			Array.set(entityPlayerArray, 0, playerConst);

			Object packetPlayOutPlayerInfo = ReflectionUtils.getNMSClass("PacketPlayOutPlayerInfo")
					.getConstructor(enumPlayerInfoAction, entityPlayerArray.getClass())
					.newInstance(ReflectionUtils.getFieldObject(enumPlayerInfoAction,
							enumPlayerInfoAction.getDeclaredField("REMOVE_PLAYER")), entityPlayerArray);

			ReflectionUtils.sendPacket(to, packetPlayOutPlayerInfo);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
