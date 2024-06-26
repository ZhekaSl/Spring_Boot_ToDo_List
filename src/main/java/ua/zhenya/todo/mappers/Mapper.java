package ua.zhenya.todo.mappers;

public interface Mapper<F, T> {
    T map(F object);

    default T map(F fromObject, T toObject) {
        return toObject;
    }
}
