package com.myeye.gameserver.server;

import java.util.concurrent.BlockingQueue;

import com.myeye.gameserver.core.domain.GameMessage;
import com.myeye.gameserver.core.service.GameMessageService;

/**
 * The Game Message Logger.
 * 
 * @author Marcello de Sales (marcello.desales@gmail.com)
 *
 */
public class GameMessageLogger implements Runnable {

    /**
     * The queue responsible for logging in the message.
     */
    private BlockingQueue<GameMessage> loggerQueue;

    public GameMessageLogger(BlockingQueue<GameMessage> logger) {
        this.loggerQueue = logger;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Logger Thread");
        while(true) {
            try {
                GameMessage message = this.loggerQueue.take();
                GameMessageService.log(message, System.out);

            } catch (InterruptedException e) {
            }
        }
    }

}
