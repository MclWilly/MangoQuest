package me.Cutiemango.MangoQuest.commands.edtior;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject;
import me.Cutiemango.MangoQuest.editor.EditorListenerHandler;
import me.Cutiemango.MangoQuest.editor.QuestEditorManager;
import me.Cutiemango.MangoQuest.editor.EditorListenerObject.ListeningType;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.QuestGUIManager;
import me.Cutiemango.MangoQuest.model.Quest;
import me.Cutiemango.MangoQuest.model.QuestStage;
import me.Cutiemango.MangoQuest.model.RequirementType;
import me.Cutiemango.MangoQuest.questobjects.QuestObjectBreakBlock;
import me.Cutiemango.MangoQuest.questobjects.SimpleQuestObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerObject;
import me.Cutiemango.MangoQuest.model.QuestTrigger.TriggerType;

public class CommandNewObject
{
	
	public static void execute(Quest q, Player sender, String[] args)
	{
		if (!QuestEditorManager.checkEditorMode(sender, true))
			return;
		switch (args[2])
		{
			case "req":
				addRequirements(q, sender, args);
				break;
			case "evt":
				addEvent(q, sender, args);
				break;
			case "stage":
				addStage(q, sender, args);
				break;
			case "object":
				addObject(q, sender, args);
				break;
			case "reward":
				addReward(q, sender, args);
				break;
		}
	}

	private static void addEvent(Quest q, Player sender, String[] args)
	{
		if (args.length == 3)
		{
			QuestEditorManager.selectTriggerType(sender);
			return;
		}
		else
			if (args.length == 4)
			{
				TriggerType type = TriggerType.valueOf(args[3]);
				if (type.equals(TriggerType.TRIGGER_STAGE_START) || type.equals(TriggerType.TRIGGER_STAGE_FINISH))
				{
					QuestEditorManager.selectStage(sender, type);
					return;
				}
				QuestEditorManager.selectTriggerObject(sender, type, 0);
				return;
			}
			else
				if (args.length == 5)
				{
					TriggerType type = TriggerType.valueOf(args[3]);
					if (type.equals(TriggerType.TRIGGER_STAGE_START) || type.equals(TriggerType.TRIGGER_STAGE_FINISH))
					{
						QuestEditorManager.selectTriggerObject(sender, type, Integer.parseInt(args[4]));
						return;
					}
					TriggerObject obj = TriggerObject.valueOf(args[4]);
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING,
							"mq e edit evt " + q.getTriggers().size() + " " + type.toString() + " " + obj.toString()));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
					return;
				}
				else
					if (args.length == 6)
					{
						TriggerType type = TriggerType.valueOf(args[3]);
						TriggerObject obj = TriggerObject.valueOf(args[5]);
						if (type.equals(TriggerType.TRIGGER_STAGE_START) || type.equals(TriggerType.TRIGGER_STAGE_FINISH))
						{
							EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit evt "
									+ q.getTriggers().size() + " " + type.toString() + " " + Integer.parseInt(args[4]) + " " + obj.toString()));
							QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
							return;
						}
					}
	}

	@SuppressWarnings("unchecked")
	private static void addRequirements(Quest q, Player sender, String[] args)
	{
		RequirementType t = RequirementType.valueOf(args[3]);
		if (t.hasIndex() && args.length == 5)
		{
			int index = Integer.parseInt(args[4]);
			switch (t)
			{
				case QUEST:
					QuestEditorManager.selectQuest(sender, "mq e edit req " + t.toString() + " " + index);
					((List<String>) q.getRequirements().get(t)).add("");
					break;
				case SCOREBOARD:
				case NBTTAG:
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit req " + t.toString() + " " + index));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
					((List<String>) q.getRequirements().get(t)).add("");
					break;
				case ITEM:
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit req " + t.toString() + " " + index));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.RightClick"));
					((List<ItemStack>) q.getRequirements().get(t)).add(new ItemStack(Material.GRASS));
					break;
				default:
					break;
			}
		}
		else
		{
			switch (t)
			{
				case LEVEL:
				case MONEY:
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e edit req " + t.toString()));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterValue"));
					break;
				default:
					break;
			}
		}
	}

	// /mq e addnew stage
	private static void addStage(Quest q, Player sender, String[] args)
	{
		List<SimpleQuestObject> l = new ArrayList<>();
		l.add(new QuestObjectBreakBlock(Material.GRASS, (short) 0, 1));
		q.getStages().add(q.getStages().size(), new QuestStage(null, null, l));
		QuestChatManager.info(sender, I18n.locMsg("EditorMessage.StageCreated"));
		QuestChatManager.info(sender, I18n.locMsg("EditorMessage.StageCreated2"));
		QuestChatManager.info(sender, I18n.locMsg("EditorMessage.StageCreated3"));
		QuestEditorManager.editQuestStages(sender);
	}

	// /mq e addnew object [stage]
	private static void addObject(Quest q, Player sender, String[] args)
	{
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
				return;
			}
			q.getStage(stage - 1).getObjects().add(q.getStage(stage - 1).getObjects().size(),
					new QuestObjectBreakBlock(Material.GRASS, (short) 0, 1));
			QuestEditorManager.selectObjectType(sender, stage, q.getStage(stage - 1).getObjects().size());
		}
	}

	// /mq e addnew reward item
	// /mq e addnew reward fp [npc] [value]
	// /mq e addnew reward command [value] ...
	private static void addReward(Quest q, Player sender, String[] args)
	{
		if (args.length == 4)
		{
			switch (args[3].toLowerCase())
			{
				case "item":
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.ITEM, "mq e edit reward item " + q.getQuestReward().getItems().size()));
					q.getQuestReward().getItems().add(new ItemStack(Material.DIRT));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.RightClick"));
					return;
				case "fp":
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e addnew reward fp"));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.FriendPoint"));
					return;
				case "command":
					EditorListenerHandler.register(sender, new EditorListenerObject(ListeningType.STRING, "mq e addnew reward command"));
					QuestGUIManager.openInfo(sender, I18n.locMsg("EditorMessage.EnterCommand"));
					return;
			}
		}
		else
			if (args.length >= 5)
			{
				switch (args[3])
				{
					case "fp":
						String[] sp = args[4].split(":");
						try
						{
							q.getQuestReward().getFp().put(Integer.parseInt(sp[0]), Integer.parseInt(sp[1]));
						}
						catch (NumberFormatException e)
						{
							QuestChatManager.error(sender, I18n.locMsg("EditorMessage.WrongFormat"));
							QuestEditorManager.editQuest(sender);
							return;
						}
						QuestEditorManager.editQuest(sender);
						return;
					case "command":
						String s = "";
						for (int i = 4; i < args.length; i++)
						{
							s += args[i];
							if (!(i == args.length - 1))
								s += " ";
						}
						if (!s.equals(""))
							q.getQuestReward().addCommand(s);
						QuestEditorManager.editQuest(sender);
						return;
				}
			}
	}

}
