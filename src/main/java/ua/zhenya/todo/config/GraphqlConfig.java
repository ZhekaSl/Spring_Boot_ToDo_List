package ua.zhenya.todo.config;


import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import ua.zhenya.todo.config.coercing.LocalDateCoercing;
import ua.zhenya.todo.config.coercing.LocalDateTimeCoercing;
import ua.zhenya.todo.config.coercing.LocalTimeCoercing;

@Configuration
public class GraphqlConfig {

    @Bean
    public GraphQLScalarType localDateTimeScalar() {
        return new GraphQLScalarType.Builder()
                .name("LocalDateTime")
                .description("LocalDateTime scalar")
                .coercing(new LocalDateTimeCoercing())
                .build();
    }

    @Bean
    public GraphQLScalarType localDateScalar() {
        return new GraphQLScalarType.Builder()
                .name("LocalDate")
                .description("LocalDate scalar")
                .coercing(new LocalDateCoercing())
                .build();
    }

    @Bean
    public GraphQLScalarType localTimeScalar() {
        return new GraphQLScalarType.Builder()
                .name("LocalTime")
                .description("LocalTime scalar")
                .coercing(new LocalTimeCoercing())
                .build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiring -> wiring
                .scalar(localDateTimeScalar())
                .scalar(localDateScalar())
                .scalar(localTimeScalar())
                .build();
    }
}
