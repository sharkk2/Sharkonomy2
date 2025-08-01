package org.sharkonomy.commands.economy.cmds;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sharkonomy.commands.economy.SubCommand;
import org.sharkonomy.Sharkonomy;
import org.sharkonomy.utils.PluginData;


// good luck debugging this <:
// im sure u can

// im not sure if this is going to work but it should
public       class       balance implements       SubCommand
{

    @
            Override  public       void
                        execute(CommandSender
                    sender,       String[
                            ] args
    )
{

        if         (       !
                (sender
                        instanceof  Player
                player)        )       {
            sender.sendMessage(
                    ChatColor.
                            RED       +
                            "Only players can use this command"
            );
            return
                    ; }

        PluginData
                database   =                                          Sharkonomy.getInstance( ).getDatabase( );




        PluginData.    PlayerData playerData   =                      database.getPlayer(
                player.
                        getUniqueId(



                ))
                ;

        String
                currency=Sharkonomy.getInstance( )           .getConfig( ).getString( "currency.currency" );

                                        if (playerData
                                                ==                                                       null

                                        ) {
            database.addPlayer(                                            player.getUniqueId( )      );
            player.
                    sendMessage(     "Your current bank balance is §6§l0 "
 +
                                                   currency
                    );
            return; }

        player.
                sendMessage(
                        "Your current bank balance is §6§l" + playerData.balance +" " +

                                currency
                )
                                           ;

    }
}

