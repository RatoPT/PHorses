package me.Rato.PHorses;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import net.minecraft.server.v1_6_R2.EntityHorse;
import net.minecraft.server.v1_6_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.v1_6_R2.org.bouncycastle.util.encoders.Base64;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftHorse;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PHorses extends JavaPlugin implements Listener{

	public static PHorses plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public Server server = this.getServer();
	public String pluginTag = "§8[§cMINEPT§8]§a ";
	
	@Override
	public void onEnable(){
		//AddRecipes();		
		PluginManager pm = this.getServer().getPluginManager();
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " Version " + pdffile.getVersion() + " Has been Enabled!");
		this.saveDefaultConfig();
		pm.registerEvents(this, this);
		ShapedRecipe g = new ShapedRecipe(PokeBall("","")).shape("GGG", "GBG", "GGG").setIngredient('G', Material.BOOK_AND_QUILL).setIngredient('B', Material.GOLD_INGOT);
		getServer().addRecipe(g);
	}
	
	@Override
	public void onDisable(){
		PluginDescriptionFile pdffile = this.getDescription();
		getServer().clearRecipes();
		this.logger.info(pdffile.getName() + " Has been Disabled!");
	}
	
	public void AddRecipes(){
		String owner = "";
		String horseName = "";
		ItemStack pokeBall = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) pokeBall.getItemMeta();
        bm.setTitle("§6§lCavalo Portatil");
        bm.setAuthor("§bMINEPT");
        bm.setLore(Arrays.asList("§aAqui Dentro pode estar um Cavalo!","§cDono: " + owner,"§bNome do Cavalo: " + horseName));
        pokeBall.setItemMeta(bm);
		
		ShapedRecipe g = new ShapedRecipe(pokeBall).shape("GGG", "GBG", "GGG").setIngredient('G', Material.BOOK_AND_QUILL).setIngredient('B', Material.GOLD_INGOT);
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(commandLabel.equalsIgnoreCase("ph")){
				if(args.length >= 1){

					if(args[0].equalsIgnoreCase("getpokeball")){
						if(player.isOp()){
							player.getInventory().addItem(PokeBall("",""));
							return true;
						} else
							player.sendMessage("§cVoce nao tem permissao para fazer isto!");
							return false;
					}
					
					if(args[0].equalsIgnoreCase("changeowner")){
						if(args[1] != null){
							if(player.getItemInHand() != null && isPokeball(player.getItemInHand()) && !isPokeballEmpty(player.getItemInHand())){
								setHorseOwner(args[1], player.getItemInHand());
								return true;
							}
						}else
							player.sendMessage(pluginTag + "Modo de Usar: §7/ph changeowner <nick>");
							return false;
					}
					
					if(args[0].equalsIgnoreCase("help")){
						if(!player.isOp()){
							player.sendMessage(new String[]{
									"§a===== §2Lista de Comandos de Portable Horses §a=====",
									"  §a/ph changeowner §2- Modifica o Dono",
									"  §a/ph help §2- Mostra esta mensagem",
									"§a===========================================",
									"§a           Plugin Feito Por §cRato_PT",
									"§a==========================================="
							});
							return true;
						} else {
							player.sendMessage(new String[]{
									"§a===== §2Lista de Comandos de Portable Horses §a=====",
									"  §a/ph changeowner <nick>§2- Modifica o Dono",
									"  §a/ph help §2- Mostra esta mensagem",
									"  §a/ph getpokeball §2- §c§lApenas para OP",
									"§a===========================================",
									"§a           Plugin Feito Por §cRato_PT",
									"§a==========================================="
							});
							return true;
						}
						
					}
				}
				player.sendMessage(pluginTag + "Escreve §b/ph help§a para veres a lista de comandos");
				return false;
			}
		} else
			sender.sendMessage(pluginTag + "Apenas Jogadores podem fazer isto!");
		return false;
	}
	
	
	public ItemStack PokeBall(String owner, String horseName){
		ItemStack pokeBall = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) pokeBall.getItemMeta();
        bm.setTitle("§6§lCavalo Portatil");
        bm.setAuthor("§bMINEPT");
        bm.setLore(Arrays.asList("§aAqui Dentro pode estar um Cavalo!","§cDono: " + owner,"§bNome do Cavalo: " + horseName));
        pokeBall.setItemMeta(bm);
		return pokeBall;
	}
	
	//tag.setString("owner", "nome_do_dono");
	//https://github.com/andrepl/PortableHorses/blob/master/v1_6_R3/src/main/java/com/norcode/bukkit/portablehorses/v1_6_R3/NMSHandler.java
	//https://github.com/andrepl/PortableHorses/blob/master/Plugin/src/main/java/com/norcode/bukkit/portablehorses/PortableHorses.java
	
	public boolean hasPermission(ItemStack pokeball, Player player){
		if(pokeball != null){
			if(player.isOp())
				return true;
			if(getHorseOwner(pokeball) == "")
				return true;
			if(getHorseOwner(pokeball) == player.getDisplayName())
				return true;
		}
		return false;
	}
	
	public String getHorseOwner(ItemStack pokeball){
		return pokeball.getItemMeta().getLore().get(1).substring(8);
	}
	
	public void setHorseOwner(String name, ItemStack pokeball){
		BookMeta bm = (BookMeta) pokeball.getItemMeta();
		pokeball.getItemMeta().getLore().set(1,"§cDono: " + name);
	}
	
	public void setHorseName(String name, ItemStack pokeball){
		BookMeta bm = (BookMeta) pokeball.getItemMeta();
		pokeball.getItemMeta().getLore().set(2,"§bNome do Cavalo: " + name);
	}
	
	public boolean isPokeball(ItemStack pokeball){
		if(pokeball != null){
			if(pokeball.getType().equals(Material.WRITTEN_BOOK)){
				BookMeta bm = (BookMeta) pokeball.getItemMeta();
				if(bm.getTitle().equals("§6§lCavalo Portatil") && bm.getAuthor().equals("§bMINEPT")){
					if(bm.getLore().get(0).equals("§aAqui Dentro pode estar um Cavalo!")){
							return true;
						}
					}
				}
			}
		return false;
	}
	
	public boolean isSpawnedHorseOwner(Player player, Horse theHorse){
		if(theHorse.getOwner().equals(player)){
			return true;
		}
		return false;
	}
	
	public boolean isPokeballEmpty(ItemStack pokeball){
		if(getHorseOwner(pokeball) != "")
			return true;
		if(getHorseOwner(pokeball) != null)
			return true;
		
		return false;
	}
	
	public String getHorseString(Horse theHorse){
		NBTTagCompound tag = new NBTTagCompound();
		EntityHorse eh = ((CraftHorse) theHorse).getHandle();
		eh.b(tag);
		byte[] data = NBTCompressedStreamTools.a(tag);
		String oleee_string = new String(Base64.encode(data));
		return oleee_string;
	}
	
	public void spawnHorse(String string, Location spawnLoc, String owner/*, Inventory inv*/){
		
		Horse theHorse = (Horse) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.HORSE);
		EntityHorse eh = ((CraftHorse) theHorse).getHandle();
		byte[] data = Base64.decode(string);
		NBTTagCompound tag = NBTCompressedStreamTools.a(data);
		tag.setString("owner", owner);
		eh.a(tag);
		/*ItemStack[] invc = inv.getContents();
		theHorse.getInventory().setContents(invc);*/
	}

	public String LoadHorse(ItemStack pokeball){
		BookMeta bm = (BookMeta) pokeball.getItemMeta();
		return bm.getPage(1);
	}
	
	public String LoadHorseInv(ItemStack pokeball){
		BookMeta bm = (BookMeta) pokeball.getItemMeta();
		return bm.getPage(2);
	}
	
	public void saveToBook(String horse, String horseInv, ItemStack pokeball){
		BookMeta bm = (BookMeta) pokeball.getItemMeta();
		bm.addPage(horse);
		bm.addPage(horseInv);
		pokeball.setItemMeta(bm);
	}
	
	public static String InventoryToString (Inventory invInventory)
    {
        String serialization = invInventory.getSize() + ";";
        for (int i = 0; i < invInventory.getSize(); i++)
        {
            ItemStack is = invInventory.getItem(i);
            if (is != null)
            {
                String serializedItemStack = new String();
               
                String isType = String.valueOf(is.getType().getId());
                serializedItemStack += "t@" + isType;
               
                if (is.getDurability() != 0)
                {
                    String isDurability = String.valueOf(is.getDurability());
                    serializedItemStack += ":d@" + isDurability;
                }
               
                if (is.getAmount() != 1)
                {
                    String isAmount = String.valueOf(is.getAmount());
                    serializedItemStack += ":a@" + isAmount;
                }
               
                Map<Enchantment,Integer> isEnch = is.getEnchantments();
                if (isEnch.size() > 0)
                {
                    for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
                    {
                        serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
                    }
                }
               
                serialization += i + "#" + serializedItemStack + ";";
            }
        }
        return serialization;
    }
   
    public static Inventory StringToInventory (String invString)
    {
        String[] serializedBlocks = invString.split(";");
        String invInfo = serializedBlocks[0];
        Inventory deserializedInventory = Bukkit.getServer().createInventory(null, Integer.valueOf(invInfo));
       
        for (int i = 1; i < serializedBlocks.length; i++)
        {
            String[] serializedBlock = serializedBlocks[i].split("#");
            int stackPosition = Integer.valueOf(serializedBlock[0]);
           
            if (stackPosition >= deserializedInventory.getSize())
            {
                continue;
            }
           
            ItemStack is = null;
            Boolean createdItemStack = false;
           
            String[] serializedItemStack = serializedBlock[1].split(":");
            for (String itemInfo : serializedItemStack)
            {
                String[] itemAttribute = itemInfo.split("@");
                if (itemAttribute[0].equals("t"))
                {
                    is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    createdItemStack = true;
                }
                else if (itemAttribute[0].equals("d") && createdItemStack)
                {
                    is.setDurability(Short.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("a") && createdItemStack)
                {
                    is.setAmount(Integer.valueOf(itemAttribute[1]));
                }
                else if (itemAttribute[0].equals("e") && createdItemStack)
                {
                    is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                }
            }
            deserializedInventory.setItem(stackPosition, is);
        }
       
        return deserializedInventory;
    }
		
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		Bukkit.broadcastMessage("bla1");
		Player player = event.getPlayer();
		if(player.getItemInHand() != null && isPokeball(player.getItemInHand())){
			Bukkit.broadcastMessage("bla2");
			if(isPokeballEmpty(player.getItemInHand())){
				Bukkit.broadcastMessage("bla3");
				if(event.getRightClicked() instanceof Horse){
					Bukkit.broadcastMessage("bla4");
					
					Horse horse = (Horse) event.getRightClicked();
					ItemStack pokeball = player.getItemInHand();
					
					if(isSpawnedHorseOwner(player, horse)){
						Bukkit.broadcastMessage("bla5");
						event.setCancelled(true);
						
						BookMeta bm = (BookMeta) pokeball.getItemMeta();
						bm.getLore().clear();
						if(horse.getCustomName() == null){
							bm.setLore(Arrays.asList("§aAqui Dentro pode estar um Cavalo!","§cDono: " + player.getName(),"§bNome do Cavalo: " + "Horse"));
						} else
							bm.setLore(Arrays.asList("§aAqui Dentro pode estar um Cavalo!","§cDono: " + player.getName(),"§bNome do Cavalo: " + horse.getCustomName()));
						//setHorseOwner(player.getName(), pokeball);
						//setHorseName(horse.getCustomName(), pokeball);
						pokeball.setItemMeta(bm);
						saveToBook(getHorseString(horse), InventoryToString(horse.getInventory()), pokeball);
						horse.remove();
						
					} else
						player.sendMessage(pluginTag + "Este Cavalo Não é Teu");
				} else
					player.sendMessage(pluginTag + "Apenas Podes Capturar Cavalos!");
			} else
				player.sendMessage(pluginTag + "Este Livro ja Contem um cavalo");
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Bukkit.broadcastMessage("blabla1");
		Player player = event.getPlayer();
		if(player.getItemInHand()!= null){
			if(isPokeball(player.getItemInHand())){
				if(getHorseOwner(player.getItemInHand()) != "" && getHorseOwner(player.getItemInHand()) != null){
					Bukkit.broadcastMessage("blabla2");
					if(hasPermission(player.getItemInHand(), player)){
						Bukkit.broadcastMessage("blabla3");
						if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
							Bukkit.broadcastMessage("blabla4");
							ItemStack pokeball = player.getItemInHand();
							event.setCancelled(true);
							spawnHorse(LoadHorse(pokeball), player.getLocation(), getHorseOwner(pokeball)/*, StringToInventory(LoadHorseInv(pokeball))*/);
							player.getInventory().remove(pokeball);
							player.getInventory().addItem(PokeBall("",""));
						}
					} else
						player.sendMessage(pluginTag + "Não tens permissão para fazer spawn deste cavalo!");
				}
			}
		}
	}
	
}
