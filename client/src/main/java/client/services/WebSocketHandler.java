package client.services;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;


public class WebSocketHandler {
    private StompSession session;

    public WebSocketHandler(String URL) {
        session = connect(URL);
    }

    /**
     * Connect to a server and start a websocket STOMP session.
     *
     * @param URL URL of server to connect to
     * @return a StompSession object containing information about the connection
     */
    private StompSession connect(String URL) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(URL, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    /**
     * Subscribes a user to a new message destination
     *
     * @param dest     Which destination to subscribe to
     * @param consumer Consumer defining the behaviour/logic to take place when messages are received
     */
    public <T> void registerForMessages(String dest, Class<T> clazz, Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return clazz;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    /**
     * Send a message to a specific destination
     *
     * @param dest destination to send the object to
     * @param o    Object payload
     */
    public void send(String dest, Object o) {
        session.send(dest, o);
    }

    /**
     * Closes the websocket connection.
     */
    public void close() {
        this.session.disconnect();
    }
}
