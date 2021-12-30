package me.htrewrite.client.command.commands;

import me.htrewrite.client.HTRewrite;
import me.htrewrite.client.command.Command;
import me.htrewrite.client.util.AuthSession;
import me.htrewrite.client.util.PostRequest;
import org.apache.commons.lang3.StringEscapeUtils;

public class CapeClaimCommand extends Command {
    public CapeClaimCommand() {
        super("cape-claim", "<player> <serial-key>", "Claims a cape with the following serial-key to the player.");
    }

    @Override
    public void call(String[] args) {
        if(args.length < 2) {
            sendInvalidUsageMessage();
            return;
        }

        HTRewrite.apiCallsQueue.submit(() -> {
            sendMessage("&eClaiming cape...");
            String response = PostRequest.urlEncodedPostRequest("https://aurahardware.eu/api/cape/claim_cape.php", "" +
                    "user=" + AuthSession.I_USERNAME +
                    "&pass=" + AuthSession.I_PASSWORD +
                    "&hwid=" + AuthSession.I_HWID+
                    "&player="+ StringEscapeUtils.escapeHtml4(args[0])+
                    "&serial_key="+StringEscapeUtils.escapeHtml4(args[1]));

            sendMessage(response.contentEquals("1")?
                    "&aCape claimed successfully!":
                    "&cThe serial-key does not exists or it has already been used!");
        });
    }
}