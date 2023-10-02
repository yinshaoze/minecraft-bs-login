package asia.yinshaoze.login;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Banshanlogin extends JavaPlugin implements Listener {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1){
            List<String> list = new ArrayList<>();
            list.add("l");
            list.add("r");
            return list;
        }
        return null;
    }

    private List<String> playerNameList = new ArrayList<>(); //没有登录的玩家

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("半山模块 登录分支 载入成功");
        getServer().getPluginManager().registerEvents(this,this);
        getCommand("login").setExecutor(this);
        getCommand("login").setTabCompleter(this);
        getLogger().info("半山模块 命令 注册成功");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    //加入服务器
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        playerNameList.add(e.getPlayer().getName());

        if (getConfig().contains("Data."+e.getPlayer().getName())){
            e.getPlayer().sendMessage(ChatColor.YELLOW+"欢迎回来");
            e.getPlayer().sendMessage(ChatColor.YELLOW+"请使用/login l 密码 命令来登录");
        }else{
            e.getPlayer().sendMessage(ChatColor.YELLOW+"欢迎来到服务器");
            e.getPlayer().sendMessage(ChatColor.YELLOW+"请使用/login r 密码 命令来注册");
        }
    }

    //移动
    @EventHandler
    public void onPlayerMove(BlockBreakEvent e) { //PlayerMoveEvent移动方法
        if(playerNameList.contains(e.getPlayer().getName())) {//判断玩家登录状态, 是则取消事件
            e.setCancelled(true);
        }
    }

    //破坏方块
    @EventHandler
    public void onPlayerBlock(PlayerMoveEvent e) { //PlayerMoveEvent破坏方块
        if(playerNameList.contains(e.getPlayer().getName())) {//判断玩家登录状态, 是则取消事件
            e.setCancelled(true);
        }
    }

    //放置方块
    @EventHandler
    public void onPlayerBlock(BlockPlaceEvent e) { //BlockPlaceEvent放置方块
        if(playerNameList.contains(e.getPlayer().getName())) {//判断玩家登录状态, 是则取消事件
            e.setCancelled(true);
        }
    }

    //命令
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MD5Utils md5 = new MD5Utils();

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (!playerNameList.contains(player.getName())){
                player.sendMessage(ChatColor.YELLOW+"您已登录");
                return true;
            }

            if(!player.hasPermission("bs.login.use")){
                player.sendMessage(ChatColor.YELLOW+"你没有bs.login.use权限！");
                return true;
            }

            if (args.length == 2){
                if (args[0].equals("l")){
                    if (getConfig().contains("Data."+player.getName())){
                        if (md5.encrypByMd5(args[1]).equals(getConfig().getString("Data."+player.getName()))){
                            player.sendMessage(ChatColor.YELLOW+"登录成功");
                            playerNameList.remove(player.getName());//登录成功记得删除
                            return true;
                        }
                    }else{
                        player.sendMessage(ChatColor.YELLOW+"此ID还未注册");
                        return true;
                    }
                }

                if (args[0].equals("r")){
                    if (getConfig().contains("Data."+player.getName())){
                        player.sendMessage(ChatColor.YELLOW+"此ID已经注册过了");
                        return true;
                    }else{
                        getConfig().set("Data."+player.getName(),md5.encrypByMd5(args[1]));
                        saveConfig();
                        player.sendMessage(ChatColor.YELLOW+"注册成功");
                        playerNameList.remove(player.getName());//登录成功记得删除
                        return true;
                    }
                }
            }
        }
        return true;
    }
}
