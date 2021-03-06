package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestTrigger;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.questobjects.ItemObject;
import me.Cutiemango.MangoQuest.questobjects.NumerableObject;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectConsumeItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectDeliverItem;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectKillMob;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectReachLocation;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectTalkToNPC;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;
import net.citizensnpcs.api.CitizensAPI;

public class CommandEditQuest
{

	// Command: /mq e edit args[2] args[3]
	public static void execute(Quest q, Player sender, String[] args)
	{
		if (!QuestEditorManager.checkEditorMode(sender, true))
			return;
		if (args.length == 4 && args[3].equals("cancel"))
		{
			QuestEditorManager.editQuest(sender);
			return;
		}
		switch (args[2])
		{
			case "name":
				editName(q, sender, args);
				break;
			case "outline":
				editOutline(q, sender, args);
				break;
			case "redo":
				editRedo(q, sender, args);
				break;
			case "redodelay":
				editRedoDelay(q, sender, args);
				break;
			case "npc":
				editNPC(q, sender, args);
				break;
			case "req":
				editRequirements(q, sender, args);
				break;
			case "evt":
				editEvent(q, sender, args);
				break;
			case "stage":
				editStage(q, sender, args);
				break;
			case "object":
				editObject(q, sender, args);
				break;
			case "reward":
				editReward(q, sender, args);
				break;
		}
	}

