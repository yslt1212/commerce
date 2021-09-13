package de.sep.server.adapter;

import de.sep.server.errors.NotFoundException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class TelegramBotAdapter extends TelegramLongPollingBot {

    DoubleAuthAdapter doubleAuthAdapter = new DoubleAuthAdapter();

    public TelegramBotAdapter() throws SQLException {
    }

    @Override
    public String getBotUsername() {
        return "sepGruppeJ_bot";
    }

    @Override
    public String getBotToken() {
        return "1669218825:AAGtJ2UjvpUAsUAi5mwXAREd577MT8r1J7M";
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());
        String command = update.getMessage().getText();
        SendMessage message = new SendMessage();
         try {
             if(command.contains("/code")) {
                 String tel = command.substring(6, command.length());

                 int code = doubleAuthAdapter.getCodeById(doubleAuthAdapter.createCode(tel, 1));

                 message.setText(String.format("Congrats thats ur code Mapping: tel: %s , code: %d", tel, code));

             }

         } catch (SQLException | NotFoundException e) {
             message.setText("Something went wrong. We are Sorry please try again later");
             e.printStackTrace();
         }

         message.setChatId(Long.toString(update.getMessage().getChatId()));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
