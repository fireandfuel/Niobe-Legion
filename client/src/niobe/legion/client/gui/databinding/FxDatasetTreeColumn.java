package niobe.legion.client.gui.databinding;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class FxDatasetTreeColumn<C> extends TreeTableColumn<FxDatasetWrapper, C>
{
    public FxDatasetTreeColumn(final String key, final String name)
    {
        this(key, name, null);
    }

    public FxDatasetTreeColumn(final String key, final String name, final boolean editable)
    {
        this(key, name, editable, null);
    }

    public FxDatasetTreeColumn(final String key, final String name,
                               final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
    {
        super(name);
        this.setCellValueFactory(column -> (ObservableValue<C>) column.getValue().getValue().getProperty(key));

        this.setCellFactory(param -> new FxDatasetTreeCell<FxDatasetWrapper, C>(getSelectedProperty));
    }

    public FxDatasetTreeColumn(final String key, final String name, final boolean editable,
                               final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
    {
        this(key, name, getSelectedProperty);

        this.setEditable(editable);
    }
}
