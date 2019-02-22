package server_package;

import java.util.ArrayList;
import client_package.ClientThread;

public class Command {
	private ClientThread client;
	private boolean operator;
	private String commandArgs;
	
	public Command(ClientThread c, String rawCommand) {
		this.client = c;
		commandArgs = rawCommand;
		/*
		ArrayList<String> commandMatches = RegexParser.matches("(PRIVMSG) (.+)", rawCommand);
		if(commandMatches.isEmpty()) {
			commandType = CommandType.INVALIDCOMMAND;
			commandArgs = "Invalid command";
		}
		else {
			//client = commandMatches.get(1);
			commandType = CommandType.PRIVMSG;
			commandArgs = commandMatches.get(2);
		}
		*/
	}
	
	public String response() {
		return commandArgs;
	}
}


