package cuina.legion.client.gui;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FxDatasetColumn<C> extends TableColumn<FxDatasetWrapper, C>
{
	public FxDatasetColumn(final String key, final String name)
	{
		super(name);
		this.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FxDatasetWrapper, C>, ObservableValue<C>>()
		{
			@Override
			public ObservableValue<C> call(
					javafx.scene.control.TableColumn.CellDataFeatures<FxDatasetWrapper, C> column)
			{
				return (ObservableValue<C>) column.getValue().getProperty(key);
			}
		});
	}
}
