package niobe.legion.client.gui.databinding;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FxDatasetColumn<C> extends TableColumn<FxDatasetWrapper, C>
{
	public FxDatasetColumn(final String key, final String name)
	{
		this(key, name, null);
	}

	public FxDatasetColumn(final String key, final String name, boolean editable)
	{
		this(key, name, editable, null);
	}

	public FxDatasetColumn(final String key,
						   final String name,
						   final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
	{
		super(name);
		this.setCellValueFactory(column -> (ObservableValue<C>) column.getValue().getProperty(key));

		this.setCellFactory(param ->  new FxDatasetCell<FxDatasetWrapper, C>(getSelectedProperty));
	}

	public FxDatasetColumn(final String key,
						   final String name,
						   boolean editable,
						   final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
	{
		this(key, name, getSelectedProperty);

		this.setEditable(true);
	}
}
