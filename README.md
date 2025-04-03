# AdvancedChatCRX
**Created by Criex just for free using :3**

Plugin for chat control and filtering

⚙ **Features:**

 - Cooldown on sending messages (Anti spam)

 - Filtering ads (For example www.example.me)

 - Filtering characters.

 - The "@" sign has been replaced with "?" to avoid various bugs with placeholders. (Optional)

 - Easy setup in configuration



🌐 **Permissions:**
 - criex.chat.nochat - enable cooldowns (default: true)
 - criex.chat.nocommand.chat - bypass cooldowns (defualt: op)


⛓ **Config:**

```
###------AdvancedChatCRX------###

###----------by Criex---------###

# Created just for free use :3 #



# Maximum number of characters in a message

max-message-length: 180



# Message sending cooldown in milliseconds

chat-cooldown-ms: 3000



# Enable replace "@" to "?"

replace-at-symbol: true



# Others:

max-spam-count: 3

max-command-spam-count: 2



# Messages for the player

messages:

  # No perrmission to send messages

  no-permission: "&cYou do not have permission to send messages."



  # Max symbols limit

  max-length: "&e> &cMaximum message length is &7{length} &ccharacters!"



  # Anti-advertising message
  advertisement-prohibited: "&cAdvertising is prohibited!"

  # If player sending messages to fast
  spam-warning: "&ePlease, do not spam.

  # Cooldown message
  cooldown: "&7> &cWait &b{seconds} &csec."

  # Anti command spam message
  command-spam-warning: "&ePlease, do not spam!"
```

🔨 **Build:**

If you want to rewrite the plugin in your own way - no problem. Download the source code with the command `git clone https://github.com/CrieXD1337/AdvancedChatCRX.git `Then, after making changes, compile the plugin with the command `mvn package`, after compilation the .jar file will be in `~/target/...`

⛔ **Bugs:**

If you encounter a bug, please write about it in https://github.com/CrieXD1337/AdvancedChatCRX/issues
