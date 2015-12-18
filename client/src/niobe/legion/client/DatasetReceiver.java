package niobe.legion.client;

import java.util.List;

public interface DatasetReceiver<T>
{
    void set(T dataset);

    default void setAll(List<T> datasets)
    {
        datasets.forEach(this::set);
    }

    void clear();

    void remove(T dataset);

    default void removeAll(List<T> datasets)
    {
        datasets.forEach(this::remove);
    }
}