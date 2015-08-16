package niobe.legion.client.gui.databinding;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FxDatasetCell<S, T> extends TableCell<S, T>
{
	private final CheckBox checkBox;

	private ObservableValue<Boolean> booleanProperty;

	private final DateTimeFormatter dateFormatter     = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
	private final DateTimeFormatter timeFormatter     = DateTimeFormatter.ofPattern("HH:mm");

	public FxDatasetCell()
	{
		this(null);
	}

	public FxDatasetCell(final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty)
	{
		this.getStyleClass().add("check-box-table-cell");

		this.checkBox = new CheckBox();

		// by default the graphic is null until the cell stops being empty
		this.setGraphic(null);

		this.setSelectedStateCallback(getSelectedProperty);
	}

	@Override
	protected void updateItem(T item, boolean empty)
	{
		super.updateItem(item, empty);

		if (item instanceof Boolean)
		{
			if (empty)
			{
				setText(null);
				setGraphic(null);
			} else
			{
				setGraphic(checkBox);

				if (booleanProperty instanceof BooleanProperty)
				{
					checkBox.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
				}
				ObservableValue<?> obsValue = getSelectedProperty();
				if (obsValue instanceof BooleanProperty)
				{
					booleanProperty = (ObservableValue<Boolean>) obsValue;
					checkBox.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);
				}

				checkBox.disableProperty()
						.bind(Bindings.not(getTableView().editableProperty().and(getTableColumn().editableProperty())
														 .and(editableProperty())));
			}
		} else if (item instanceof LocalDate)
		{
			if (empty)
			{
				setText(null);
				setGraphic(null);
			} else
			{
				LocalDate date = (LocalDate) item;
				setText(dateFormatter.format(date));
				setGraphic(null);
			}
		} else if (item instanceof LocalDateTime)
		{
			if (empty)
			{
				setText(null);
				setGraphic(null);
			} else
			{
				LocalDateTime date = (LocalDateTime) item;
				setText(dateTimeFormatter.format(date));
				setGraphic(null);
			}
		} else if (item instanceof LocalTime)
		{
			if (empty)
			{
				setText(null);
				setGraphic(null);
			} else
			{
				LocalTime date = (LocalTime) item;
				setText(timeFormatter.format(date));
				setGraphic(null);
			}
		} else
		{
			if (empty)
			{
				setText(null);
				setGraphic(null);
			} else
			{
				setText(item != null ? item.toString() : null);
				setGraphic(null);
			}
		}
	}

	// --- selected state callback property
	private ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallback =
			new SimpleObjectProperty<Callback<Integer, ObservableValue<Boolean>>>(this, "selectedStateCallback");

	/**
	 * Property representing the {@link Callback} that is bound to by the
	 * CheckBox shown on screen.
	 */
	public final ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallbackProperty()
	{
		return selectedStateCallback;
	}

	/**
	 * Sets the {@link Callback} that is bound to by the CheckBox shown on screen.
	 */
	public final void setSelectedStateCallback(Callback<Integer, ObservableValue<Boolean>> value)
	{
		selectedStateCallbackProperty().set(value);
	}

	/**
	 * Returns the {@link Callback} that is bound to by the CheckBox shown on screen.
	 */
	public final Callback<Integer, ObservableValue<Boolean>> getSelectedStateCallback()
	{
		return selectedStateCallbackProperty().get();
	}

	private ObservableValue<?> getSelectedProperty()
	{
		return getSelectedStateCallback() != null ? getSelectedStateCallback().call(getIndex()) :
			   getTableColumn().getCellObservableValue(getIndex());
	}
}
