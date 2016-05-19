package com.upitis.chat.client;

import com.upitis.chat.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by upitis on 13.04.16.
 */
public class BotClient extends ConsoleClient {
    private static volatile int botNumber = 0;

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    public ClientSocketThread getSocketThread() {
        return new BotSocketThread(this);
    }

    protected boolean shouldSentTextFromConsole() {
        return false;
    }

    @Override
    public String getUserName() {
        if (botNumber == 99) botNumber = 0;
        return String.format("date_bot_%d", botNumber++);
    }

    public class BotSocketThread extends ClientSocketThread {

        public BotSocketThread(BotClient botClient) {
            super((Client)botClient);
        }

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String[] ss = message.split("\\: ");
            if (ss.length > 1) {
                String userName = ss[0];
                String text = ss[1];
                Calendar calendar = new GregorianCalendar();
                String answer = null;
                switch (text) {
                    case "дата":
                        answer = new SimpleDateFormat("d.MM.YYYY").format(calendar.getTime());
                        break;
                    case "день":
                        answer = new SimpleDateFormat("d").format(calendar.getTime());
                        break;
                    case "месяц":
                        answer = new SimpleDateFormat("MMMM").format(calendar.getTime());
                        break;
                    case "год":
                        answer = new SimpleDateFormat("YYYY").format(calendar.getTime());
                        break;
                    case "время":
                        answer = new SimpleDateFormat("H:mm:ss").format(calendar.getTime());
                        break;
                    case "час":
                        answer = new SimpleDateFormat("H").format(calendar.getTime());
                        break;
                    case "минуты":
                        answer = new SimpleDateFormat("m").format(calendar.getTime());
                        break;
                    case "секунды":
                        answer = new SimpleDateFormat("s").format(calendar.getTime());
                        break;
                }
                if (answer != null) {
                    sendTextMessage(String.format("Информация для %s: %s", userName, answer));
                }
            }
        }
    }
}
