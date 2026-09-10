package dsb.sunny.selectionroles;

import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelectionRolesManager extends ListenerAdapter {

	private Guild guild;

	public SelectionRolesManager() {}

	public SelectionRolesManager(Guild guild) {
		this.guild = guild;
	}

	public Guild getGuild() {
		return guild;
	}

	public static boolean hasSelectMenu(Message message) {
		if (message.getComponents().isEmpty()) {
			return false;
		}

		for (MessageTopLevelComponent component : message.getComponents()) {
			if (component instanceof ActionRow row) {
				for (Component comp : row.getComponents()) {
					if (comp instanceof StringSelectMenu) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public StringSelectMenu getSelectMenu(Message message) {
		if (message.getComponents().isEmpty()) {
			return null;
		}

		for (MessageTopLevelComponent component : message.getComponents()) {
			if (component instanceof ActionRow row) {
				for (Component comp : row.getComponents()) {
					if (comp instanceof StringSelectMenu stringSelectMenu) {
						return stringSelectMenu;
					}
				}
			}
		}

		return null;
	}

	@Override
	public void onStringSelectInteraction(StringSelectInteractionEvent event) {
		if (event.getComponentId().equals("selectionroles")) {
			event.deferEdit().queue();

			for (SelectOption value : event.getComponent().getOptions()) {

				String roleId = value.getValue().split(":")[1];
				Member member = event.getMember();
				Role role = event.getGuild().getRoleById(roleId);

				if (!event.getSelectedOptions().contains(value)) {
					event.getGuild().removeRoleFromMember(member, role).queue();
					continue;
				}
				event.getGuild().addRoleToMember(member, role).queue();
			}
		}
	}

	/*
	 * Checks if role has been added to the Menu
	 */
	public SelectionRoleBuilder editSelectMenu(StringSelectMenu menu) {
		return new SelectionRoleBuilder(menu);
	}

	public static class SelectionRoleBuilder {

		private StringSelectMenu menu;

		private int minValues;
		private int maxValues;

		private final List<SelectOption> options;

		public SelectionRoleBuilder(StringSelectMenu menu) {
			this.menu = menu.createCopy().setMinValues(0).build();
			this.options = new ArrayList<>(menu.getOptions());
			this.minValues = menu.getMinValues();
			this.maxValues = menu.getMaxValues();
		}

		public boolean addRole(Role role, String emote) {
			if (options.stream().noneMatch(option -> option.getValue().equals(role.getId()))) {
				return false;
			}

            if (options.getFirst().getValue().equals("dummy")) {
				options.remove(menu.getOptions().getFirst());
			}

			options.add(SelectOption.of(role.getName(), "giverole:" + role.getId()).withEmoji(Emoji.fromFormatted(emote)));
			return true;
		}

		public boolean removeRole(Role role) {
			Iterator<SelectOption> option = this.options.iterator();
			while(option.hasNext()) {
				SelectOption selectOption = option.next();
				if (selectOption.getValue().contains(role.getId())) {
					option.remove();
					return true;
				}
			}
			return false;
		}

		public void setMaxValues(int maxValues) {
			this.menu = menu.createCopy().setMaxValues(maxValues).build();
			this.maxValues = maxValues;
		}

		public void setMinValues(int minValues) {
			this.menu = menu.createCopy().setMinValues(minValues).build();
			this.minValues = minValues;
		}

		public int getMaxValues() {
			return maxValues;
		}

		public int getMinValues() {
			return minValues;
		}

		public SelectMenu getSelectMenu() {
			if (options.isEmpty()) {
				StringSelectMenu.Builder builder = menu.createCopy();
				builder.getOptions().clear();
				builder.setPlaceholder("Es wurde keine Rolle hinzugefügt.");
				builder.addOption("Es wurde keine Rolle eingerichtet", "dummy");
				builder.setDisabled(true);

				return builder.build();
			}

			StringSelectMenu.Builder builder = menu.createCopy();
			builder.getOptions().clear();
			builder.addOptions(options);
			builder.setDisabled(false);
			builder.setPlaceholder(null);

			return builder.build();
		}
	}
}
