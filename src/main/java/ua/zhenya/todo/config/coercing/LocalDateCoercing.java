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
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateCoercing implements Coercing<LocalDate, String> {
    @Override
    public @Nullable LocalDate parseValue(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseValueException {
        return LocalDate.parse((String) input);
    }

    @Override
    public @Nullable LocalDate parseLiteral(@NotNull Value<?> input, @NotNull CoercedVariables variables, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseLiteralException {
        return LocalDate.parse(((StringValue) input).getValue());
    }

    @Override
    public @Nullable String serialize(@NotNull Object dataFetcherResult, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingSerializeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);
        return ((LocalDate) dataFetcherResult).format(formatter);
    }


}
