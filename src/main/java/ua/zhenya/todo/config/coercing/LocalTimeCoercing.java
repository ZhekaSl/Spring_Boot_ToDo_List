package ua.zhenya.todo.config.coercing;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeCoercing implements Coercing<LocalTime, String> {
    @Override
    public @Nullable LocalTime parseValue(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseValueException {
        return LocalTime.parse((String) input);
    }

    @Override
    public @Nullable LocalTime parseLiteral(@NotNull Value<?> input, @NotNull CoercedVariables variables, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseLiteralException {
        return LocalTime.parse(((StringValue) input).getValue());
    }

    @Override
    public @Nullable String serialize(@NotNull Object dataFetcherResult, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingSerializeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", locale);
        return ((LocalTime) dataFetcherResult).format(formatter);
    }


}
