package br.net.fabiozumbi12.PixelVip;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PVUtil {
	private PixelVip plugin;

	public PVUtil(PixelVip plugin){
		this.plugin = plugin;
	}
	
	public String toColor(String str){
    	return str.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2"); 
    }
	
	public long getNowMillis(){
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
    }
	
	public long dayToMillis(Long days){
		return TimeUnit.DAYS.toMillis(days);
	}
	
	public long millisToDay(String millis){
		return TimeUnit.MILLISECONDS.toDays(new Long(millis));
	}
	
	public long millisToDay(Long millis){
		return TimeUnit.MILLISECONDS.toDays(millis);
	}
	
	public void sendHoverKey(CommandSender sender, String key){
		if (plugin.getPVConfig().getBoolean(true, "configs.clickKeySuggest") && sender instanceof Player){
			TextComponent text = new TextComponent();			
			text.setText(plugin.getUtil().toColor(plugin.getPVConfig().getLang("timeKey")+key+" "+plugin.getPVConfig().getLang("hoverKey")));
			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(plugin.getUtil().toColor(plugin.getPVConfig().getLang("hoverKey")))));
			text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, 
					plugin.getPVConfig().getString("/usekey ", "configs.spigot.clickSuggest").replace("{key}", key)));			
			sender.spigot().sendMessage(text);
    	} else {
    		sender.sendMessage(plugin.getUtil().toColor(plugin.getPVConfig().getLang("timeKey")+key));
    	}
	}
	public String genKey(int length) {
	    char[] chartset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		Random random = new SecureRandom();
	    char[] result = new char[length];
	    for (int i = 0; i < result.length; i++) {
	        int randomCharIndex = random.nextInt(chartset.length);
	        result[i] = chartset[randomCharIndex];
	    }
	    return new String(result);
	}
	
	public boolean sendVipTime(CommandSender src, String UUID, String name) {	
		List<String[]> vips = plugin.getPVConfig().getVipInfo(UUID);
		if (vips.size() > 0){
			src.sendMessage(plugin.getUtil().toColor(plugin.getPVConfig().getLang("_pluginTag","vipInfoFor")+name+":"));
			src.sendMessage(plugin.getUtil().toColor("&b---------------------------------------------"));
			vips.stream().filter(v->v.length == 5).forEach((vipInfo)->{
				String time = plugin.getUtil().millisToMessage(new Long(vipInfo[0]));
				if (plugin.getPVConfig().isVipActive(vipInfo[1], UUID.toString())){
					time = plugin.getUtil().millisToMessage(new Long(vipInfo[0])-plugin.getUtil().getNowMillis());
				}
		    	src.sendMessage(plugin.getUtil().toColor(plugin.getPVConfig().getLang("timeLeft")+time));
		    	src.sendMessage(plugin.getUtil().toColor(plugin.getPVConfig().getLang("timeGroup")+vipInfo[1]));		
		    	src.sendMessage(plugin.getUtil().toColor(plugin.getPVConfig().getLang("timeActive")+plugin.getPVConfig().getLang(vipInfo[3])));	
		    	src.sendMessage(plugin.getUtil().toColor("&b---------------------------------------------"));
			});			
			return true;
		} else {
			src.sendMessage(plugin.getUtil().toColor(plugin.getPVConfig().getLang("_pluginTag","playerNotVip")));	
			return false;
		}
	}
	
	public String millisToMessage(long millis){		
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		long hour = TimeUnit.MILLISECONDS.toHours(millis-TimeUnit.DAYS.toMillis(days));
		long min = TimeUnit.MILLISECONDS.toMinutes((millis-TimeUnit.DAYS.toMillis(days))-TimeUnit.HOURS.toMillis(hour));
		StringBuilder msg = new StringBuilder();
		if (days > 0){
			msg.append("&6"+days+plugin.getPVConfig().getLang("days")+", ");
		}
		if (hour > 0 ){
			msg.append("&6"+hour+plugin.getPVConfig().getLang("hours")+", ");
		}
		if (min > 0){
			msg.append("&6"+min+plugin.getPVConfig().getLang("minutes")+", ");
		}
		try{
			msg = msg.replace(msg.lastIndexOf(","), msg.lastIndexOf(",")+1, ".").replace(msg.lastIndexOf(","), msg.lastIndexOf(",")+1, plugin.getPVConfig().getLang("and"));
		} catch(StringIndexOutOfBoundsException ex){
			return plugin.getPVConfig().getLang("lessThan");
		}		
		return msg.toString();
	}
	
	public String expiresOn(Long millis){
		Date date = new Date(millis);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");		
		return sdf.format(date);
	}
}
