package cuina.legion.client.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;

public class FxDatasetColumn<C> extends TableColumn<FxDatasetWrapper, C>
{
	public FxDatasetColumn(final String key, final String name)
	{
		super(name);
		this.setCellValueFactory(column -> (ObservableValue<C>) column.getValue().getProperty(key));
	}
}
