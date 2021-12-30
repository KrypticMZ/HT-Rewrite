package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.util.AuthSession;
import me.htrewrite.client.util.PostRequest;
import org.apache.commons.lang3.StringEscapeUtils;

public class CapeUseCommand extends Command {
    public CapeUseCommand() {
        super("cape-use", "<cape-id>", "Use a cape you have on the list!");
    }

    @Override
    public void call(String[] args) {
        if(args.length < 1) {
            sendInvalidUsageMessage();
            return;
        }

        HTRewrite.apiCallsQueue.submit(() -> {
            sendMessage("&eCommunicating the API...");

            String response = PostRequest.urlEncodedPostRequest("https://aurahardware.eu/api/cape/use_cape.php", "" +
                    "user="+ AuthSession.I_USERNAME+
                    "&pass="+AuthSession.I_PASSWORD+
                    "&hwid="+AuthSession.I_HWID+
                    "&cape_id="+ StringEscapeUtils.escapeHtml4(args[0]));
            HTRewrite.INSTANCE.getCapes().refresh();
            sendMessage(response.contentEquals("1")?
                    "&aNow you are using cape id: " + args[0]:
                    "&cYou don't have that cape!");
        });
    }
}