package Me.Teenaapje.Referral.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Me.Teenaapje.Referral.Utils.Utils;

import java.util.List;
import java.util.UUID;

public class RefAdmin extends CommandBase {
	// init class
	public RefAdmin() {
		permission = "RefAdmin";
		command = "Admin";
		forPlayerOnly = false;
		subCommands = new String[]{"Reset", "Remove", "Lookup"};
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// check arguments
		if (args.length > 3) {
			Utils.SendMessage(sender, core.config.tooManyArgs);
			return false;
		} else if (args.length >= 2 && args.length < 3) {
			Utils.SendMessage(sender, core.config.missingPlayer);
			return false;
		} else if (args.length < 2) {
			Utils.SendMessage(sender, core.config.missingArgs);
			return false;
		}


		switch (args[1].toLowerCase()) {
			case "remove":
				// remove player from database
				if (args[2].compareTo("*") == 0) {
					core.db.RemoveAll();
					Utils.SendMessage(sender, core.config.allRemoved);
				} else {
					Player player = core.GetPlayer(args[2]);

					if (player != null && RemovePlayer(player)) {
						// Player removed
						Utils.SendMessage(sender, core.config.playerRemoved, player);
					} else {
						// Player removed failed
						Utils.SendMessage(sender, core.config.playerRemovedFailed, player);
					}
				}
				break;
			case "reset":
				// reset player
				if (args[2].compareTo("*") == 0) {
					core.db.ResetAll();
					Utils.SendMessage(sender, core.config.allReset);
				} else {
					Player player = core.GetPlayer(args[2]);

					if (player != null && ResetPlayer(player)) {
						// Player reset
						Utils.SendMessage(sender, core.config.playerReset, player);
					} else {
						// Player reset failed
						Utils.SendMessage(sender, core.config.playerResetFailed, player);
					}
				}
				break;
			case "lookup":
				if (args.length == 3) {
					Player player = core.GetPlayer(args[2]);
					if (player != null) {
						List<String> referredPlayers = core.db.getReferredPlayers(player.getUniqueId().toString());
						if (referredPlayers.isEmpty()) {
							sender.sendMessage("Player " + player.getName() + " has not referred anybody.");
						} else {
							sender.sendMessage("Player " + player.getName() + " has referred the following players:");
							for (String referredPlayer : referredPlayers) {
								OfflinePlayer offlineReferredPlayer = Bukkit.getOfflinePlayer(UUID.fromString(referredPlayer));
								String referredPlayerName = offlineReferredPlayer.getName();
								sender.sendMessage("- " + referredPlayerName);
							}
						}
					} else {
						sender.sendMessage("Player not found.");
					}
				} else {
					sender.sendMessage("Please provide a player name.");
				}
				break;
			default:
				Utils.SendMessage(sender, "&cIncorrect use of command");
				break;
		}
		return true;
	}

	private boolean RemovePlayer(Player player) {
		return core.db.PlayerRemove(player.getUniqueId().toString());
	}

	private boolean ResetPlayer(Player player) {
		return core.db.PlayerReset(player.getUniqueId().toString());
	}
}