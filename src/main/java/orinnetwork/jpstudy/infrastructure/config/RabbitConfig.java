package orinnetwork.jpstudy.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String POST_EXCHANGE_NAME = "post.exchange";
    public static final String FILTER_QUEUE_NAME = "filter.queue";
    public static final String POST_CREATED_ROUTING_KEY = "post.created";

    public static final String DEAD_EXCHANGE_NAME = "dead.exchange";
    public static final String DEAD_QUEUE_NAME = "dead.queue";
    public static final String DEAD_ROUTING_KEY = "dead.key";

    @Bean
    public TopicExchange postExchange() {
        return new TopicExchange(POST_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange deadExchange() {
        return new TopicExchange(DEAD_EXCHANGE_NAME);
    }

    @Bean
    public Queue filterQueue() {
        return QueueBuilder.durable(FILTER_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DEAD_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DEAD_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deadQueue() {
        return new Queue(DEAD_QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue filterQueue, TopicExchange postExchange) {
        return BindingBuilder.bind(filterQueue)
                .to(postExchange)
                .with(POST_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding deadBinding(Queue deadQueue, TopicExchange deadExchange) {
        return BindingBuilder.bind(deadQueue)
                .to(deadExchange)
                .with(DEAD_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}