	private static void editName(Quest q, Player sender, String args[])
	{
		if (args.length == 3)
		{

			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit name"));
			QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				q.setQuestName(args[3]);
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	private static void editOutline(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit outline"));
			QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.Outline"));
			return;
		}
		else
			if (args.length >= 4)
			{
				int line = 0;
				try
				{
					line = Integer.parseInt(args[3]) - 1;
				}
				catch (NumberFormatException e)
				{
					QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
					QuestEditorManager.editQuest(sender);
					return;
				}
				if (line < 0)
				{
					QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
					QuestEditorManager.editQuest(sender);
					return;
				}
				String s = "";
				for (int i = 4; i < args.length; i++)
				{
					s = s + args[i] + " ";
				}
				s = s.trim();
				if (q.getQuestOutline().size() - 1 < line)
					q.getQuestOutline().add(line, s);
				else
					q.getQuestOutline().set(line, s);
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	private static void editRedo(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit redo"));
			QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				q.setRedoable(Boolean.parseBoolean(args[3]));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	@SuppressWarnings("unchecked")
	private static void editRequirements(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.editQuestRequirement(sender);
			return;
		}
		RequirementType t = RequirementType.valueOf(args[3]);
		if (args.length >= 6)
		{
			if (args[5].equalsIgnoreCase("cancel"))
			{
				QuestEditorManager.editQuestRequirement(sender);
				return;
			}
			switch (t)
			{
				case ITEM:
				case LEVEL:
				case MONEY:
					break;
				case NBTTAG:
					if (((List<String>) q.getRequirements().get(t)).contains(args[5]))
					{
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ObjectExist"));
						((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
						break;
					}
					((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
					((List<String>) q.getRequirements().get(t)).add(args[5]);
					break;
				case QUEST:
					if (QuestUtil.getQuest(args[5]) != null)
					{
						if (((List<String>) q.getRequirements().get(t)).contains(args[5]))
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ObjectExist"));
							((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
							break;
						}
						((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
						((List<String>) q.getRequirements().get(t)).add(args[5]);
						break;
					}
					else
					{
						QuestChatManager.error(sender, I18n.locMsg("CommandInfo.QuestNotFound"));
						break;
					}
				case SCOREBOARD:
					if (((List<String>) q.getRequirements().get(t)).contains(args[5]))
					{
						QuestChatManager.error(sender, I18n.locMsg("EditorMessage.ObjectExist"));
						((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
						break;
					}
					((List<String>) q.getRequirements().get(t)).remove(Integer.parseInt(args[4]));
					((List<String>) q.getRequirements().get(t)).add(args[5]);
					break;
				default:
					break;

			}
			QuestEditorManager.editQuestRequirement(sender);
			return;
		}
		else
			if (args.length == 5)
			{
				switch (t)
				{
					case LEVEL:
						q.getRequirements().put(t, Integer.parseInt(args[4]));
						QuestEditorManager.editQuestRequirement(sender);
						break;
					case MONEY:
						q.getRequirements().put(t, Double.parseDouble(args[4]));
						QuestEditorManager.editQuestRequirement(sender);
						break;
					case QUEST:
						QuestEditorManager.selectQuest(sender, "mq e edit req " + t.toString() + " " + Integer.parseInt(args[4]));
						break;
					case SCOREBOARD:
					case NBTTAG:
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit req " + t.toString() + " " + Integer.parseInt(args[4])));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
						break;
					case ITEM:
						break;
				}
				return;
			}
			else
				if (args.length == 4)
				{
					switch (t)
					{
						case LEVEL:
						case MONEY:
							EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit req " + t.toString()));
							QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
							break;
						case ITEM:
							EditorListenerHandler.registerGUI(sender, "requirement");
							break;
						default:
							break;
					}
					return;
				}
	}

	private static void editNPC(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.NPC_LEFT_CLICK, "mq e edit npc"));
			QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.ClickNPC"));
			return;
		}
		else
			if (args.length == 4)
			{
				if (args[3].equalsIgnoreCase("-1"))
				{
					q.setQuestNPC(null);
					QuestEditorManager.editQuest(sender);
					QuestChatManager.info(sender, I18n.locMsg("EditorMessage.NPCRemoved"));
					return;
				}
				q.setQuestNPC(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[3])));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	private static void editRedoDelay(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit redodelay"));
			QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
			return;
		}
		else
			if (args.length == 4)
			{
				String mili = Integer.toString(Integer.parseInt(args[3]) * 1000);
				q.setRedoDelay(Long.parseLong(mili));
				QuestEditorManager.editQuest(sender);
				return;
			}
	}

	// Command: /mq e edit evt [index] [type] ([stage]) [object]
	private static void editEvent(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.editQuestTrigger(sender);
			return;
		}
		int index = Integer.parseInt(args[3]);
		TriggerType type = TriggerType.valueOf(args[4]);
		if (args.length >= 8)
		{
			if (type.equals(TriggerType.TRIGGER_STAGE_START) || type.equals(TriggerType.TRIGGER_STAGE_FINISH))
			{
				int i = Integer.parseInt(args[5]);
				TriggerObject obj = TriggerObject.valueOf(args[6]);
				String s = "";
				for (int j = 7; j < args.length; j++)
				{
					s = s + args[j] + " ";
				}
				if (index == q.getTriggers().size())
					q.getTriggers().add(new QuestTrigger(type, obj, i, s));
				else
					q.getTriggers().set(index, new QuestTrigger(type, obj, i, s));
				QuestEditorManager.editQuestTrigger(sender);
				return;
			}
			TriggerObject obj = TriggerObject.valueOf(args[5]);
			String s = "";
			for (int j = 6; j < args.length; j++)
			{
				s = s + args[j];
				if (j + 1 == args.length)
					break;
				else
					s += " ";
			}
			if (index == q.getTriggers().size())
				q.getTriggers().add(new QuestTrigger(type, obj, s));
			else
				q.getTriggers().set(index, new QuestTrigger(type, obj, s));
			QuestEditorManager.editQuestTrigger(sender);
			return;
		}
		else
			if (args.length == 7)
			{
				switch (type)
				{
					case TRIGGER_STAGE_START:
					case TRIGGER_STAGE_FINISH:
						int i = Integer.parseInt(args[5]);
						TriggerObject obj = TriggerObject.valueOf(args[6]);
						EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING,
								"mq e edit evt " + index + " " + type.toString() + " " + i + " " + obj.toString()));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
						return;
					default:
						obj = TriggerObject.valueOf(args[5]);
						String s = "";
						for (int j = 6; j < args.length; j++)
						{
							s = s + args[j];
							if (j + 1 == args.length)
								break;
							else
								s += " ";
						}
						if (index == q.getTriggers().size())
							q.getTriggers().add(new QuestTrigger(type, obj, s));
						else
							q.getTriggers().set(index, new QuestTrigger(type, obj, s));
						QuestEditorManager.editQuestTrigger(sender);
						return;
				}
			}
			else
				if (args.length == 6)
				{
					TriggerObject obj = TriggerObject.valueOf(args[5]);
					EditorListenerHandler.register(sender,
							new EditorListenerObject(ListeningType.STRING, "mq e edit evt " + index + " " + type.toString() + " " + obj.toString()));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
					return;
				}
	}

	private static void editStage(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.editQuestStages(sender);
			return;
		}
		else
			if (args.length == 4)
			{
				int stage = 1;
				try
				{
					stage = Integer.parseInt(args[3]);
				}
				catch (NumberFormatException e)
				{
					QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
				}
				QuestEditorManager.editQuestObjects(sender, stage);
				return;
			}
	}

	// /mq e edit object [stage] [objcount] [obj] [內容]...
	private static void editObject(Quest q, Player sender, String[] args)
	{
		if (args.length <= 4)
		{
			QuestEditorManager.editQuestStages(sender);
			return;
		}
		int stage = 1;
		int obj = 1;
		try
		{
			stage = Integer.parseInt(args[3]);
			obj = Integer.parseInt(args[4]);
		}
		catch (NumberFormatException e)
		{
			QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
			return;
		}
		switch (args.length)
		{
			case 5:
				QuestEditorManager.editQuestObject(sender, stage, obj);
				return;
			case 6:
				switch (args[5].toLowerCase())
				{
					case "block":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.BLOCK, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.BreakBlock"));
						break;
					case "amount":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterAmount"));
						break;
					case "item":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.ITEM, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.RightClick"));
						break;
					case "itemnpc":
					case "npc":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.NPC_LEFT_CLICK, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.ClickNPC"));
						break;
					case "mtmmob":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.MTMMOB_LEFT_CLICK, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterMobID"));
						break;
					case "mobname":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterMobName"));
						break;
					case "mobtype":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.MOB_LEFT_CLICK, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.HitMob"));
						break;
					case "loc":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.LOCATION, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.ReachLocation"));
						break;
					case "locname":
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit object " + stage + " " + obj + " " + args[5]));
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.LocationName"));
						break;
					case "type":
						QuestEditorManager.selectObjectType(sender, stage, obj);
						return;
					default:
						return;
				}
				return;
			case 7:
				SimpleQuestObject o = q.getStage(stage - 1).getObject(obj - 1);
				if (args[6].equalsIgnoreCase("cancel"))
					break;
				switch (args[5].toLowerCase())
				{
					case "block":
						String[] split = args[6].split(":");
						((QuestObjectBreakBlock) o).setType(Material.getMaterial(split[0]));
						((QuestObjectBreakBlock) o).setSubID(Short.parseShort(split[1]));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered",
								QuestUtil.translate(Material.getMaterial(split[0]), Short.parseShort(split[1]))));
						break;
					case "amount":
						((NumerableObject) o).setAmount(Integer.parseInt(args[6]));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", args[6]));
						break;
					case "item":
						((ItemObject) o).setItem(Main.instance.handler.getItemInMainHand(sender));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ItemRegistered"));
						break;
					case "itemnpc":
						((QuestObjectDeliverItem) o).setTargetNPC(CitizensAPI.getNPCRegistry().getById(Integer.valueOf(args[6])));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", args[6]));
						break;
					case "npc":
						((QuestObjectTalkToNPC) o).setTargetNPC(CitizensAPI.getNPCRegistry().getById(Integer.valueOf(args[6])));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", args[6]));
						break;
					case "mtmmob":
						MythicMob mob = Main.instance.initManager.getMythicMobsAPI().getMythicMob(args[6]);
						((QuestObjectKillMob) o).setMythicMob(mob);
						((QuestObjectKillMob) o).setCustomName(mob.getDisplayName());
						((QuestObjectKillMob) o).setType(EntityType.valueOf(mob.getEntityType().toUpperCase()));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", args[6]));
						break;
					case "mobname":
						((QuestObjectKillMob) o).setCustomName(QuestChatManager.translateColor(args[6]));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", args[6]));
						break;
					case "mobtype":
						((QuestObjectKillMob) o).setType(EntityType.valueOf(args[6]));
						QuestChatManager.info(sender,
								I18n.locMsg("EditorMessage.ObjectRegistered", QuestUtil.translate(EntityType.valueOf(args[6]))));
						break;
					case "loc":
						((QuestObjectReachLocation) o).setRadius(Integer.parseInt(args[6]));
						Location l = sender.getLocation();
						((QuestObjectReachLocation) o).setLocation(new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered",
								"(" + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + ")"));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", args[6]));
						break;
					case "locname":
						((QuestObjectReachLocation) o).setName(QuestChatManager.translateColor(args[6]));
						QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ObjectRegistered", args[6]));
						break;
					case "type":
						SimpleQuestObject ob = null;
						switch (args[6].toUpperCase())
						{
							case "BREAK_BLOCK":
								ob = new QuestObjectBreakBlock(Material.GRASS, (short) 0, 1);
								break;
							case "CONSUME_ITEM":
								ob = new QuestObjectConsumeItem(new ItemStack(Material.BREAD), 1);
								break;
							case "DELIVER_ITEM":
								ob = new QuestObjectDeliverItem(CitizensAPI.getNPCRegistry().getById(0), new ItemStack(Material.APPLE), 1);
								break;
							case "KILL_MOB":
								ob = new QuestObjectKillMob(EntityType.ZOMBIE, 1, null);
								break;
							case "REACH_LOCATION":
								ob = new QuestObjectReachLocation(new Location(Bukkit.getWorld("world"), 0, 0, 0), 0,
										I18n.locMsg("EditorMessage.DefaultLocation"));
								break;
							case "TALK_TO_NPC":
								ob = new QuestObjectTalkToNPC(CitizensAPI.getNPCRegistry().getById(0));
								break;
							default:
								return;
						}
						if (ob != null)
						{
							q.getStage(stage - 1).getObjects().set(obj - 1, ob);
							QuestChatManager.info(sender, I18n.locMsg("EditorMessage.ChangeObject"));
						}
						break;
				}
				QuestEditorManager.editQuestObject(sender, stage, obj);
		}
	}

	// /mq e edit reward [type] [value]
	// /mq e edit command [counter] [value]...
	private static void editReward(Quest q, Player sender, String[] args)
	{
		if (args.length == 4)
		{
			switch (args[3].toLowerCase())
			{
				case "money":
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.MoneyAmount"));
					break;
				case "exp":
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.ExpAmount"));
					break;
				case "item":
					EditorListenerHandler.registerGUI(sender, "reward");
					return;
			}
			EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit reward " + args[3]));
			return;
		}
		else
			if (args.length == 5)
			{
				switch (args[3].toLowerCase())
				{
					case "money":
						double money = q.getQuestReward().getMoney();
						try
						{
							money = Integer.parseInt(args[4]);
						}
						catch (NumberFormatException e)
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
							break;
						}
						q.getQuestReward().setMoney(money);
						break;
					case "exp":
						int exp = q.getQuestReward().getExp();
						try
						{
							exp = Integer.parseInt(args[4]);
						}
						catch (NumberFormatException e)
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
							break;
						}
						q.getQuestReward().setExp(exp);
						break;
					case "fp":
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.FriendPoint"));
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit reward fp " + Integer.parseInt(args[4])));
						return;
					case "command":
						QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterCommand"));
						EditorListenerHandler.register(sender,
								new EditorListenerObject(ListeningType.STRING, "mq e edit reward command " + Integer.parseInt(args[4])));
						return;
				}
				QuestEditorManager.editQuest(sender);
			}
			else
				if (args.length >= 6)
				{
					switch (args[3].toLowerCase())
					{
						case "fp":
							int npc = 0;
							int fp = 0;
							try
							{
								npc = Integer.parseInt(args[4]);
								fp = Integer.parseInt(args[5]);
							}
							catch (NumberFormatException e)
							{
								QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
								QuestEditorManager.editQuest(sender);
								return;
							}
							q.getQuestReward().getFp().put(npc, fp);
							break;
						case "command":
							String cmd = "";
							int index = Integer.parseInt(args[4]);
							for (int i = 5; i <= args.length; i++)
							{
								cmd += args[i];
								if (!(i == args.length - 1))
									cmd += " ";
							}
							if (!cmd.equals(""))
								q.getQuestReward().getCommands().set(index, cmd);
							break;
					}
					QuestEditorManager.editQuest(sender);
				}
	}

}
